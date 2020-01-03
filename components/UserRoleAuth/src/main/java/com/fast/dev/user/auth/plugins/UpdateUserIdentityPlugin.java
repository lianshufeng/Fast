package com.fast.dev.user.auth.plugins;


import com.fast.dev.user.auth.dao.RoleDao;
import com.fast.dev.user.auth.dao.UserIdentityUpdateListDao;
import com.fast.dev.user.auth.domain.UserRole;
import com.fast.dev.user.auth.event.RoleUpdateEvent;
import com.fast.dev.user.auth.model.RequestRoleParm;
import com.fast.dev.user.auth.model.RoleModel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Log
@Component
public class UpdateUserIdentityPlugin implements ApplicationListener<RoleUpdateEvent> {


    @Autowired
    private UserIdentityUpdateListDao userIdentityUpdateListDao;

    @Autowired
    private RoleDao roleDao;

    //线程池
    private ExecutorService threadPool = Executors.newFixedThreadPool(5);

    //匹配清空用户令牌的方法
    private Set<String> updateIdentityFromRole = new HashSet<String>() {{
        add("updateRole");
        add("removeRole");

        add("setRoleIdentity");
    }};


    private Set<String> updateIdentityFromUser = new HashSet<String>() {{
        add("addUserRole");
        add("removeUserRole");
    }};


    //用户字段
    private ThreadLocal<File> currentUserIds = new ThreadLocal<>();


    @Override
    public void onApplicationEvent(RoleUpdateEvent event) {
        RoleUpdateEvent.Source source = event.getSource();
        if (source == null) {
            return;
        }

        if (source.getCallType() == RoleUpdateEvent.CallType.Before) {
            if (updateIdentityFromRole.contains(source.getMethodName())) {
                Object o = source.getParm()[0];
                String roleName = null;
                if (o instanceof RoleModel) {
                    roleName = ((RoleModel) o).getRoleName();
                } else {
                    roleName = String.valueOf(o);
                }
                cacheUserFromRole(roleName);
            } else if (updateIdentityFromUser.contains(source.getMethodName())) {
                cacheUserFromUser((String[]) source.getParm()[1]);
            }
        } else if (source.getCallType() == RoleUpdateEvent.CallType.After) {
            submitUpdateUser();
        }
    }

    /**
     * 将当前线程里缓存的数据提交到db中进行修改
     */
    private void submitUpdateUser() {
        final File file = this.currentUserIds.get();
        if (file == null) {
            return;
        }
        this.currentUserIds.remove();

        //同步需要更新的用户身份
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                updateUser(file);
            }
        });
    }


    /**
     * 缓存角色中的所有用户
     */
    @SneakyThrows
    private void cacheUserFromRole(String roleName) {
        @Cleanup OutputStream outputStream = createFileOutStreamToCurrentThread();


        //分页查询
        Pageable pageable = PageRequest.of(0, 100);
        readUserPageRecord(roleName, outputStream, pageable);

    }


    /**
     * 翻页查询数据
     *
     * @param roleName
     * @param outputStream
     * @param pageable
     */
    private void readUserPageRecord(String roleName, OutputStream outputStream, Pageable pageable) {
        Page<UserRole> userRole = this.roleDao.listRoleUser(roleName, pageable);
        if (userRole == null) {
            return;
        }
        writeUserInfo(outputStream, userRole.getContent().stream().map((it) -> {
            return it.getUser().getId();
        }).collect(Collectors.toSet()));
        if (userRole.hasNext()) {
            readUserPageRecord(roleName, outputStream, pageable.next());
        }
    }

    /**
     * 缓存用户
     *
     * @param userIds
     */
    @SneakyThrows
    private void cacheUserFromUser(String[] userIds) {
        @Cleanup OutputStream outputStream = createFileOutStreamToCurrentThread();
        writeUserInfo(outputStream, new HashSet<>(Arrays.asList(userIds)));
    }


    /**
     * 向管道写用户id
     *
     * @param outputStream
     * @param userIds
     */
    @SneakyThrows
    private void writeUserInfo(OutputStream outputStream, Set<String> userIds) {
        if (userIds == null || userIds.size() == 0) {
            return;
        }

        for (String userId : userIds) {
            outputStream.write((userId + "\n").getBytes());
        }
    }


    /**
     * 打开一个临时文件的输出流，并把这个文件保存到当前的线程里
     *
     * @return
     */
    @SneakyThrows
    private OutputStream createFileOutStreamToCurrentThread() {
        File file = File.createTempFile(this.getClass().getSimpleName(), ".tmp");
        this.currentUserIds.set(file);
        return new FileOutputStream(file);
    }


    @SneakyThrows
    private void updateUser(final File file) {
        @Cleanup("delete") File f = file;
        @Cleanup FileReader fr = new FileReader(file);
        @Cleanup BufferedReader bf = new BufferedReader(fr);
        @Cleanup AppendUpdateUser appendUpdateUser = new AppendUpdateUser();
        String userId;
        // 按行读取字符串
        while ((userId = bf.readLine()) != null) {
            appendUpdateUser.append(userId);
        }
    }


    /**
     * 增加需要更新的用户
     */
    class AppendUpdateUser {
        private List<String> userCache = new ArrayList<>();

        public synchronized void append(String userId) {
            userCache.add(userId);
            if (userCache.size() > 50) {
                updateToDB();
            }
        }

        /**
         * 关闭
         */
        public void close() {
            updateToDB();
        }


        /**
         * 更新到数据库里
         */
        private synchronized void updateToDB() {
            Set<String> userIds = new HashSet<>();
            while (this.userCache.size() > 0) {
                userIds.add(this.userCache.remove(0));
            }
            userIdentityUpdateListDao.addUser(userIds);
        }

    }

}

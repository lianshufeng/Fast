package demo.simple.controller;


import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.token.service.ResourceTokenService;
import com.fast.dev.data.token.service.impl.ResourceTokenServiceImpl;
import com.mongodb.DBRef;
import demo.simple.dao.UserDao;
import demo.simple.service.UserService;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;


    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private ResourceTokenService resourceTokenService;


    private long resourceTokenCount = 0l;

    @RequestMapping("rt")
    @SneakyThrows
    public Object resourceToken(final String name, @RequestParam(defaultValue = "1000") Integer size) {

        long time = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(100);
        final CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            final long inxdex = i;
            es.execute(() -> {
                ResourceTokenService.Token token = this.resourceTokenService.token(name);
                resourceTokenCount++;

                ResourceTokenServiceImpl.LockTokenImpl lockToken = (ResourceTokenServiceImpl.LockTokenImpl) token;
                System.out.println("name : " + name + " , index :  " + inxdex + " , count : " + resourceTokenCount + " , " + lockToken.getCounter());

                token.close();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        es.shutdownNow();

        long costTime = System.currentTimeMillis() - time;
        return new HashMap<>() {{
            put("cost", costTime);
            put("average", (double) size / costTime * 1000);

        }};
    }


    @RequestMapping("dbref")
    public Object dbref(@RequestParam(defaultValue = "key") String key, @RequestParam(defaultValue = "tb") String collectionName, @RequestParam(defaultValue = "5f0834beb322117a96e3d0ed") String id) {
        Document document = new Document(key, new DBRef(collectionName, new ObjectId(id)));
        return this.dbHelper.toJson(document);
    }


    @RequestMapping("time")
    public Object time() {
        return new HashMap<String, Object>() {{
            put("time", new Date().getTime());
        }};
    }


    @RequestMapping("user")
    public Object user(String name) {
        String userId = this.userService.save(name);
        return new HashMap<String, Object>() {{
            put("time", new Date().getTime());
            put("name", name);
            put("userId", userId);
        }};
    }

    @RequestMapping("find")
    public Object find(String name, int r) {
        return this.userService.findUser(name, r);
    }


    @RequestMapping("query")
    public Object query(String name) {
        return this.userService.query(name);
    }

    @RequestMapping("count")
    public Object count(String name) {
        return this.userService.count(name);
    }


    @RequestMapping("remove")
    public Object remove(String name, int r) {
        return this.userService.remove(name, r);
    }


    @RequestMapping("get")
    public Object get(String name, int r) {
        return this.userService.get(name, r);
    }


    @RequestMapping("list")
    public Object list(String name, int page, int size) {
        return this.userService.list(name, page, size);
    }

    @RequestMapping("update")
    public Object update(String name, int r, int o) {
        return this.userService.update(name, r, o);
    }


    @RequestMapping("transactional")
    public Object transactional(@RequestParam(defaultValue = "10") Integer n) {
        return this.userService.transactional(n);
    }


    @RequestMapping("dataUpdate")
    public Object dataUpdate(String id, String name) {
        return this.userService.dataUpdate(id, name);
    }


}

package demo.simple.controller;

import com.fast.dev.core.util.result.InvokerResult;
import demo.simple.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;

@RestController
public class TestController {

    private final static String UserName = "xiaofeng";


    @Autowired
    private UserService userService;

    @RequestMapping("test")
    public Object test() {
        return new HashMap<String, Object>() {{
            put("t", System.currentTimeMillis());
        }};
    }


    @RequestMapping("save")
    public Object save() {
        return InvokerResult.success(this.userService.save(UUID.randomUUID().toString()));
    }


    @RequestMapping("findExtend")
    public Object findExtendId(Long id) {
        return InvokerResult.success(this.userService.findExtendId(id));
    }


    @RequestMapping("findJpa")
    public Object findJpaId(Long id) {
        return InvokerResult.success(this.userService.findJpaId(id));
    }


    @RequestMapping("listAge")
    public Object listAge(int age, Pageable pageable) {
        return InvokerResult.success(this.userService.listAge(age, pageable));
    }


    @RequestMapping("findByUserNameAndId")
    public Object queryJpa(String userName, Long id) {
        return InvokerResult.success(this.userService.findByUserNameAndId(userName, id));
    }

    @RequestMapping("updateAge")
    public Object updateAge(int oldAge, int newAge) {
        return InvokerResult.success(this.userService.updateAge(oldAge, newAge));
    }

    @RequestMapping("group")
    public Object group() {
        return InvokerResult.success(this.userService.group());
    }

}

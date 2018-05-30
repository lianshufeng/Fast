package demo.simple.controller;


import demo.simple.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


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
        return this.userService.update(name,r,o);
    }

}

package boot;

import boot.interfaces.FeignService;
import boot.interfaces.UserService;
import com.fast.dev.core.boot.info.ActuatorInfoController;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@RestController
public class Info extends ActuatorInfoController {


    @Autowired
    private FeignService feignService;

    @Autowired
    private UserService userService;


    @Value("${server.port}")
    private int port;

    public Info() {
    }

    @RequestMapping("time")
    public Object time(@RequestParam(value = "name") String userName) {
        return new HashMap<String, Object>() {{
            put("time", new Date().getTime());
            put("name", userName);
            put("port", port);
        }};
    }


    @RequestMapping("test")
    public Object test(@RequestParam(value = "name") String userName) {
        return feignService.time(userName);
    }


    @RequestMapping("user")
    public Object user() {
        return this.userService.user();
    }



}

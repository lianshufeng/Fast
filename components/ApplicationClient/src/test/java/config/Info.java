package config;

import com.fast.dev.core.boot.info.ActuatorInfoController;
import feign.interfaces.FeignService;
import feign.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@RestController
public class Info extends ActuatorInfoController {



    @Value("${server.port}")
    private int port;


    @Override
    public Object info() {
        return "1111";
    }
}

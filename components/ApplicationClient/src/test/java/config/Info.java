package config;

import com.fast.dev.core.boot.info.ActuatorInfoController;
import feign.interfaces.FeignService;
import feign.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@RefreshScope
@RestController
public class Info extends ActuatorInfoController {



    @Value("${hi}")
    private String hi;



    @RequestMapping("hi")
    public Object info() {
        return this.hi;
    }
}

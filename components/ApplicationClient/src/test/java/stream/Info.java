package stream;

import com.fast.dev.core.boot.info.ActuatorInfoController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

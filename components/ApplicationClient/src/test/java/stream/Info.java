package stream;

import com.fast.dev.core.boot.info.ActuatorInfoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stream.model.User;

import java.util.Date;

@RefreshScope
@RestController
public class Info extends ActuatorInfoController {


    @Value("${hi}")
    private String hi;

    @RequestMapping("hi")
    public Object info() {
        return this.hi;
    }


    @Autowired
    private UserMessageStream userMessageStream;


    @RequestMapping("send")
    public String send(String value) {
        User u = new User(value, 10, "15123241353l", new Date().getTime());
        this.userMessageStream.publish(u);
        return "finish";
    }

}

package stream;

import com.fast.dev.core.boot.info.ActuatorInfoController;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stream.model.User;
import stream.template.OuputStreamTemplate;

import javax.annotation.Resource;
import java.util.Date;

@RefreshScope
@RestController
@EnableBinding(OuputStreamTemplate.class)
public class Info extends ActuatorInfoController {


    @Resource
    @Output(OuputStreamTemplate.name)
    MessageChannel messageChannel;


    @RequestMapping("send")
    public String send(String value) {
        User u = new User(value, new Date().getTime());
        OuputStreamTemplate.publish(messageChannel, u);
        return "finish";
    }

}

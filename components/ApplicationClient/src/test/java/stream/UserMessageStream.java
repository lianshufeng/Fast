package stream;

import com.fast.dev.acenter.stream.MessageStream;
import com.fast.dev.core.util.JsonUtil;
import net.minidev.json.JSONUtil;
import org.springframework.stereotype.Component;
import stream.model.User;

@Component
public class UserMessageStream extends MessageStream<User> {
    @Override
    public String name() {
        return "test.user";
    }

    @Override
    public void subscribe(User entity) {

        try {
            System.out.println(JsonUtil.toJson(entity));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

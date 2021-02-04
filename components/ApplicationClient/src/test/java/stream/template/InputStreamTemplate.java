package stream.template;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 消息模版
 */
public interface InputStreamTemplate {



    public static String name="InputStream";

    /**
     * 订阅
     *
     * @return
     */
    @Input(name)
    SubscribableChannel input();




}

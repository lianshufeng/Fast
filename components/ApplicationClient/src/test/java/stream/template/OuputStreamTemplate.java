package stream.template;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 消息模版
 */
public interface OuputStreamTemplate {


    public static String name = "OutputStream";

    @Output(name)
    MessageChannel messageChannel();


    /**
     * 发布
     *
     * @param
     */
    public static void publish(final MessageChannel messageChannel, Object body) {
        Message message = MessageBuilder.withPayload(body).build();
        messageChannel.send(message);
    }


}

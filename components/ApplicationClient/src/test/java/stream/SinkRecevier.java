package stream;

import com.netflix.discovery.converters.Auto;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;

//@EnableBinding(Sink.class)
@EnableBinding(Source.class)
public class SinkRecevier {

    @Autowired
    private ApplicationContext applicationContext;

    @StreamListener(Sink.INPUT)
    public void receive(Message<String> msg){
        System.out.println("Recevied:"+ msg.getPayload());
    }

}

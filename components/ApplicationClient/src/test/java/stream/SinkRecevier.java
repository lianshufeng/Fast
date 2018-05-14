package stream;

import com.netflix.discovery.converters.Auto;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.ApplicationContext;

@EnableBinding(Sink.class)
public class SinkRecevier {

    @Autowired
    private ApplicationContext applicationContext;

    @StreamListener(Sink.INPUT)
    public void receive(Object payload){
        System.out.println("Recevied:"+ payload+" "+applicationContext);
    }

}

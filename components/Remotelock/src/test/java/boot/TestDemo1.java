package boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestDemo1 extends  Demo {

    @Autowired
    private void init() throws Exception {
       run();
    }


}

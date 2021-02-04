package demo.simple.config;

import com.fast.dev.data.timer.config.TaskTimerConfiguration;
import com.fast.dev.data.timer.event.SimpleTaskTimerEvent;
import demo.simple.domain.TestTaskTimerTable1;
import demo.simple.domain.TestTaskTimerTable2;
import org.springframework.context.annotation.Configuration;

/**
 * 任务定时器的配置
 */
@Configuration
public class SimpleTaskTimerConfig extends TaskTimerConfiguration {

    @Override
    public TaskTimerItem[] register() {
        return new TaskTimerItem[]{
                TaskTimerItem.builder().taskTimerTable(TestTaskTimerTable1.class).taskTimerEvent((it) -> {
                    System.out.println(Thread.currentThread() + " TestTaskTimerTable1 -> " + it);
                }).build()
                ,
                new TaskTimerItem().setTaskTimerTable(TestTaskTimerTable2.class).setTaskTimerEvent(new SimpleTaskTimerEvent<TestTaskTimerTable2>() {
                    @Override
                    public void execute(TestTaskTimerTable2 it) {
                        System.out.println(Thread.currentThread() + " TestTaskTimerTable2 -> " + it);
                    }
                })
        };
    }
}

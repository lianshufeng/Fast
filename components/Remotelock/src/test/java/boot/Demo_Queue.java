package boot;

import com.fast.dev.component.remotelock.RemoteLock;
import com.fast.dev.component.remotelock.SyncToken;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
@Component
public class Demo_Queue {

    static Long count = 0l;


    @Autowired
    private RemoteLock remoteLock;

    CountDownLatch countDownLatch = null;

    public void run() throws Exception {

        long startTime = System.currentTimeMillis();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(maxTheadPool());

        countDownLatch = new CountDownLatch((int) maxRunCount());


        for (int i = 0; i < maxRunCount(); i++) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    runLock();
                }
            });
        }

        countDownLatch.await();
        fixedThreadPool.shutdownNow();

        log.info("count :" + count + ", class : " + this.getClass() + " , 每秒 : " + (((double) maxRunCount()) / ((System.currentTimeMillis() - startTime)) * 1000))  ;
    }

    private void runLock() {


        SyncToken lockToken = null;
        try {
            lockToken = remoteLock.queue("queue_token");
            if (lockToken != null) {
                inc();
                Thread.sleep(sleepTime());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (lockToken != null) {
                lockToken.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();
    }


    private  void inc() {
        count++;
        log.info(Thread.currentThread() + " inc : " + count);
    }

    /**
     * 每次执行延迟时间
     *
     * @return
     */
    public long sleepTime() {
        return 0;
    }

    ;

    /**
     * 运行次数
     *
     * @return
     */
    public long maxRunCount() {
        return 5000;
    }


    /**
     * 最大线程数
     *
     * @return
     */
    public int maxTheadPool() {
        return 30;
    }

}

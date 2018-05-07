package boot;

import com.fast.dev.component.remotelock.RemoteLock;
import com.fast.dev.component.remotelock.SyncToken;
import com.fast.dev.component.remotelock.conf.LockOption;
import com.fast.dev.component.remotelock.impl.RemoteLockZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Demo {

    static Long count = 0l;


    @Autowired
    private RemoteLock remoteLock;

    CountDownLatch countDownLatch = null;

    public void run() throws Exception {

        long startTime = System.currentTimeMillis();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(maxTheadPool());

        countDownLatch = new CountDownLatch((int) maxRunCount());


        for (int i = 0; i < maxRunCount(); i++) {
            final Integer index = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    runLock(index);
                }
            });
        }

        countDownLatch.await();
        fixedThreadPool.shutdownNow();

        System.out.println("count :" + count + ", class : " + this.getClass()+" , qps : " + ((System.currentTimeMillis()-startTime) / maxRunCount()));
    }

    private  void runLock(final Integer index) {
        SyncToken lockToken = null;
        try {
            long startTime = (System.currentTimeMillis());
            lockToken = remoteLock.lock("test");
            if (sleepTime() > 0) {
                Thread.sleep(sleepTime());
            }
            count++;
            System.out.println(Thread.currentThread() + " index:" + index + " time : " + (System.currentTimeMillis() - startTime) + " inc : " + count);
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
        return 1000;
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

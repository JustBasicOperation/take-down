package juc.ThreadPool;

import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TestExecutor {

    @Test
    public void test01(){
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> beep = scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("beep");
            }
        }, 1, 1, SECONDS);

        scheduledThreadPool.schedule(new Runnable() {
            @Override
            public void run() {
                beep.cancel(true);
            }
        },60, SECONDS);
        //等待打印结果
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

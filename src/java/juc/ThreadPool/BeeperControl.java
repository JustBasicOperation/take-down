package juc.ThreadPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

class BeeperControl {
    //创建一个ScheduledExecutorService实例
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void beepForAnHour() {

        //具体任务，这里是打印deep
        final Runnable beeper = new Runnable() {
            public void run() { System.out.println("beep"); }
        };

        //创建一个固定频率的周期任务
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);

        //在一个小时之后取消该周期任务
        scheduler.schedule(new Runnable() {
            public void run() { beeperHandle.cancel(true); }
        }, 60 * 60, SECONDS);
    }
}

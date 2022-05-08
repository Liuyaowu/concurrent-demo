package com.mobei.concurrent.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author liuyaowu
 * @date 2022/4/12 22:45
 * @remark
 */
public class ThreadCreateDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread t1 = new Thread("t1"){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        System.out.println("t1线程启动了");
        t1.join();
        System.out.println("t1 join after");

        FutureTask<Integer> futureTask = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(3000);
                return 3000;
            }
        });
        Thread t2 = new Thread(futureTask, "t1");
        t2.start();
        System.out.println(futureTask.get());
        System.out.println("---end");
    }
}

package com.mobei.concurrent.thread;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * @author liuyaowu
 * @date 2022/4/23 15:26
 * @remark
 */
@Slf4j
public class TestBatchBiased {
public static void main(String[] args) throws InterruptedException {
    Vector<Dog> list = new Vector<>();
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 39; i++) {
            Dog d = new Dog();
            list.add(d);
            synchronized (d) {
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }
        synchronized (list) {
            list.notify();
        }
    }, "t1");
    t1.start();

    Thread t2 = new Thread(() -> {
        synchronized (list) {
            try {
                list.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.debug("===============> ");
        for (int i = 0; i < 39; i++) {
            Dog d = list.get(i);
            log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
            log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
        }
    }, "t2");
    t2.start();

    Thread t3 = new Thread(() -> {
        LockSupport.park();
        log.debug("===============> ");
        for (int i = 0; i < 39; i++) {
            Dog d = list.get(i);
            log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
            log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
        }
    }, "t3");
    t3.start();
    t3.join();
    log.debug(ClassLayout.parseInstance(new Dog()).toPrintable(true));
}
public void testClear(){
    Object o = new Object();
    synchronized (o) {
    }
}
}

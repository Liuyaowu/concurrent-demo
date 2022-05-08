package com.mobei.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuyaowu
 * @date 2022/4/25 22:16
 * @remark
 */
@Slf4j
public class TestVolatile {

static boolean run = true;

public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(() -> {
        while (run) {
            log.error("running");
            System.out.println("xxx");
        }
        log.error("子线程结束了");
    });
    t.start();

    Thread.sleep(1);
    log.error("主线程停止");
    run = false;

}

}

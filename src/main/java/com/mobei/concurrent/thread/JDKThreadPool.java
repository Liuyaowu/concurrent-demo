package com.mobei.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyaowu
 * @date 2022/5/7 7:23
 * @remark
 */
@Slf4j
public class JDKThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                4,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10));

        for (int i = 0; i < 2; i++) {
            int finalI = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    log.error("循环1 >>>>>> 线程id{}, 第: {}次循环", Thread.currentThread().getName(), finalI);
                }
            });
        }

        for (int i = 0; i < 2; i++) {
            int finalI = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    log.error("循环2 >>>>>> 线程id{}, 第: {}次循环", Thread.currentThread().getName(), finalI);
                }
            });
        }


    }
}

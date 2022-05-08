package com.mobei.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static java.lang.Thread.sleep;

/**
 * @author liuyaowu
 * @date 2022/5/8 22:03
 * @remark
 */
@Slf4j
public class TestAqs {
    public static void main(String[] args) {
        MyLock lock = new MyLock();

        new Thread(() -> {
            lock.lock();
            log.error("上锁");
            lock.lock();
            try {
                log.error("locking");
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.error("unlocking");
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.error("locking");
            }finally {
                log.error("unlocking");
                lock.unlock();
            }
        }, "t2").start();

    }
}

/**
 * 自定义不可重入锁
 */
class MyLock implements Lock {

    /**
     * 独占锁
     */
    class MySync extends AbstractQueuedSynchronizer {

        /**
         * 获取锁
         *
         * @param arg
         * @return
         */
        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)) {
                // 设置当前拥有访问权的线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 释放锁
         *
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0);

            return true;
        }

        /**
         * 是否持有独占锁
         *
         * @return
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }

    }

    private MySync mySync = new MySync();

    /**
     * 加锁 不成功会进入等待队列
     */
    @Override
    public void lock() {
        mySync.acquire(1);
    }

    /**
     * 加锁,可打断
     * @throws InterruptedException
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        mySync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return mySync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mySync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        mySync.release(1);
    }

    @Override
    public Condition newCondition() {
        return mySync.newCondition();
    }

}

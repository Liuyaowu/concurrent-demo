package com.mobei.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuyaowu
 * @date 2022/5/5 22:23
 * @remark
 */
@Slf4j(topic = "c.TestThreadPool")
public class TestThreadPool {

    public static void main(String[] args) throws InterruptedException {
        ThreadPool threadPool = new ThreadPool(1, 1000, TimeUnit.MILLISECONDS, 1,
                (queue, task) -> {
                    queue.put(task);
                });

        for (int i = 0; i < 3; i++) {

            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.error("循环1  {}", j);
            });
        }

//        for (int i = 0; i < 2; i++) {
//            int j = i;
//            threadPool.execute(() -> log.error("循环2  {}", j));
//        }
    }

}

@Slf4j(topic = "c.ThreadPool")
class ThreadPool {

    /**
     * 任务队列
     */
    private BlockingQueue<Runnable> taskQueue;

    /**
     * 工作线程集合
     */
    private HashSet<Worker> workers = new HashSet();

    /**
     * 核心线程数
     */
    private int coreSize;

    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 超时时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 拒绝策略
     */
    private RejectPolicy rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity, RejectPolicy rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    public void execute(Runnable task) {
        // taskQueue是线程安全的,但是workers的操作不是线程安全的
        synchronized (workers) {
            if (workers.size() < coreSize) {
                // 工作线程数量少于核心线程数量

                // 交给工作线程执行
                Worker worker = new Worker(task);
                log.error("新增 worker {}, task {}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                // 工作线程数量达到了核心线程数量
                // 将任务添加到等待队列中
//                taskQueue.put(task);
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }

    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 1.如果task不为空则执行任务
            // 2.如果task执行完毕,再从任务队列中获取任务并执行
//            while (task != null || (task = taskQueue.take()) != null) {
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                try {
                    log.error("正在执行...  {}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }

            synchronized (workers) {
                log.error("worker执行完毕,准备移除...  {}", this);
                workers.remove(this);
            }
        }
    }
}

@Slf4j(topic = "c.BlockingQueue")
class BlockingQueue<T> {

    /**
     * 任务队列
     */
    private Deque<T> queue = new ArrayDeque<>();

    /**
     * 锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 生产者条件变量
     */
    private Condition fullWaitSet = lock.newCondition();

    /**
     * 消费者条件变量
     */
    private Condition emptyWaitSet = lock.newCondition();

    /**
     * 任务队列容量
     */
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 阻塞获取
     *
     * @return
     */
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞添加
     *
     * @param task
     */
    public void put(T task) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    log.error("等待加入任务队列{}", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.error("加入任务队列{}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时间的阻塞获取
     *
     * @param timeout
     * @param timeUnit
     * @return
     */
    public T poll(long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            // 将timeout统一转成纳秒
            long nanos = timeUnit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    // 防止假唤醒
                    if (nanos <= 0) {
                        return null;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时间的阻塞添加
     *
     * @param task
     * @param timeout
     * @param timeUnit
     * @return
     */
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity) {
                try {
                    log.error("等待加入任务队列{}", task);
                    if (nanos <= 0) {
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.error("加入任务队列{}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取队列大小
     *
     * @return
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            if (queue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                // 队列没满
                log.error("加入任务队列{}", task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}

@FunctionalInterface
interface RejectPolicy<T> {

    void reject(BlockingQueue<T> blockingQueue, T task);

}

package com.mobei.concurrent.thread;

/**
 * @author liuyaowu
 * @date 2022/4/14 7:46
 * @remark
 */
public class ThreadState {
public static void main(String[] args) {
    Thread newStateThread = new Thread("newStateThread") {
        @Override
        public void run() {
            System.out.println("running");
        }
    };


    Thread runnableStateThread = new Thread("runnableStateThread") {
        @Override
        public void run() {
            while (true){

            }
        }
    };
    runnableStateThread.start();

    Thread terminatedStateThread = new Thread("terminatedStateThread") {
        @Override
        public void run() {
            System.out.println("running");
        }
    };
    terminatedStateThread.start();

    Thread timedWaitingStateThread = new Thread("timedWaitingStateThread") {
        @Override
        public void run() {
            synchronized (ThreadState.class) {
                try {
                    // timed_waiting
                    Thread.sleep(1000000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    timedWaitingStateThread.start();

    Thread waitingStateThread = new Thread("waitingStateThread") {
        @Override
        public void run() {
            try {
                runnableStateThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    waitingStateThread.start();

    Thread blockedStateThread = new Thread("blockedStateThread") {
        @Override
        public void run() {
            synchronized (ThreadState.class) {
                try {
                    // timed_waiting
                    Thread.sleep(1000000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    blockedStateThread.start();
}
}

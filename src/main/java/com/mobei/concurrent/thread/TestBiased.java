package com.mobei.concurrent.thread;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * @author liuyaowu
 * @date 2022/4/21 7:25
 * @remark
 */
@Slf4j
public class TestBiased {
@SneakyThrows
public static void main(String[] args) {
// 添加JVM参数禁止延迟-XX:BiasedLockingStartupDelay=0
Dog dog = new Dog();
dog.hashCode();
// 调用hashCode禁掉了偏向锁,后3位是001
log.error(ClassLayout.parseInstance(dog).toPrintable());

synchronized (dog) {
    // 此时加的是轻量级锁,前62位指向的是LockRecord的地址,后2位是00表示加的轻量级锁
    log.error(ClassLayout.parseInstance(dog).toPrintable());
}
// 解锁以后恢复正常
log.error(ClassLayout.parseInstance(dog).toPrintable());
}
}

class Dog {
    static final Object obj = new Object();
    public static void m1() {

        synchronized( obj ) {
            m2();
        }
    }
    public static void m2() {
        synchronized( obj ) {
            m3();
        }
    }
    public static void m3() {
        synchronized( obj ) {}
    }



}
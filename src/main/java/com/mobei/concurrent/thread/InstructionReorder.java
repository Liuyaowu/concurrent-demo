package com.mobei.concurrent.thread;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author liuyaowu
 * @date 2022/4/26 8:08
 * @remark
 */

public class InstructionReorder {
    private volatile int field;

    public static void main(String[] args) {
        AtomicIntegerFieldUpdater fieldUpdater =
                AtomicIntegerFieldUpdater.newUpdater(InstructionReorder.class, "field");
        InstructionReorder i = new InstructionReorder();
        fieldUpdater.compareAndSet(i, 0, 10);
        // 修改成功:field = 10
        System.out.println(i.field);

        fieldUpdater.compareAndSet(i, 10, 20);
        // 修改成功:field = 20
        System.out.println(i.field);

        fieldUpdater.compareAndSet(i, 10, 30);
        // 修改失败:field = 20
        System.out.println(i.field);
    }
}

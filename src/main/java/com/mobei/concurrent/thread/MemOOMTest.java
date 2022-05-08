package com.mobei.concurrent.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyaowu
 * @date 2022/4/19 22:55
 * @remark
 */
public class MemOOMTest {
public static void main(String[] args) {
    long counter = 0;
    List list = new ArrayList<>();
    while (true) {
        list.add(new Object());
        System.out.println("创建了第 " + (++counter) + "个对象");
    }
}
}

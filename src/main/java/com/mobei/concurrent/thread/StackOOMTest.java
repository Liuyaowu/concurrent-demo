package com.mobei.concurrent.thread;

/**
 * @author liuyaowu
 * @date 2022/4/19 22:49
 * @remark
 */
public class StackOOMTest {

public static  long counter = 0;
public static void main(String[] args) {
    work();
}
public static void work() {
//    System.out.println("第" + (++counter) +"次调用");
    work();
}

}

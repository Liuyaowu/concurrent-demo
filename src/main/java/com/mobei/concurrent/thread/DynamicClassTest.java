package com.mobei.concurrent.thread;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author liuyaowu
 * @date 2022/4/19 22:07
 * @remark
 */
public class DynamicClassTest {

public static void main(String[] args) {
    long counter = 0;
    while (true) {
        System.out.println("创建了:" + (++counter) + "个Car子类对象");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Car.class);
        enhancer.setUseCache(false);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if ("run".equals(method.getName())) {
                    System.out.println("启动汽车之前先进行安全检查......");
                    return methodProxy.invokeSuper(o, objects);
                } else {
                    return methodProxy.invokeSuper(o, objects);
                }
            }
        });
        Car car = (Car) enhancer.create();
        car.run();
    }
}
static class Car {
    public void run() {
        System.out.println("汽车启动,开始行驶......");
    }
}

}

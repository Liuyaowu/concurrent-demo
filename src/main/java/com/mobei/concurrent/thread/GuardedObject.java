package com.mobei.concurrent.thread;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author liuyaowu
 * @date 2022/4/23 22:44
 * @remark
 */
class GuardedObject {
    private int id;
    private Object response;

    public GuardedObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Object get(long timeout) {
        synchronized (this) {
            // 条件不满足则等待
            long begin = System.currentTimeMillis();
            long passedTime = 0;
            while (response == null) {
                long waitTime = timeout - passedTime;
                try {
                    if (waitTime <= 0) {
                        break;
                    }
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }

    public void complete(Object response) {
        synchronized (this) {
            // 条件满足，通知等待线程
            this.response = response;
            this.notifyAll();
        }
    }
}

class Mailboxes{
    private static Map<Integer, GuardedObject> map = new Hashtable<>();
    private static int id = 1;
    private static synchronized int generateId() {
        return id++;
    }
    public static GuardedObject createGuardedObject() {
        GuardedObject go = new GuardedObject(generateId());
        map.put(go.getId(), go);
        return go;
    }
    public static Set<Integer> getIds() {
        return map.keySet();
    }


}
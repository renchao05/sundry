package com.renchao.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟线程池情况下 ThreadLocal导致的内存泄露
 *
 * @author ren_chao
 * @version 2024-10-21
 */
public class ThreadLocal_Demo {

    static ExecutorService pool = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        // 因为每使用一次pool的线程，都会新创建一个ThreadLocal，而且ThreadLocal一直都是被引用状态 list -> user -> threadLocal
        // 这样就导致pool线程的ThreadLocalMap无法清理，最终导致内存溢出
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(new User(pool));
            // new User(pool);
            System.out.println(i);
        }

        System.out.println("完成。。。。。");
    }

    static class User {

        // 虽然ThreadLocalMap有自动清理功能，但是因为User对象放在list中，没有被回收，导致threadLocal无法被回收
        // 这样以threadLocal为key的Entry就无法被清理
        // 如果可以确保任务完成后，threadLocal对象不再被引用，也可以不手动remove（ThreadLocalMap会自动清理）
        private ThreadLocal<Student> threadLocal = new ThreadLocal<>();

        public User(ExecutorService pool) {
            pool.execute(this::test);
        }
        private void test() {
            threadLocal.set(new Student());

            // threadLocal.remove();  // 线程使用完，主动删除
        }
    }

    static class Student {
        private byte[] bytes = new byte[10240];
    }
}

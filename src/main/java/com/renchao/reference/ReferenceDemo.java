package com.renchao.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * -Xms10m -Xmx10m
 *
 * @author ren_chao
 * @version 2024-10-21
 */
public class ReferenceDemo {
    public static void main(String[] args) {
        // 缓存
        Map<Integer, SoftReferenceStudent> map = new HashMap<>();
        // 收集被回收的Reference
        ReferenceQueue<Student> queue = new ReferenceQueue<>();

        for (int i = 0; i < 100; i++) {
            Student st = new Student();
            // 模拟放入缓存
            map.put(i, new SoftReferenceStudent(i, st, queue));
            SoftReferenceStudent ref;
            int count = 0;
            // 清理map中已经被回收的对象
            while ((ref = (SoftReferenceStudent) queue.poll()) != null) {
                map.remove(ref.key);
                count++;
            }

            System.out.println("回收数量：" + count + "  当前map大小：" + map.size());
        }

        System.out.println("完成=====");

    }


    static class SoftReferenceStudent extends SoftReference<Student> {
        public int key;

        // 当Student被回收后，SoftReferenceStudent对象会被加入到队列ReferenceQueue中
        public SoftReferenceStudent(int key, Student referent, ReferenceQueue<? super Student> q) {
            super(referent, q);
            this.key = key;
        }
    }

    static class Student {
        // 1M
        byte[] bytes = new byte[1024 * 1024];
    }
}


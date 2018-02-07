package com.openwudi.sa.thread;

import com.openwudi.sa.adb.ADB;

public class Daemon implements Runnable {
    public void run() {
        ADB adb = new ADB();
        while (true) {
            adb.keepAlive();
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

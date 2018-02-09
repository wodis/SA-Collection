package com.openwudi.sa.script;

import com.openwudi.sa.adb.ADB;

public abstract class Script {
    public String name = null;
    protected ADB adb;
    protected String imagePath;

    public Script(String name) {
        this.name = name;
    }

    public Script(String name, ADB adb, String imagePath) {
        this.name = name;
        this.adb = adb;
        this.imagePath = imagePath;
    }

    public abstract boolean run() throws Exception;
}

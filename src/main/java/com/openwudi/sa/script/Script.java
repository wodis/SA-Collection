package com.openwudi.sa.script;

public abstract class Script {
    public String name = null;

    public Script(String name) {
        this.name = name;
    }

    public abstract boolean run() throws Exception;
}

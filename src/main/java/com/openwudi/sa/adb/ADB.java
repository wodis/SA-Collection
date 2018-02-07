package com.openwudi.sa.adb;

import com.openwudi.sa.util.Command;
import com.openwudi.sa.util.Utils;

public class ADB {
    private static final String TAP = "adb %s shell input tap %d %d";
    private static final String SCREENCAP = "adb %s shell /system/bin/screencap -p %s";
    private static final String PULL = "adb %s pull %s %s";

    private String device = "";

    public ADB() {
    }

    public ADB(String device) {
        this.device = device;
    }

    private String selectDevice() {
        String cmd = "";
        if (device != null && !"".equals(device)) {
            cmd = "-s " + device;
        }
        return cmd;
    }

    /**
     * handle adb tap to simulate screen touching.
     * @param x x
     * @param y y
     */
    public void tap(int x, int y) {
        String tap = String.format(TAP, selectDevice(), x, y);
        Command.exeCmd(tap);
        Utils.sleep(10);
    }

    /**
     * handle adb screencap to get the result of image's path.
     * @return cmd
     */
    public String screencap(String path){
        String screencap = String.format(SCREENCAP, selectDevice(), path);
        return Command.exeCmd(screencap);
    }

    /**
     * pull file from device to Mac
     * @param fromPath Mobile file path
     * @param toPath Mac file path
     * @return cmd
     */
    public String pull(String fromPath, String toPath){
        String pull = String.format(PULL, selectDevice(), fromPath, toPath);
        return Command.exeCmd(pull);
    }

    /**
     * keep adb alive.
     */
    public void keepAlive(){
        String result = Command.exeCmd("adb devices");
        if (result.contains("offline")){
            Command.exeCmd("adb kill-server");
            Command.exeCmd("adb start-server");
        }
    }

    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        ADB adb = new ADB();
        String path = adb.screencap("/sdcard/screenshot.png");
        System.out.println(path);
        System.out.println(System.currentTimeMillis() - s);

    }
}

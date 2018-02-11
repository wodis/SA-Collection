package com.openwudi.sa;

import com.openwudi.sa.adb.ADB;
import com.openwudi.sa.script.CollectionScript;
import com.openwudi.sa.script.PetCaptureScript;
import com.openwudi.sa.script.PetLevelScript;
import com.openwudi.sa.script.Script;
import com.openwudi.sa.thread.Daemon;
import com.openwudi.sa.util.LogUtil;
import com.openwudi.sa.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StoneAgeApp {

    public static void main(String[] args) throws Exception {
        String device = selectDevice();
        String cache = getDeviceCachePath(device);

        ADB adb = new ADB(device);
        //守护线程保证设备存在
        new Thread(new Daemon()).start();

        Script script = selectScript(adb, cache);

        int runTimes = 0;
        while (true) {
            LogUtil.infoFirst("开始执行脚本");
            boolean success = script.run();
            if (success) {
                runTimes++;
                LogUtil.infoEnd("已运行 {} 次\n", runTimes);
            }
        }
    }

    private static String selectDevice() {
        ADB adb = new ADB();
        List<String> devices = adb.getAliveDevices();
        LogUtil.infoFirst("══════════════════════════╗");
        LogUtil.info("       请选择以下设备:");
        for (String device : devices) {
            LogUtil.info("       {}", device);
        }
        LogUtil.info("       请输入设备标识:");
        Scanner sc = new Scanner(System.in);
        String device = sc.nextLine();
        LogUtil.info("       您选择的是:{}", device);
        LogUtil.infoEnd("══════════════════════════╝");
        return device;
    }

    private static String getDeviceCachePath(String device) {
        String cache = new File("").getAbsolutePath() + File.separator + "PicCache" + File.separator + device;
        File path = new File(cache);
        if (!path.exists()) {
            path.mkdirs();
        }
        System.out.println("PicCache:" + cache);
        return cache;
    }

    private static Script selectScript(ADB adb, String cachePath) {
        List<Script> list = new ArrayList<Script>();
        list.add(new CollectionScript(adb, cachePath));
        list.add(new PetLevelScript(adb, cachePath));
        list.add(new PetCaptureScript(adb, cachePath));



        Script script = null;
        LogUtil.infoFirst("══════════════════════════╗");
        LogUtil.info("       选择脚本:");
        for (int i = 0; i < list.size(); i++) {
            Script s = list.get(i);
            LogUtil.info("       {}:{}", i, s.name);
        }
        LogUtil.infoEnd("══════════════════════════╝");
        LogUtil.infoFirst("══════════════════════════╗");
        LogUtil.info("       请输入编号:");
        Scanner sc = new Scanner(System.in);
        String item = sc.nextLine().trim();
        script = list.get(Integer.parseInt(item));
        LogUtil.info("       您选择的是:{}", script.name);
        LogUtil.infoEnd("══════════════════════════╝");
        return script;
    }
}

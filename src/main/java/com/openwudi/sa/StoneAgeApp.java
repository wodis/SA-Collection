package com.openwudi.sa;

import com.openwudi.sa.adb.ADB;
import com.openwudi.sa.script.CollectionScript;
import com.openwudi.sa.script.PetLevelScript;
import com.openwudi.sa.script.Script;
import com.openwudi.sa.thread.Daemon;
import com.openwudi.sa.util.LogUtil;
import com.openwudi.sa.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class StoneAgeApp {

    public static void main(String[] args) throws Exception {
        String cache = new File("").getAbsolutePath() + File.separator + "PicCache";
        File path = new File(cache);
        if (!path.exists()){
            path.mkdir();
        }

        System.out.println("PicCache:" + cache);

        ADB adb = new ADB();

        //守护线程保证设备存在
        new Thread(new Daemon()).start();

//        Script script = new CollectionScript(adb, cache);
        Script script = new PetLevelScript(adb, cache);

        int runTimes = 0;
        while (true) {
            LogUtil.infoFirst("开始执行脚本");
            boolean success = script.run();
            if (success) {
                runTimes++;
                LogUtil.infoEnd("已运行 {} 次整理\n", runTimes);
                Utils.sleep(CollectionScript.ROUND_WAIT_TIME);
            }
        }
    }
}

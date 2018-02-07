package com.openwudi.sa.script;

import com.openwudi.sa.adb.ADB;
import com.openwudi.sa.image.FingerPrint;
import com.openwudi.sa.util.LogUtil;
import com.openwudi.sa.util.ScriptUtils;
import com.openwudi.sa.util.Utils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CollectionScript implements Script {
    private static final long RETRY_TIME = 5000L;
    private static final int RETRY_FAIL_TIMES = 5;
    public static final long ROUND_WAIT_TIME = 10 * 60 * 1000L;

    private String imagePath;
    private ADB adb;
    private ScriptUtils scriptUtils;

    public CollectionScript(ADB adb, String imagePath) {
        this.imagePath = imagePath;
        this.adb = adb;
        this.scriptUtils = new ScriptUtils(adb, imagePath);
    }

    public boolean run() {
        long startTime = System.currentTimeMillis();
        scriptUtils.stopBattle();
        scriptUtils.stopBattle();

        int openFailTimes = 0;
        try {
            while (true) {
                String questionAndAnswers = getResult(1804, 706, 97, 53);
                if (questionAndAnswers.contains("道具")) {
                    //打开道具
                    scriptUtils.orderTools();
                    //加血
                    scriptUtils.fillUpBlood();
                    //开始战斗
                    scriptUtils.startBattle();
                    //删除缓存截图
                    Utils.deleteCache(imagePath);
                    String date = Utils.stampToDate((System.currentTimeMillis() + ROUND_WAIT_TIME) + "");
                    LogUtil.info("本次用时: {} 秒", (System.currentTimeMillis() - startTime) / 1000);
                    LogUtil.info("下次启动时间: {}", date);
                    break;
                } else {
                    LogUtil.info("还在战斗,1分钟内重试");
                    Utils.sleep(RETRY_TIME);
                    openFailTimes++;
                    if (openFailTimes > RETRY_FAIL_TIMES) {
                        scriptUtils.checkGameOffline();
                        throw new RuntimeException("超过错误次数，准备重试！");
                    }
                }
            }
            return true;
        } catch (Exception e) {
            LogUtil.infoEnd(e.getMessage() + "\n");
            return false;
        }
    }

    private String getResult(int x, int y, int w, int h) throws IOException {
        String image = scriptUtils.getImage();
        InputStream daojuPNG = this.getClass().getResourceAsStream("/daoju.png");

        float result = scriptUtils.getImageHashResult(image, x, y, w, h, daojuPNG);
        LogUtil.info("脱离战斗,比对结果={}", result);
        if (result > 0.9f) {
            return "道具";
        } else {
            return "";
        }
    }
}

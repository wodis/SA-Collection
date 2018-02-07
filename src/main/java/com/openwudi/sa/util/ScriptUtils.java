package com.openwudi.sa.util;

import com.openwudi.sa.adb.ADB;
import com.openwudi.sa.image.FingerPrint;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ScriptUtils {
    private static final Long MIN_IMAGE_SIZE = 1000L;

    private ADB adb;
    private String imagePath;

    public ScriptUtils(ADB adb, String imagePath) {
        this.adb = adb;
        this.imagePath = imagePath;
    }

    /**
     * 截图
     *
     * @return
     */
    public String getImage() {
        //获取当前时间作为名字
        File curPhoto = new File(Utils.getImagePath(imagePath));
        //截屏存到手机本地
        while (!curPhoto.exists() || curPhoto.length() < MIN_IMAGE_SIZE) {
            adb.screencap("/sdcard/screenshot.png");
            //将截图放在电脑本地
            adb.pull("/sdcard/screenshot.png", curPhoto.getAbsolutePath());
        }
        //返回当前图片名字
        return curPhoto.getAbsolutePath();
    }

    /**
     * 检查是否掉线
     */
    public void checkGameOffline() {
        try {
            String image = getImage();
            InputStream daojuPNG = this.getClass().getResourceAsStream("/offline.png");
            float result = getImageHashResult(image, 880, 600, 155, 50, daojuPNG);
            LogUtil.info("掉线,比对结果={}", result);
            if (result > 0.9f) {
                adb.tap(960, 625);
                Utils.sleep(1000);
                adb.tap(780, 845);
                Utils.sleep(1000);
                adb.tap(960, 845);
                Utils.sleep(1000);
                adb.tap(760, 900);
                Utils.sleep(1000);
            }
        } catch (IOException e) {
            LogUtil.error("掉线检查遇到错误.");
        }
    }

    public void startBattle() {
        //打开便捷
        adb.tap(1560, 1000);
        //原地遇敌
        adb.tap(1340, 579);
        LogUtil.info("原地遇敌");
    }

    public void stopBattle() {
        //打开便捷
        adb.tap(1560, 1000);
        //取消遇敌
        adb.tap(1605, 580);
        LogUtil.info("取消遇敌");
    }

    /**
     * 整理道具
     */
    public void orderTools() {
        LogUtil.info("");
        LogUtil.info(">>>>开始打开道具");
        adb.tap(1850, 740);
        LogUtil.info(">>>>>>>>1.点击整理");
        adb.tap(1210, 858);
        adb.tap(1210, 858);
        LogUtil.info(">>>>>>>>2.整理完成");
        adb.tap(1792, 110);
        LogUtil.info(">>>>完成整理");
        LogUtil.info("");
    }

    /**
     * 加血
     */
    public void fillUpBlood() {
        LogUtil.info("");
        LogUtil.info(">>>>开始加血");
        //打开精灵
        adb.tap(1850, 580);
        LogUtil.info(">>>>>>>>1.选择新手滋润精灵");
        adb.tap(1400, 245);
        LogUtil.info(">>>>>>>>2.点击人物");
        adb.tap(880, 210);
        LogUtil.info(">>>>>>>>3.点击宠物");
        adb.tap(400, 220);
        adb.tap(400, 400);
        adb.tap(400, 550);
        adb.tap(400, 720);
        adb.tap(400, 880);
        //关闭精灵
        adb.tap(1790, 110);
        LogUtil.info(">>>>完成加血");
        LogUtil.info("");
    }

    public float getImageHashResult(String imagePath, int x, int y, int w, int h, InputStream compare) throws IOException {
        String cutImgPath = Utils.cutImage(imagePath, x, y, w, h);
        FingerPrint fp1 = new FingerPrint(ImageIO.read(compare));
        FingerPrint fp2 = new FingerPrint(ImageIO.read(new File(cutImgPath)));

        float result = fp1.compare(fp2);
        LogUtil.info("图片比对结果: {}", result);
        return result;
    }
}

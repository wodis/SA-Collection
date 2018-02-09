package com.openwudi.sa.script;

import com.openwudi.sa.adb.ADB;
import com.openwudi.sa.image.FingerPrint;
import com.openwudi.sa.ocr.OCR;
import com.openwudi.sa.ocr.impl.OCRFactory;
import com.openwudi.sa.util.*;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class PetLevelScript extends Script {
    private static final Long ROUND_TIME = 2 * 60000L;
    private static final OCRFactory OCR_FACTORY = new OCRFactory();

    private String imagePath;
    private ADB adb;
    private ScriptUtils scriptUtils;
    private MusicPlay musicPlay = new MusicPlay(Utils.class.getResourceAsStream("/sabgm_s0.wav"));

    public PetLevelScript(ADB adb, String imagePath) {
        super("限制升级脚本");
        this.imagePath = imagePath;
        this.adb = adb;
        this.scriptUtils = new ScriptUtils(adb, imagePath);
    }

    public boolean run() throws IOException {
        LogUtil.info("请输入宠物位置(0~4):");
        Scanner sc = new Scanner(System.in);
        String petString = sc.nextLine().trim();
        LogUtil.info("请输入限制等级:");
        String limitString = sc.nextLine().trim();
        LogUtil.info("请输入结束时的操作: 1=登出  2=停止遇敌");
        String optString = sc.nextLine().trim();
        int pet = Integer.parseInt(petString);
        int limit = Integer.parseInt(limitString);
        int opt = Integer.parseInt(optString);
        LogUtil.info("宠物位置:{} == 限制等级:{} == 停止方式:{}", pet, limit, opt);

        OCR ocr = OCR_FACTORY.getOcr(1);

        while (true) {
            LogUtil.infoFirst("打开宠物栏, 宠物位置 {}, 等级限制 lv.{}", pet, limit);
            adb.tap(1430, 1000);
            String screenshot = scriptUtils.getImage();
            String path = "";
            switch (pet) {
                case 0:
                    path = Utils.cutImage(screenshot, 240, 230, 73, 40);
                    break;
                case 1:
                    path = Utils.cutImage(screenshot, 240, 400, 73, 50);
                    break;
                case 2:
                    path = Utils.cutImage(screenshot, 240, 565, 73, 50);
                    break;
                case 3:
                    path = Utils.cutImage(screenshot, 240, 730, 73, 50);
                    break;
                case 4:
                    path = Utils.cutImage(screenshot, 240, 895, 73, 50);
                    break;
                default:
                    path = Utils.cutImage(screenshot, 240, 400, 73, 50);
            }
            String blackWhitePng = convert2BlackWhite(path);
            adb.tap(1800, 110);
            String result = ocr.getOCR(new File(blackWhitePng)).trim();
            if (result.length() > 0) {
                LogUtil.info("OCR结果：{}", result);
                try {
                    Integer level = null;
                    result = result.replaceAll("O", "0").replaceAll("o", "0");
                    level = Integer.valueOf(result);
                    LogUtil.info("等级: {}", level);
                    if (level >= limit) {
                        //打开升级音乐
                        LogUtil.info("宠物位置 {}, 到达lv.{}限制!!!", pet, limit);
                        operation(opt);
                    }
                    LogUtil.info("等待 {} 秒后继续检查.", ROUND_TIME / (1000f));
                    Utils.sleep(ROUND_TIME);
                } catch (NumberFormatException e) {
                    moveDebugImage(path);
                    LogUtil.error("画面太快了！");
                }
            } else {
                moveDebugImage(path);
                scriptUtils.checkGameOffline();
            }
            Utils.deleteCache(imagePath);
            LogUtil.infoEnd("完成检查\n");
        }
    }

    private void levelUpBgmStart() {
        new Thread(new Runnable() {
            public void run() {
                musicPlay.continuousStart();
            }
        }).start();
    }

    private void levelUpBgmStop() {
        musicPlay.continuousStop();
    }

    private String convert2BlackWhite(String path) {
        String dest = Utils.getImagePath(imagePath);
        BiImage bi = new BiImage();
        try {
            bi.initialize(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        bi.monochrome(dest); // 黑白化，输出到磁盘
        return dest;
    }

    private void moveDebugImage(String path) throws IOException {
        File debugDir = new File(new File(imagePath).getParent(), "debug");
        FileUtils.moveFileToDirectory(new File(path), debugDir, true);
    }

    /**
     * 达到等级后的操作
     *
     * @param opt
     */
    private void operation(int opt) {
        levelUpBgmStart();
        if (1 == opt) {
            int times = 0;
            while (true) {
                adb.tap(1560, 1000);
                adb.tap(1600, 830);
                adb.tap(1330, 760);
                times++;
                if (times > 10) {
                    //关闭升级音乐
                    levelUpBgmStop();
                    LogUtil.info("登出并退出程序!!!");
                    System.exit(0);
                }
            }
        } else if (2 == opt) {
            int times = 0;
            while (true) {
                scriptUtils.stopBattle();
                times++;
                if (times > 10) {
                    //关闭升级音乐
                    levelUpBgmStop();
                    LogUtil.info("登出并退出程序!!!");
                    System.exit(0);
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}

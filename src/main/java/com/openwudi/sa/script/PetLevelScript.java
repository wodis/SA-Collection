package com.openwudi.sa.script;

import com.openwudi.sa.adb.ADB;
import com.openwudi.sa.image.FingerPrint;
import com.openwudi.sa.ocr.OCR;
import com.openwudi.sa.ocr.impl.OCRFactory;
import com.openwudi.sa.util.LogUtil;
import com.openwudi.sa.util.ScriptUtils;
import com.openwudi.sa.util.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PetLevelScript implements Script {
    private static final Long MIN_IMAGE_SIZE = 1000L;
    private static final OCRFactory OCR_FACTORY = new OCRFactory();

    private String imagePath;
    private ADB adb;
    private ScriptUtils scriptUtils;

    public PetLevelScript(ADB adb, String imagePath) {
        this.imagePath = imagePath;
        this.adb = adb;
        this.scriptUtils = new ScriptUtils(adb, imagePath);
    }

    public boolean run() throws IOException {
        int pet = 1;

        OCR ocr = OCR_FACTORY.getOcr(1);

        while (true) {
            adb.tap(1430, 1000);
            String screenshot = scriptUtils.getImage();
            String path = "";
            switch (pet){
                case 0:
                    path = Utils.cutImage(screenshot, 240, 240, 73, 50);
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
            adb.tap(1800, 110);
            String result = ocr.getOCR(new File(path)).trim();
            if (result.length() > 0) {
                try {
                    Integer level = null;
                    level = Integer.valueOf(result);
                    System.out.println("等级:" + level);
                    if (level > 79) {
//                        playMusic();
                        int times = 0;
                        while (true) {
                            adb.tap(1560, 1000);
                            adb.tap(1600, 830);
                            adb.tap(1330, 760);
                            times++;
                            if (times > 50) {
                                System.exit(0);
                            }
                        }
                    }
                    Utils.sleep(50000);
                } catch (NumberFormatException e) {
                    System.out.println("画面太快了！");
                }
            } else {
                scriptUtils.checkGameOffline();
            }
            Utils.deleteCache(imagePath);
        }
    }
}

package com.openwudi.sa.script;

import com.openwudi.sa.adb.ADB;
import com.openwudi.sa.util.LogUtil;
import com.openwudi.sa.util.MusicPlay;
import com.openwudi.sa.util.ScriptUtils;
import com.openwudi.sa.util.Utils;

import java.io.IOException;
import java.util.Scanner;

public class PetCaptureScript extends Script {
    private static final Long ROUND_TIME = 1 * 10 * 1000L;

    private ScriptUtils scriptUtils;

    public PetCaptureScript(ADB adb, String imagePath) {
        super("抓宠脚本", adb, imagePath);
        scriptUtils = new ScriptUtils(adb, imagePath);
    }

    public boolean run() throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            long startTime = System.currentTimeMillis();
            String img = scriptUtils.getImage();
            float result = scriptUtils.getImageHashResult(img, 1350, 155, 100, 100, this.getClass().getResourceAsStream("/defense.png"));
            if (result > 0.9) {
                LogUtil.info("遇敌了！快抓吧！");
                MusicPlay musicPlay = new MusicPlay(Utils.class.getResourceAsStream("/sabgm_b1.wav"));
                start(musicPlay);
                while (true) {
                    LogUtil.info("请输入ok继续:");
                    String str = sc.nextLine().trim();
                    if ("ok".equalsIgnoreCase(str)) {
                        stop(musicPlay);
                        break;
                    }
                }
            } else {
                LogUtil.info("耗时:{}毫秒", (System.currentTimeMillis() - startTime));
                LogUtil.info("等待 {} 秒后继续检查.", ROUND_TIME / (1000f));
                Utils.sleep(ROUND_TIME);
            }
            Utils.deleteCache(imagePath);
        }
    }

    private boolean isStart = false;
    private void start(final MusicPlay musicPlay) {
        isStart = true;
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    if (isStart){
                        musicPlay.start();
                    } else {
                        break;
                    }
                }
            }
        }).start();
    }

    private void stop(MusicPlay musicPlay) {
        isStart = false;
        musicPlay.stop();
    }

    public static void main(String[] args) throws IOException {
        MusicPlay musicPlay = new MusicPlay(Utils.class.getResourceAsStream("/sabgm_b1.wav"));
        musicPlay.start();
    }
}

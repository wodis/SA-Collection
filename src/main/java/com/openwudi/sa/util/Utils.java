package com.openwudi.sa.util;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String getImagePath(String parentPath) {
        Date current = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String curDate = df.format(current);
        File curPhoto = new File(parentPath, curDate + ".png");
        return curPhoto.getAbsolutePath();
    }

    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String cutImage(String screenshot, int x, int y, int w, int h) throws IOException {
        File screenshotFile = new File(screenshot);
        String img = screenshot;

        BufferedImage bufferedImage = ImageIO.read(new File(img));
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        if (height > width) {
            Thumbnails.of(img)
                    .size(1080, 1920)
                    .rotate(-90)
                    .toFile(new File(img));
        }


        String cutImgPath = getImagePath(screenshotFile.getParent());
        Thumbnails.of(img)
                .sourceRegion(x, y, w, h)
                .size(w, h)
                .toFile(new File(cutImgPath));

        return cutImgPath;
    }

    public static void deleteCache(String imagePath) {
        File dir = new File(imagePath);
        File[] tempList = dir.listFiles();
        if (tempList != null) {
            for (File file : tempList) {
                file.delete();
            }
        }
    }

    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static void main(String[] args) throws IOException {
        String file = "/Users/diwu/Pictures/my_screen01.png";
        cutImage(file, 300, 155, 180, 60);
    }
}

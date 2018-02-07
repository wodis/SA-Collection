package com.openwudi.sa.util;

import com.openwudi.sa.adb.ADB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextScriptUtils {
    private ADB adb;
    private String imagePath;

    public TextScriptUtils(ADB adb, String imagePath) {
        this.adb = adb;
        this.imagePath = imagePath;
    }

    public void run(String line) {
        String[] array = line.split(" ");
        String msg = "";
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            String item = array[i];
            if (i == 0) {
                msg = item;
            } else {
                args.append(item).append(" ");
            }
        }

        if ("左按".equals(msg)) {
            click(args.toString());
        }
    }

    private void click(String args) {
        String[] xy = args.split(" ");
        List<String> listXY = new ArrayList<String>(2);
        for (String i : xy) {
            if (!"".equals(i)) {
                listXY.add(i);
            }
        }

        if (listXY.size() >= 2) {
            adb.tap(Integer.valueOf(listXY.get(0)), Integer.valueOf(listXY.get(1)));
        }
    }

    public static void main(String[] args) {
        String cache = new File("").getAbsolutePath() + File.separator + "PicCache";
        TextScriptUtils utils = new TextScriptUtils(new ADB(), cache);

        String dian = "左按 10 10";
        utils.run(dian);
    }
}

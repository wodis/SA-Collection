package com.openwudi.sa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    static Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static void infoFirst(String var1, Object... var2) {
        logger.info(" ╔ " + var1, var2);
    }

    public static void info(String var1, Object... var2) {
        logger.info(" ║ " + var1, var2);
    }

    public static void infoEnd(String var1, Object... var2) {
        logger.info(" ╚ " + var1, var2);
    }

    public static void error(String var1, Object... var2) {
        logger.error(" ║ " + var1, var2);
    }
}

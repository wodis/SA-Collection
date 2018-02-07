package com.openwudi.sa.ocr.impl;

import com.openwudi.sa.ocr.OCR;

/**
 * Created by 618 on 2018/1/12.
 *
 * @author lingfengsan
 */
public class OCRFactory {
    public OCR getOcr(int choice) {
        switch (choice) {
            case 1: {
                return new TessOCR();
            }
            default: {
                return new TessOCR();
            }
        }

    }
}

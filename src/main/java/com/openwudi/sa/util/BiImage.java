package com.openwudi.sa.util;


import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;


/*
 * 用于把一个图片转化为12x18的01矩阵
 *
 * @author soulmachine@gmail.com
 * @version 1.0, 02/04/2010
 */
public class BiImage {
    /** 将文件读取成Image 对象，便于操作 */
    //protected Image image;

    /** 图片的宽 */
    protected int width;

    /** 图片的高 */
    protected int height;

    /** 将image中的像素读取出来，存放在本变量中 */
    protected int pixels[];

    /**
     * 查找灰色像素
     */
    protected boolean checkGray = false;

    /** The constructor */
    public BiImage() {

    }

    /**
     * 判断一个图片文件的类型。
     * 前提是，已知该文件是图片；本函数仅读取文件头部两个字节进行判断。
     * 虽然可以多读几个字节会更精确，这里没必要，因为已知是图片了。
     *
     * @param file
     * @return 图片类型后缀
     * @throws IOException
     */

    private static String getImageType(String filePath) throws IOException{
        File f = new File(filePath);
        FileInputStream in = null;
        String type = null;
        byte[] bytes = { 0, 0 }; // 用于存放文件头两个字节

        in = new FileInputStream(f);

        in.read(bytes, 0, 2);

        if (((bytes[0] & 0xFF) == 0x47) && ((bytes[1] & 0xFF) == 0x49)) { // GIF
            type = "gif";
        } else if (((bytes[0] & 0xFF) == 0x89) && ((bytes[1] & 0xFF) == 0x50)) { // PNG
            type = "png";
        } else if (((bytes[0] & 0xFF) == 0xFF) && ((bytes[1] & 0xFF) == 0xD8)) { // JPG
            type = "jpg";
        } else if (((bytes[0] & 0xFF) == 0x42) && ((bytes[1] & 0xFF) == 0x4D)) { // BMP
            type = "bmp";
        } else { // not supported type
            // System.out.println("not supported type!");
        }

        in.close();

        return type;
    }


    /**
     * 判断一个TYPE_INT_ARGB彩色是靠近白色还是靠近黑色
     *
     * @param pixel 一个 TYPE_INT_ARGB颜色
     * @return 对应的黑色或白色
     */
    private int convertToBlackWhite(int pixel) {
        int result = 0;

        //int alpha = (pixel >> 24) & 0xff; // not used
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        result = 0xff000000; // 这样，白色就为全F，即 -1

        if (checkGray){
            if (red == green && green == blue && blue < 100){
                result += 0x00ffffff;
            }
        } else {
            int tmp = red * red + green * green + blue * blue;
            if(tmp > 6*128*128){ // 大于，则是白色
                result += 0x00ffffff;
            }
        }

        return result;
    }
    /**
     * 从磁盘文件读取图片
     *
     * @param imageFile 文件路径
     * @return BufferedImage对象，失败为null
     * @throws IOException
     */
    public static BufferedImage readImageFromFile(String imageFile) throws IOException{
        BufferedImage bi;

        // 获取某种图片格式的reader对象
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(
                BiImage.getImageType(imageFile));
        ImageReader reader = (ImageReader)readers.next();
        // 为该reader对象设置输入源
        ImageInputStream iis = ImageIO.createImageInputStream(
                new File(imageFile));
        reader.setInput(iis);

        // 创建图片对象
        bi = reader.read(0);

        readers = null;
        reader = null;
        iis = null;

        return bi;
    }

    /**
     * 将图片写入磁盘文件
     *
     * @param imgFile 文件路径
     * @param bi BufferedImage 对象
     * @return 无
     */
    public static void  writeImageToFile(String imgFile, BufferedImage bi)
            throws IOException {
        // 写图片到磁盘上
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(
                imgFile.substring(imgFile.lastIndexOf('.') + 1));
        ImageWriter writer = (ImageWriter) writers.next();
        // 设置输出源
        File f = new File(imgFile);
        ImageOutputStream ios;

        ios = ImageIO.createImageOutputStream(f);
        writer.setOutput(ios);
        // 写入到磁盘
        writer.write(bi);
    }



    /**
     * 初始化函数
     *
     * @param imageFile 文件路径
     * @return 无
     * @throws IOException
     * @throws IOException
     * @exception 无
     */
    public void initialize(String imageFile) throws IOException{
        BufferedImage bi = readImageFromFile(imageFile);

        // 得到宽和高
        width = bi.getWidth(null);
        height = bi.getHeight(null);

        // 读取像素
        pixels = new int[width * height];
        bi.getRGB(0, 0, width, height, pixels, 0, width);

        bi = null;
    }

    public void gray(boolean checkGray){
        this.checkGray = checkGray;
    }

    /**
     * 将图片转化为黑白图片
     *
     * @param imgFile 输出文件路径
     * @return
     */
    public BufferedImage monochrome(String imgFile) {
        int newPixels[] = new int[width * height];
        for(int i = 0; i < width * height; i++) {
            newPixels[i] = convertToBlackWhite(pixels[i]);
        }

        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0, 0, width, height, newPixels, 0, width);
        newPixels = null;

        try {
            BiImage.writeImageToFile(imgFile, bi);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bi;
    }

    /**
     * 判断一张图片是否是黑白图片
     *
     * @param imgFile
     * @return 是，返回true，灰度或彩色图片，返回false
     */
    private static boolean isMonochrome(String imgFile) {
        BufferedImage bi = null;
        boolean result = false;
        int w = 0, h = 0;
        int i = 0, j = 0;

        try {
            bi = readImageFromFile(imgFile);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        w = bi.getWidth();
        h = bi.getHeight();
        int count = 0; //黑白像素个数
        int n = 0; // 非黑白个数
        for(j = 0; j < h; j++)
            for(i = 0; i < w; i++) {
                int rgb = bi.getRGB(i, j);
                rgb &= 0x00FFFFFF;
                if((rgb != 0x00FFFFFF) && (rgb != 0)){ // 既不是白色也不是黑色
                    n++;
                    break;
                }
                else {
                    count ++;
                }
            }
        System.out.println(count);
        System.out.println(n);
        if((i == w) && (j == h)) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public static void main(String[] args) {

        BiImage bi = new BiImage();
        try {
            bi.initialize("d:\\20180308234650.png");
            bi.gray(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String b = "d:\\temp.png";
        bi.monochrome(b); // 黑白化，输出到磁盘
        // 从磁盘读取刚生成的文件，检测每个像素，是否是黑白两色
        System.out.println(BiImage.isMonochrome(b)); // 总是false，检测失败，靠
    }
}

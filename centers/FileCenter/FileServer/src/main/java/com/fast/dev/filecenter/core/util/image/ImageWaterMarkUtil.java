package com.fast.dev.filecenter.core.util.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/*******************************************************************************
 * Description: 图片水印工具类
 * @author zengshunyao
 * @version 1.0
 */
public class ImageWaterMarkUtil {

    // 水印透明度
    private static float alpha = 0.5f;
    // 水印横向位置
    private static int positionWidth = 150;
    // 水印纵向位置
    private static int positionHeight = 300;
    // 水印文字字体
    private static Font font = new Font("宋体", Font.BOLD, 72);
    // 水印文字颜色
    private static Color color = Color.GRAY;

    /**
     * 给图片添加水印图片、可设置水印图片旋转角度
     *
     * @param iconPath
     * @param sourceImageFile
     * @param outputImageFile
     * @param degree
     * @return
     */
    public static boolean markImageByIcon(File iconPath, File sourceImageFile, File outputImageFile, Integer degree) {
        try {
            return markImageByIcon(new FileInputStream(iconPath), new FileInputStream(sourceImageFile), new FileOutputStream(outputImageFile), degree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean markImageByIcon(InputStream iconStream, InputStream sourceImageStream, OutputStream outputImageStream, Integer degree) {
        try {
            Image srcImg = ImageIO.read(sourceImageStream);

            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 1、得到画笔对象
            Graphics2D g = buffImg.createGraphics();

            // 2、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            // 3、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }

            // 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
            // 5、得到Image对象。
            Image img = ImageIO.read(iconStream);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,alpha));

            // 6、水印图片的位置
            g.drawImage(img, positionWidth, positionHeight, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            // 7、释放资源
            g.dispose();

            // 8、生成图片
            ImageIO.write(buffImg, "JPG", outputImageStream);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     *
     * @param logoText
     * @param sourceImageFile
     * @param outputImageFile
     * @param degree
     * @return
     */
    public static boolean markImageByText(String logoText, File sourceImageFile, File outputImageFile, Integer degree) {
        try {
            return markImageByText(logoText, new FileInputStream(sourceImageFile), new FileOutputStream(outputImageFile), degree);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     *
     * @param logoText
     * @param sourceImageStream
     * @param outputImageStream
     * @param degree
     * @return
     */
    public static boolean markImageByText(String logoText, InputStream sourceImageStream, OutputStream outputImageStream, Integer degree) {
        try {
            // 1、源图片
            Image srcImg = ImageIO.read(sourceImageStream);
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 2、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            // 4、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }
            // 5、设置水印文字颜色
            g.setColor(color);
            // 6、设置水印文字Font
            g.setFont(font);
            // 7、设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
            g.drawString(logoText, positionWidth, positionHeight);
            // 9、释放资源
            g.dispose();
            // 10、生成图片
            ImageIO.write(buffImg, "JPG", outputImageStream);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static void main(String[] args) {
//        File srcImgPath = new File("F:/image/input/jhz.jpg");
//        String logoText = "艾 艺 在 线";
//        String iconPath = "F:/image/input/watermark.jpg";
//
//        File targerTextPath2 = new File("F:/image/qie_text_rotate.jpg");
//
//        String targerIconPath = "F:/image/qie_icon.jpg";
//        String targerIconPath2 = "F:/image/qie_icon_rotate.jpg";
//
//        System.out.println("给图片添加水印文字开始...");
//        // 给图片添加水印文字,水印文字旋转-45
//        markImageByText(logoText, srcImgPath, targerTextPath2, -25);
//        System.out.println("给图片添加水印文字结束...");
////
////        System.out.println("给图片添加水印图片开始...");
////        setImageMarkOptions(0.3f, 1, 1, null, null);
////        // 给图片添加水印图片
////        markImageByIcon(iconPath, srcImgPath, targerIconPath);
////        // 给图片添加水印图片,水印图片旋转-45
////        markImageByIcon(iconPath, srcImgPath, targerIconPath2, -45);
////        System.out.println("给图片添加水印图片结束...");
//
//        String str = "thumb_200_300";
//        String[] s = str.split("_", 2);
//        System.out.println(s.length);
//        System.out.println(s[0]);
//        System.out.println(s[1]);
//    }

}

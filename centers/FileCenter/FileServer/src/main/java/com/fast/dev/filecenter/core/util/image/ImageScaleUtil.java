package com.fast.dev.filecenter.core.util.image;

import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageScaleUtil {

    /**
     * 缩放图片
     * @param sourceImageFile
     * @param outputImageFile
     * @param width
     * @param height
     * @param rgb
     * @return
     */
    public static boolean scale(File sourceImageFile, File outputImageFile, int width, int height, String rgb) {
        try {
            return scale(new FileInputStream(sourceImageFile),new FileOutputStream(outputImageFile),width,height,rgb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 缩放图片
     *
     * @param inputStream
     * @param outputStream
     * @param width
     * @param height
     * @return
     */
    public static boolean scale(InputStream inputStream, OutputStream outputStream, int width, int height, String rgb) {
        try {
            Image image = null;
            int x = 0;//x偏移
            int y = 0;//y偏移
            BufferedImage sourceImage = ImageIO.read(inputStream);
            int sourceWidth = sourceImage.getWidth();
            int sourceHeight = sourceImage.getHeight();
            float wRate = (float) sourceWidth / width;
            float hRate = (float) sourceHeight / height;
            if (wRate < 1 && hRate < 1) {//这种情况没办法填充
                image = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            }
            if (wRate >= hRate) {//这种情况以宽度为准，算高，需要填充高
                int _height = sourceHeight * width / sourceWidth;
                y = (height - _height) / 2;
                image = sourceImage.getScaledInstance(width, _height, Image.SCALE_SMOOTH);
            }
            if (wRate < hRate) {//这种情况以高为准，算宽度，需要填充宽度
                int _width = sourceWidth * height / sourceHeight;
                x = (width - _width) / 2;
                image = sourceImage.getScaledInstance(_width, height, Image.SCALE_SMOOTH);
            }

            //创建一个画布
            BufferedImage targetImage = new BufferedImage(width, height, BufferedImage.SCALE_SMOOTH);
            Graphics2D graphics2d = targetImage.createGraphics();
            Color color = StringUtils.isEmpty(rgb) ? new Color(Color.white.getRGB()) : new Color(Integer.parseInt(rgb, 16));//默认设置为白色
            graphics2d.setBackground(color);//设置背景
            graphics2d.clearRect(0, 0, targetImage.getWidth(), targetImage.getHeight());//清除原背景
            graphics2d.drawImage(image, x, y, null);
            graphics2d.dispose();

            // 保存图片
            ImageIO.write(targetImage, "jpg", outputStream);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

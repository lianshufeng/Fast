package com.fast.dev.robot.robotserver.core.util;

import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.util.StreamUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 图片处理工具
 */
public class ImageHelper {


    //原输入流
    @Getter
    private InputStream inputStream;

    //图片格式
    private String imageType = "jpg";

    /**
     * @param inputStream 图片输入流
     */
    public ImageHelper(InputStream inputStream) {
        readMemStream(inputStream);
    }

    /**
     * @param inputStream 图片输入流
     * @param imageType   图片格式
     */
    public ImageHelper(InputStream inputStream, String imageType) {
        this.inputStream = inputStream;
        this.imageType = imageType;
    }


    /**
     * 转换图片，填充色用随机色
     *
     * @param angel
     * @return
     */
    public ImageHelper rotate(int angel) {
        Color color = new Color(RandomCodeUtil.getRandom(0, 255 * 255 * 255));
        return this.rotate(angel, color);
    }


    /**
     * 进行图片旋转
     *
     * @param angel
     * @return
     */
    @SneakyThrows
    public ImageHelper rotate(int angel, Color color) {
        BufferedImage source = ImageIO.read(this.inputStream);

        BufferedImage resultImg = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        @Cleanup("dispose") Graphics2D g = resultImg.createGraphics();
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, source.getWidth(), source.getHeight());
        // 使用 setRenderingHint 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        BufferedImage retImg = g.getDeviceConfiguration().createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.TRANSLUCENT);
        @Cleanup("dispose") Graphics2D ret = retImg.createGraphics();
        // 使用 setRenderingHint 设置抗锯齿
        ret.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ret.setClip(shape);
        //旋转
        ret.rotate(Math.toRadians(angel), source.getWidth() / 2, source.getHeight() / 2);
        //画布渲染到缓存
        ret.drawImage(source, 0, 0, null);

        //png to jpg
        BufferedImage jpgImage = new BufferedImage(retImg.getWidth(), retImg.getHeight(), source.getType());
        jpgImage.createGraphics().drawImage(retImg, 0, 0, color, null);


        return toInputStream(jpgImage);
    }


    /**
     * 自定义缩放，如果目标宽高比例与原始宽高比例不同，则缩放后进行裁剪
     *
     * @param width
     * @param height
     * @return
     */
    @SneakyThrows
    public ImageHelper scale(int width, int height) {
        BufferedImage source = ImageIO.read(this.inputStream);
        int source_w = source.getWidth();
        int source_h = source.getHeight();

        //计算宽高
        double ratio_w = (double) source_w / width;
        double ratio_h = (double) source_h / height;

        //取缩放比例,参考最小的边
        double scaleRatio = ratio_w > ratio_h ? ratio_h : ratio_w;

        //计算目标图片的分辨率
        int target_w = scaleNumber(source_w, scaleRatio);
        int target_h = scaleNumber(source_h, scaleRatio);


        //进行图片等比例方法
        BufferedImage scaleImage = new BufferedImage(target_w, target_h, source.getType());
        @Cleanup("dispose") Graphics2D g = scaleImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, target_w, target_h, 0, 0, source_w, source_h, null);


        //计算偏移的宽度与高度
        int x = (scaleImage.getWidth() - width) / 2;
        int y = (scaleImage.getHeight() - height) / 2;


        //防止宽度高度越界
        if (x + width > scaleImage.getWidth()) {
            x = 0;
            width = scaleImage.getWidth();
        }
        if (y + height > scaleImage.getHeight()) {
            y = 0;
            height = scaleImage.getHeight();
        }


        //进行裁剪
        BufferedImage target = scaleImage.getSubimage(x, y, width, height);

        return toInputStream(target);
    }

    /**
     * 缩放数字类型
     *
     * @param number
     * @param scaleRatio
     * @return
     */
    private static int scaleNumber(int number, double scaleRatio) {
        int scale = (int) (number / scaleRatio);
        //原始分辨率如果为偶数
        if (number % 2 == 0 && scale % 2 != 0) {
            scale++;
        } else if (number % 2 == 1 && scale % 2 != 1) {
            scale++;
        }
        return scale;
    }


    /**
     * 读取流
     *
     * @param inputStream
     */
    @SneakyThrows
    private void readMemStream(InputStream inputStream) {
        this.inputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));
    }

    /**
     * 画布转换为输入流
     *
     * @param bufferedImage
     * @return
     */
    @SneakyThrows
    private ImageHelper toInputStream(BufferedImage bufferedImage) {
        @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, imageType, byteArrayOutputStream);
        if (inputStream != null) {
            inputStream.close();
        }
        inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return this;
    }


}

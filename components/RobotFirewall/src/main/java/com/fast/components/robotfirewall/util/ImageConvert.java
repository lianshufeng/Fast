package com.fast.components.robotfirewall.util;

import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


@Slf4j
public class ImageConvert {


    //原输入流
    @Getter
    private InputStream inputStream;


    public ImageConvert(InputStream inputStream) {
        readMemStream(inputStream);
    }


    @SneakyThrows
    private void readMemStream(InputStream inputStream) {
        this.inputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));
    }


    /**
     * 将图片处理为圆形图片
     * 传入的图片必须是正方形的才会生成圆形 如果是长方形的比例则会变成椭圆的
     *  
     * 支持旋转的度数
     *
     * @return
     */
    @SneakyThrows
    public ImageConvert transferImgForRoundImgage(int angel) {
        BufferedImage buffImg = ImageIO.read(this.inputStream);
        BufferedImage resultImg = new BufferedImage(buffImg.getWidth(), buffImg.getHeight(), BufferedImage.TYPE_INT_RGB);
        @Cleanup("dispose") Graphics2D g = resultImg.createGraphics();
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, buffImg.getWidth(), buffImg.getHeight());
        // 使用 setRenderingHint 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        BufferedImage retImg = g.getDeviceConfiguration().createCompatibleImage(buffImg.getWidth(), buffImg.getHeight(),
                Transparency.TRANSLUCENT);
        @Cleanup("dispose") Graphics2D ret = retImg.createGraphics();
        // 使用 setRenderingHint 设置抗锯齿
        ret.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ret.setClip(shape);

        //旋转
        ret.rotate(Math.toRadians(angel), buffImg.getWidth() / 2, buffImg.getHeight() / 2);

        //画布渲染到缓存
        ret.drawImage(buffImg, 0, 0, null);


        toInputStream(retImg);
        return this;
    }


    /**
     * 图片缩放
     *
     * @param width
     * @param height
     * @return
     */
    @SneakyThrows
    public ImageConvert resize(int width, int height) {
        BufferedImage img = ImageIO.read(this.inputStream);
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(width, height, img.getType());
        @Cleanup("dispose") Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, width, height, 0, 0, w, h, null);
        toInputStream(dimg);
        return this;
    }


    /**
     * 画布转换为输入流
     *
     * @param bufferedImage
     * @return
     */
    @SneakyThrows
    private void toInputStream(BufferedImage bufferedImage) {
        @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        if (inputStream != null) {
            inputStream.close();
        }
        inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


}

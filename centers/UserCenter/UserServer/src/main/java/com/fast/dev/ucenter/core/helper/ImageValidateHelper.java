package com.fast.dev.ucenter.core.helper;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
@Component
public class ImageValidateHelper {

    @Autowired
    private DefaultKaptcha defaultKaptcha;


    /**
     * 创建图片
     *
     * @param code
     * @return
     */
    public byte[] create(String code) {
        // 生成图片验证码
        byte[] data = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedImage bufferedImage = defaultKaptcha.createImage(code);
            ImageIO.write(bufferedImage, "png", out);
            data = out.toByteArray();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}

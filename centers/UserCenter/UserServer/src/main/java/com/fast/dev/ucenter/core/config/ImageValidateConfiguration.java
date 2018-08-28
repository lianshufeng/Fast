package com.fast.dev.ucenter.core.config;

import com.fast.dev.ucenter.core.conf.KaptchaConf;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */

@Configuration
public class ImageValidateConfiguration {


    @Autowired
    private KaptchaConf kaptchaConf;

    @Bean
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
//        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", kaptchaConf.getFontColor());
        properties.setProperty("kaptcha.image.width", String.valueOf(kaptchaConf.getWidth()));
        properties.setProperty("kaptcha.image.height", String.valueOf(kaptchaConf.getHeight()));
//        properties.setProperty("kaptcha.session.key", "code");
//        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", kaptchaConf.getFontNames());
//        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}

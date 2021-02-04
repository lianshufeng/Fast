package com.fast.dev.core.util.script;

import groovy.lang.GroovyShell;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class GroovyUtil {


    /**
     * 执行脚本
     *
     * @param var
     * @param script
     * @return
     */
    public static Object runScript(Map<String, Object> var, String script) {
        GroovyShell groovyShell = new GroovyShell();
        for (Map.Entry<String, Object> entry : var.entrySet()) {
            groovyShell.setVariable(entry.getKey(), entry.getValue());
        }
        return groovyShell.evaluate(script);
    }


    private final static SimpleTemplateEngine simpleTemplateEngine = new SimpleTemplateEngine();

    /**
     * 转换为文本的模板引擎
     *
     * @return
     */
    @SneakyThrows
    public static String textTemplate(Map<String, Object> var, String template) {
        Writer writer = new StringWriter();
        Writable writable = simpleTemplateEngine.createTemplate(template).make(var);
        writable.writeTo(writer);
        String data = writer.toString();
        return data;
    }


}
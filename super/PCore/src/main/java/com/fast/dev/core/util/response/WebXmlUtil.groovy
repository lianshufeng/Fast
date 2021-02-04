package com.fast.dev.core.util.response

import com.fast.dev.core.util.response.model.MimeType

class WebXmlUtil {

    /**
     * 读取tomcat的xml中的所有的mime
     * @param inputStream
     * @param m
     */
    public static void readMime(InputStream inputStream, Map<String, MimeType> m) {
        def xml = new XmlParser();
        def result = xml.parse(inputStream);
        result['mime-mapping'].each() {
            String extension = it['extension'].text();
            MimeType mimeType = new MimeType();
            mimeType.setExtension(extension)
            mimeType.setName(it['mime-type'].text())
            m.put(extension, mimeType)
        }
    }


}

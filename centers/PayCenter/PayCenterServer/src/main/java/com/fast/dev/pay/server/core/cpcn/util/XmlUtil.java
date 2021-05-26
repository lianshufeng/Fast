package com.fast.dev.pay.server.core.cpcn.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XmlUtil {

    public XmlUtil() {
    }

    public static Schema createSchema(File schemaFile) throws Exception {
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        return schemaFactory.newSchema(schemaFile);
    }

    public static void validateViaDOM(Document document, Schema schema) throws Exception {
        DOMSource source = new DOMSource(document);
        Validator validator = schema.newValidator();
        validator.validate(source);
    }

    public static void validateViaDOM(String xmlString, Schema schema) throws Exception {
        DOMSource source = new DOMSource(createDocument(xmlString));
        Validator validator = schema.newValidator();
        validator.validate(source);
    }

    public static void validateViaSAX(String xmlString, Schema schema) throws Exception {
        SAXSource source = new SAXSource(new InputSource(new InputStreamReader(new ByteArrayInputStream(xmlString.getBytes("UTF-8")))));
        Validator validator = schema.newValidator();
        validator.validate(source);
    }

    public static void validateViaStreamSource(String xmlString, Schema schema) throws Exception {
        StreamSource source = new StreamSource(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
        Validator validator = schema.newValidator();
        validator.validate(source);
    }

    public static Document createDocument(String xmlString) throws Exception {
        DocumentBuilder documentBuilder = createDocumentBuilder();
        return documentBuilder.parse(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
    }

    public static Document createDocument(String xmlString, String charset) throws Exception {
        DocumentBuilder documentBuilder = createDocumentBuilder();
        return documentBuilder.parse(new ByteArrayInputStream(xmlString.getBytes(charset)));
    }

    public static Document createDocument(File xmlFile) throws Exception {
        DocumentBuilder documentBuilder = createDocumentBuilder();
        return documentBuilder.parse(xmlFile);
    }

    public static DocumentBuilder createDocumentBuilder() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        return documentBuilderFactory.newDocumentBuilder();
    }

    public static String getNodeText(Document document, String nodeName) throws Exception {
        return getNodeText(document, nodeName, 0);
    }

    public static String getNodeText(Document document, String nodeName, int index) throws Exception {
        NodeList nodeList = document.getElementsByTagName(nodeName);
        return nodeList != null && index < nodeList.getLength() ? nodeList.item(index).getTextContent() : null;
    }

    public static String getChildNodeText(Node node, String childName) {
        NodeList children = node.getChildNodes();
        int len = children.getLength();

        for(int i = 0; i < len; ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == 1 && child.getNodeName() == childName) {
                return child.getTextContent();
            }
        }

        return null;
    }

    public static List<String> getNodeList(Document document, String nodeName) throws Exception {
        NodeList nodeList = document.getElementsByTagName(nodeName);
        List<String> textList = new ArrayList();
        if (nodeList == null) {
            return null;
        } else {
            for(int i = 0; i < nodeList.getLength(); ++i) {
                textList.add(nodeList.item(i).getTextContent());
            }

            return textList;
        }
    }

    public static String getNodeAttributeValue(Document document, String nodeName, String attributeName) throws Exception {
        NodeList nodeList = document.getElementsByTagName(nodeName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element)nodeList.item(0);
            return element.getAttribute(attributeName);
        } else {
            return null;
        }
    }

    public static String getNodeText(Node node, String childName) throws Exception {
        NodeList children = node.getChildNodes();
        int len = children.getLength();

        for(int i = 0; i < len; ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == 1 && child.getNodeName().equals(childName)) {
                return child.getTextContent();
            }
        }

        return null;
    }

    public static Node getChildNode(Node node, String childName) throws Exception {
        NodeList children = node.getChildNodes();
        int len = children.getLength();

        for(int i = 0; i < len; ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == 1 && child.getNodeName().equals(childName)) {
                return child;
            }
        }

        return null;
    }

    public static String createPrettyFormat(Document document) throws Exception {
        return createPrettyFormat(document, "UTF-8");
    }

    public static String createPrettyFormat(Document document, String outputCharset) throws Exception {
        Source source = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("encoding", outputCharset);
        Writer writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));
        return writer.toString();
    }

    public static String createCompactFormat(Document document) throws Exception {
        return createCompactFormat(document, "UTF-8");
    }

    public static String createCompactFormat(Document document, String outputCharset) throws Exception {
        Source source = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("encoding", outputCharset);
        Writer writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));
        return writer.toString();
    }

}

package com.manson;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlPatcherService {

    private final Config config;
    private final XPath xPath;

    public XmlPatcherService(Config config) {
        this.config = config;
        this.xPath = XPathFactory.newInstance().newXPath();
    }

    public Object parse() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new FileInputStream(config.getInput()));
            for (XmlConfig xmlConfig : config.getConfigs()) {
                try {
                    NodeList nodes =
                        (NodeList) xPath.compile(xmlConfig.getPath()).evaluate(doc, XPathConstants.NODESET);
                    if (nodes != null && nodes.getLength() > 0) {
                        nodes.item(0).setNodeValue(xmlConfig.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Unable to patch value: " + xmlConfig.getPath());
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(new File(config.getOutput())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

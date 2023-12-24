package utils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;

public class XmlUtils {
    public static Document readXML (String filePath) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);

        doc.getDocumentElement().normalize();

        return doc;
    }

    public static void saveXML (String filePath, Document doc) throws Exception {
        doc.getDocumentElement().normalize();
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        FileWriter fw = new FileWriter(filePath);
        trans.transform(new DOMSource(doc), new StreamResult(fw));
    }
}
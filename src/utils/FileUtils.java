package utils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;

public class FileUtils {
    public static Object[][] readFile(String filePath, int columnsAmount) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line;
        int rowCount = 0;

        while ((line = br.readLine()) != null) {
            rowCount++;
        }

        br.close();

        Object[][] result = new Object[rowCount][columnsAmount];
        BufferedReader br2 = new BufferedReader(new FileReader(filePath));

        for (int row = 0; row < rowCount; row++) {
            String line1 = br2.readLine();
            String[] parts = line1.split(";");

            for (int j = 0; j < parts.length; j++) {
                result[row][j] = parts[j];
            }
        }

        br2.close();

        return result;
    }

    public static void writeFile(Object[][] data, String filePath) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

        for (Object[] row : data) {
            for (Object cell : row) {
                if (cell == null) {
                    bw.write("");
                } else {
                    bw.write(cell.toString());
                }

                bw.write(";");
            }
            bw.newLine();
        }

        bw.close();
    }
}
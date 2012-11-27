
package yeungeek.tk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @ClassName: DomHandler
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-27 上午09:59:12
 */
public class DomHandler {
    private static final Logger logger = LoggerFactory.getLogger(DomHandler.class);

    public static void readXml(InputStream inputSource) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputSource);
            // 获取根节点
            Element rootElement = document.getDocumentElement();
            // 获取第一级子节点
            NodeList nodeList = rootElement.getElementsByTagName("string-array");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                // element.getc
                NodeList subNodeList = element.getElementsByTagName("item");
                logger.debug("{}", subNodeList);
                for (int j = 0; j < subNodeList.getLength(); j++) {
                    Element subElement = (Element) subNodeList.item(i);
                    logger.debug("nodeValue {},{}", j, subElement.getNodeValue());
                }
            }
        } catch (Exception e) {
            logger.error("read xml error", e);
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws FileNotFoundException {
        FileInputStream is = new FileInputStream("/sdcard");
    }
}

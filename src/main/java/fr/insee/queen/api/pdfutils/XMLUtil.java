package fr.insee.queen.api.pdfutils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class XMLUtil {

	// Méthode générique pour exécuter une requête XPath sur une ficjhier XML
	public static String execXpathOnFile(String uriFichier, String expressionXpath) throws Exception {
		String url = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(uriFichier));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.compile(expressionXpath).evaluate(document, XPathConstants.NODESET);
		url = nodeList.item(0).getNodeValue();
		return url;
	}

	//
	// Méthode générique pour exécuter une requête XPath sur une réponse XMl de WS au format String
	public static String execXpathOnString(String xml, String expressionXpath) throws Exception {
		String url = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(xml);
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.compile(expressionXpath).evaluate(document, XPathConstants.NODESET);
		url = nodeList.item(0).getNodeValue();
		return url;
	}

}

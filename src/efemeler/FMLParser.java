package efemeler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FMLParser {
	
	private static String FuzzyControllerName, FuzzyControllerIP;
	private static String FuzzyVariableName, FuzzyVariableDomainLeft, FuzzyVariableDomainRight, FuzzyVariableScale, FuzzyVariableType;
	private static String FuzzyTermName, FuzzyTermComplement;
	
	private static String RuleBaseName, RuleBaseAndMethod, RuleBaseOrMethod, RuleBaseActivationMethod, RuleBaseType;
	
	private static ArrayList<String> xpaths = new ArrayList<String>();
	
	public static void parseFiles(File file, String location) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    	if (!file.isHidden()) {
    		System.out.print("File: " + file.getName());
    		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    		domFactory.setNamespaceAware(true); 
    		DocumentBuilder builder = domFactory.newDocumentBuilder();
    		Document doc = builder.parse(file.getAbsolutePath());
    		XPathFactory factory = XPathFactory.newInstance();
    		XPath xpath = factory.newXPath();
        
    		XPathExpression expr = xpath.compile("//intelwebclass/text()");
    		Object result = expr.evaluate(doc, XPathConstants.NODESET);
    		NodeList nodes = (NodeList) result;
    		String className = "";
    		for (int i=0; i<nodes.getLength(); i++) {
    			className = nodes.item(i).getNodeValue().toString().trim();
    		}
    		
    		if (location.contains(className.substring(9).toLowerCase())) {
    			System.out.println(" Located in: " + className.substring(9).toLowerCase());
    			
    			String uri = className.substring(9).toLowerCase();
    			
    			doc = builder.parse("fmlMapping");
    			expr = xpath.compile("//class/@value");
    			result = expr.evaluate(doc, XPathConstants.NODESET);
    			nodes = (NodeList) result;
    			String[] classes = new String[nodes.getLength()];
    			for (int i=0; i<nodes.getLength(); i++) {
    				classes[i] = nodes.item(i).getNodeValue().toString().trim();
    			}
    			
    			for (int i=0; i<classes.length; i++) {
    				if (classes[i].equals(className.substring(9))) {
    					doc = builder.parse("fmlMapping");
    					expr = xpath.compile("//class[@xmlClass=\""+className.substring(9)+"\"]/files/property/@value");
    					result = expr.evaluate(doc, XPathConstants.NODESET);
    					nodes = (NodeList) result;
    					String[] propertyNames = new String[nodes.getLength()];
    					for (int j=0; j<nodes.getLength(); j++) {
    						propertyNames[j] = nodes.item(j).getNodeValue().toString().trim();
    					}
    					
    					doc = builder.parse("fmlMapping");
    					expr = xpath.compile("//class[@xmlClass=\""+className.substring(9)+"\"]/files/property[@value]/xpath/text()");
    					result = expr.evaluate(doc, XPathConstants.NODESET);
    					nodes = (NodeList) result;
    					String[] xpaths = new String[nodes.getLength()];
    					for (int n=0; n<nodes.getLength(); n++) {
    						xpaths[n] = nodes.item(n).getNodeValue().toString().trim();
    					}
    					
    					doc = builder.parse(file.getAbsolutePath());
    					for (int m=0; m<xpaths.length-1; m++) {
    						expr = xpath.compile(xpaths[m]);
    						result = expr.evaluate(doc, XPathConstants.NODESET);
    						nodes = (NodeList) result;
    					}   					
    					// WRITE TO RDF FILE
    					FileOutputStream fos = new FileOutputStream("output");
    				}
    			}
    		}
    	}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		
//		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//		domFactory.setNamespaceAware(true); 
//		DocumentBuilder builder = domFactory.newDocumentBuilder();
//		Document doc = builder.parse("fmlMapping");
//		XPathFactory factory = XPathFactory.newInstance();
//		XPath xpath = factory.newXPath();
//		
//		String currDir = System.getProperty("user.dir");
//		XPathExpression expr = xpath.compile("//class[@value]//files//location/@value");
//		Object result = expr.evaluate(doc, XPathConstants.NODESET);
//		NodeList nodes = (NodeList) result;
//		for (int i=0; i<nodes.getLength(); i++) {
//			String path = currDir + nodes.item(i).getNodeValue().toString().trim();
//			System.out.println(path);
//			File[] files = new File(path).listFiles();
//			String locat = nodes.item(i).getNodeValue().toString().trim();
//			String directory = "";
//			parseFiles(files, locat);
//		}
		
		File exampleFML = new File("tipperNew.fml");
		File mapping = new File("fmlMapping.xml");
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(mapping);
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		XPathExpression expr = xpath.compile("//class/@value");
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		String[] classes = new String[nodes.getLength()];
		for (int i=0; i<nodes.getLength(); i++) {
			String expression = nodes.item(i).getNodeValue().toString();
			classes[i] = expression;
		}
		
		for (int i=0; i<classes.length; i++) {
			expr = xpath.compile("//class[@value=\'" + classes[i] + "\']/property/@value");
			result = expr.evaluate(doc, XPathConstants.NODESET);
			nodes = (NodeList) result;
			for (int j=0; j<nodes.getLength(); j++) {
				String property = nodes.item(j).getNodeValue().toString();
				expr = xpath.compile("//class[@value=\'" + classes[i] + "\']/property[@value=\'" + property + "\']/xpath/text()");
				Object propertiesResult = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList properties = (NodeList) propertiesResult;
				for (int k=0; k<properties.getLength(); k++) {
					xpaths.add(properties.item(k).getNodeValue().toString());
				}
			}
		}
		
		for (int i=0; i<xpaths.size(); i++) {
//			/System.out.println(xpaths.get(i).toString());
		}
		
//		doc = builder.parse(exampleFML);
//		expr = xpath.compile("name(//FuzzyVariable[@name=\'" + variableName + "\']/FuzzyTerm[@name=\'" + termName + "\']/*)");
//		Object mfNameObject = expr.evaluate(doc);
//		String mfName = mfNameObject.toString();
//		System.out.println(mfName);
		
		doc = builder.parse(exampleFML);
		expr = xpath.compile("//FuzzyVariable[@name=\'building\']/FuzzyTerm[@name=\'good\']/*/@*");
		Object attr = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList attrs = (NodeList) attr;
		System.out.println(attrs.getLength());
		for (int i=0; i<attrs.getLength(); i++) {
			System.out.println(attrs.item(i).getNodeValue());
		}
		
	}
}

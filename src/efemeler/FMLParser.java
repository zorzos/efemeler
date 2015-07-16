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

import generic.*;

public class FMLParser {
	
	private static String fuzzyControllerName, fuzzyControllerIP;
	private static String fuzzyVariableName, fuzzyVariableDomainLeft, fuzzyVariableDomainRight, fuzzyVariableScale, fuzzyVariableType, fuzzyVariableDefaultValue, fuzzyVariableAccumulation, fuzzyVariableDefuzzifier;
	private static String fuzzyTermName, fuzzyTermComplement;
	private static String functionName;
	private static String ruleBaseName, ruleBaseAndMethod, ruleBaseOrMethod, ruleBaseActivationMethod, ruleBaseType;
	private static String ruleName, ruleConnector, ruleOperator, ruleWeight;
	private static NodeList clauseVariables, clauseTerms;
	private static String[] antecedentVars, antecedentTerms, consequentVars, consequentTerms;
	private static String modifier;
	
	private static ArrayList<String> xpaths = new ArrayList<String>();
	private static int[] parameters;
	
	private static Input[] collectedInputs;
	
	public static void parseFile(File file, ArrayList<String> paths) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		System.out.println("File: " + file.getName());
		System.out.println("Path: " + file.getAbsolutePath());
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(file.getAbsolutePath());
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
//		for (int i=0; i<xpaths.size(); i++) {
//			System.out.println(xpaths.get(i).toString());
//		}
		
		for (int i=0; i<xpaths.size(); i++) {
			switch (xpaths.get(i).toString()) {
				case "//FuzzyController/@name":
					fuzzyControllerName = getSingleResult(xpath.compile(xpaths.get(i)), doc);
					break;
				case "//FuzzyController/@ip":
					fuzzyControllerIP = getSingleResult(xpath.compile(xpaths.get(i)), doc);
					break;
				case "//FuzzyVariable/@name":
					NodeList res = getMultipleResults(xpath.compile(xpaths.get(i)), doc);
					for (int j=0; j<res.getLength(); j++) {
						fuzzyVariableName = res.item(j).getNodeValue();
						fuzzyVariableDomainLeft = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/@domainleft"), doc);
						fuzzyVariableDomainRight = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/@domainright"), doc);
						fuzzyVariableScale = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/@scale"), doc);
						fuzzyVariableType = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/@type"), doc);
						NodeList variableTerms = getMultipleResults(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm"), doc);
						for (int k=0; k<variableTerms.getLength(); k++) {
							fuzzyTermName = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/@name"), doc);
							fuzzyTermComplement = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/@complement"), doc);
							functionName = getSingleResult(xpath.compile("name(//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/*)"), doc);
							NodeList functionParams = getMultipleResults(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/*/@*"), doc);
							parameters = new int[functionParams.getLength()];
							for (int l=0; l<functionParams.getLength(); l++) {
								parameters[l] = Integer.parseInt(functionParams.item(l).getNodeValue());
							}
						}
					}
					break;
				case "//RuleBase/@name":
					NodeList ruleBases = getMultipleResults(xpath.compile(xpaths.get(i)), doc);
					for (int j=0; j<ruleBases.getLength(); j++) {
						ruleBaseName = getSingleResult(xpath.compile(xpaths.get(i)), doc);
						ruleBaseAndMethod = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@andMethod"), doc);
						ruleBaseOrMethod = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@orMethod"), doc);
						ruleBaseActivationMethod = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@activationMethod"), doc);
						ruleBaseType = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@type"), doc);
						NodeList rules = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule/@name"), doc);
						for (int k=0; k<rules.getLength(); k++) {
							ruleName = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@name"), doc);
							ruleConnector = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@connector"), doc);
							ruleOperator = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@operator"), doc);
							ruleWeight = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@weight"), doc);
							
							NodeList antecedent = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause"), doc);
							for (int l=0; l<antecedent.getLength(); l++) {
								modifier = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause/@modifier"), doc);
								clauseVariables = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause/Variable/text()"), doc);
								clauseTerms = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause/Term/text()"), doc);
								antecedentVars = new String[clauseVariables.getLength()];
								antecedentTerms = new String[clauseTerms.getLength()];
								for (int m=0; m<antecedent.getLength(); m++) {
									antecedentVars[m] = clauseVariables.item(m).getNodeValue();
									if (!modifier.isEmpty()) {
										antecedentTerms[m] = modifier + " " + clauseTerms.item(m).getNodeValue();
									} else {
										antecedentTerms[m] = clauseTerms.item(m).getNodeValue();
									}
								}
							}
							
							NodeList consequent = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause"), doc);
							for (int l=0; l<consequent.getLength(); l++) {
								modifier = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause/@modifier"), doc);
								clauseVariables = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause/Variable/text()"), doc);
								clauseTerms = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause/Term/text()"), doc);
								consequentVars = new String[clauseVariables.getLength()];
								consequentTerms = new String[clauseTerms.getLength()];
								for (int m=0; m<consequent.getLength(); m++) {
									consequentVars[m] = clauseVariables.item(m).getNodeValue();
									if (!modifier.isEmpty()) {
										consequentTerms[m] = modifier+ " " + clauseTerms.item(m).getNodeValue();
									} else {
										consequentTerms[m] = clauseTerms.item(m).getNodeValue();
									}
								}
							}
							
							for (int n=0; n<antecedent.getLength(); n++) {
								System.out.println(antecedentVars[n] + " " + antecedentTerms[n]);
							}
							
							for (int o=0; o<consequent.getLength(); o++) {
								System.out.println(consequentVars[o] + " " + consequentTerms[o]);
							}
						}
					}
					break;
				default:
					break;
			}
		}
	}
	
	private static String getSingleResult(XPathExpression expr, Document doc) throws XPathExpressionException {
		Object result = expr.evaluate(doc);
		String singleResult = result.toString();
		return singleResult;
	}
	
	private static NodeList getMultipleResults(XPathExpression expr, Document doc) throws XPathExpressionException {
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList multipleResults = (NodeList) result;
		return multipleResults;
	}
	
	public static String getFuzzyControllerName() {
		return fuzzyControllerName;
	}
	
	public static String fuzzyControllerIP() {
		return fuzzyControllerIP;
	}
	
	public static ArrayList<String> getExpressions(File mapping) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
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
		
		return xpaths;
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
		
		xpaths = getExpressions(mapping);
		parseFile(exampleFML, xpaths);
		
//		SINGLE ANSWER
//		doc = builder.parse(exampleFML);
//		expr = xpath.compile("name(//FuzzyVariable[@name=\'" + variableName + "\']/FuzzyTerm[@name=\'" + termName + "\']/*)");
//		Object mfNameObject = expr.evaluate(doc);
//		String mfName = mfNameObject.toString();
//		System.out.println(mfName);
		
//		MULTIPLE ANSWERS
//		doc = builder.parse(exampleFML);
//		expr = xpath.compile("//FuzzyVariable[@name=\'building\']/FuzzyTerm[@name=\'good\']/*/@*");
//		Object attr = expr.evaluate(doc, XPathConstants.NODESET);
//		NodeList attrs = (NodeList) attr;
//		for (int i=0; i<attrs.getLength(); i++) {
//			System.out.println(attrs.item(i).getNodeValue());
//		}		
	}
}

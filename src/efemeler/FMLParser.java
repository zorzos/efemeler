package efemeler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import generic.*;
import type1.sets.*;
import type1.system.*;

public class FMLParser {
	
	private static String fuzzyControllerName, fuzzyControllerIP;
	private static String fuzzyVariableName, fuzzyVariableDomainLeft, fuzzyVariableDomainRight, fuzzyVariableScale, fuzzyVariableType, fuzzyVariableDefaultValue, fuzzyVariableAccumulation, fuzzyVariableDefuzzifier;
	private static String fuzzyTermName, fuzzyTermComplement;
	private static String functionName;
	private static String ruleBaseName, ruleBaseAndMethod, ruleBaseOrMethod, ruleBaseActivationMethod, ruleBaseType;
	private static String ruleName, ruleConnector, ruleOperator, ruleWeight;
	private static NodeList clauseVariables, clauseTerms;
	private static String[] antecedentVars, antecedentTerms, consequentVars, consequentTerms;

	
	private static ArrayList<String> xpaths = new ArrayList<String>();
	private static double[] parameters;
	
	private static Input[] collectedInputs;
	private static Output output;
	private static T1_Antecedent[] ants;
	private static T1_Consequent[] cons;
	private static Exporter systemFile;
	private static ArrayList<T1MF_Prototype> functions;
	private static String[] rulebaseNames;
	private static int inputCount, outputCount = 0;
	
	public FMLParser(String name) throws IOException {
		systemFile = new Exporter(name);
	}
	
	public static void closeUp() {
		systemFile.closeUp();
	}
	
	public static void parseFile(File file, ArrayList<String> paths) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
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
						Tuple domain = new Tuple(Double.parseDouble(fuzzyVariableDomainLeft), Double.parseDouble(fuzzyVariableDomainRight));
						
						if (fuzzyVariableType.equals("Input")) {
							Input variable = new Input(fuzzyVariableName, domain);
							variable.setScale(fuzzyVariableScale);
							inputCount++;
						}
						
						if (fuzzyVariableType.equals("Output")) {
							output = new Output(fuzzyVariableName, domain);
							output.setScale(fuzzyVariableScale);
							outputCount++;
						}
						
						NodeList variableTerms = getMultipleResults(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm"), doc);
						for (int k=0; k<variableTerms.getLength(); k++) {
							fuzzyTermName = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/@name"), doc);
							fuzzyTermComplement = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/@complement"), doc);
							functionName = getSingleResult(xpath.compile("name(//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/*)"), doc);
							NodeList functionParams = getMultipleResults(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/*/@*"), doc);
							parameters = new double[functionParams.getLength()];
							
							for (int l=0; l<functionParams.getLength(); l++) {
								parameters[l] = Double.parseDouble(functionParams.item(l).getNodeValue());
							}
							
							switch (functionName) {
								case "TrapezoidShape":
									T1MF_Trapezoidal trapezoidal = new T1MF_Trapezoidal(fuzzyTermName, parameters);
									systemFile.writeMembershipFunction(trapezoidal);
									functions.add(trapezoidal);
									break;
								case "TriangularShape":
									T1MF_Triangular triangular = new T1MF_Triangular(fuzzyTermName, parameters[0], parameters[1], parameters[2]);
									systemFile.writeMembershipFunction(triangular);
									functions.add(triangular);
									break;
								case "GaussianShape":
									T1MF_Gaussian gaussian = new T1MF_Gaussian(fuzzyTermName, parameters[0], parameters[1]);
									systemFile.writeMembershipFunction(gaussian);
									functions.add(gaussian);
									break;
								case "SingletonShape":
									T1MF_Singleton singleton = new T1MF_Singleton(fuzzyTermName, parameters[0]);
									systemFile.writeMembershipFunction(singleton);
									functions.add(singleton);
									break;
							}
						}
					}
					break;
				case "//RuleBase/@name":
					NodeList ruleBases = getMultipleResults(xpath.compile(xpaths.get(i)), doc);
					rulebaseNames = new String[ruleBases.getLength()];
					for (int j=0; j<ruleBases.getLength(); j++) {
						rulebaseNames[j] = ruleBases.item(j).toString();
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
							ants = new T1_Antecedent[antecedent.getLength()];
							for (int l=0; l<antecedent.getLength(); l++) {
								clauseVariables = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause/Variable/text()"), doc);
								clauseTerms = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause/Term/text()"), doc);
								antecedentVars = new String[clauseVariables.getLength()];
								antecedentTerms = new String[clauseTerms.getLength()];
								for (int m=0; m<antecedent.getLength(); m++) {
									antecedentVars[m] = clauseVariables.item(m).getNodeValue();
									antecedentTerms[m] = clauseTerms.item(m).getNodeValue();
								}
								
																
								for (int n=0; n<clauseVariables.getLength(); n++) {
									for (int o=0; o<collectedInputs.length; o++) {
										if (clauseVariables.item(n).toString().equals(collectedInputs[o].getName())) {
											for (int p=0; p<functions.size(); p++) {
												if (clauseTerms.item(n).toString().equals(functions.get(p).getName())) {
													T1_Antecedent ant = new T1_Antecedent(clauseTerms.item(n).toString() + clauseVariables.item(n).toString(), functions.get(p), collectedInputs[o]);
													systemFile.writeAntecedent(ant);
													ants[l] = ant;
												}
											}
										}
									}
								}
							}
							
							
							
							NodeList consequent = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause"), doc);
							cons = new T1_Consequent[consequent.getLength()];
							for (int l=0; l<consequent.getLength(); l++) {
								clauseVariables = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause/Variable/text()"), doc);
								clauseTerms = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause/Term/text()"), doc);
								consequentVars = new String[clauseVariables.getLength()];
								consequentTerms = new String[clauseTerms.getLength()];
								for (int m=0; m<consequent.getLength(); m++) {
									consequentVars[m] = clauseVariables.item(m).getNodeValue();
									consequentTerms[m] = clauseTerms.item(m).getNodeValue();
								}
								
								for (int n=0; n<clauseVariables.getLength(); n++) {
									if (clauseVariables.item(n).toString().equals(output.getName())) {
										for (int p=0; p<functions.size(); p++) {
											if (clauseTerms.item(n).toString().equals(functions.get(p).getName())) {
												T1_Consequent con = new T1_Consequent(clauseTerms.item(n).toString() + clauseVariables.item(n).toString(), functions.get(p), output);
												systemFile.writeConsequent(con);
												cons[l] = con;
											}
										}
									}
								}
							}
							systemFile.writeRule(ruleBaseName, ants, cons);
						}
						systemFile.writeRuleBase(ruleBaseName, rules.getLength());
						systemFile.writeResult(ruleBaseName);
					}
					systemFile.prepare(fuzzyControllerName, collectedInputs, output, rulebaseNames);
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
		File exampleFML = new File("tipperNew.fml");
		File mapping = new File("fmlMapping.xml");
		
		xpaths = getExpressions(mapping);
		parseFile(exampleFML, xpaths);		
	}
}

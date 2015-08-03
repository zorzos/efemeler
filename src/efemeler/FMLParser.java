/**
 * Class FMLParser
 * 
 * @author Rafail Zorzos
 * @version 1.0 August 2015
 */
package efemeler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

	
	private static ArrayList<String> xpaths = new ArrayList<String>();
	private static double[] parameters;
	
	private static ArrayList<Input> collectedInputs = new ArrayList<Input>();
	private static ArrayList<Output> collectedOutputs = new ArrayList<Output>();
	private static ArrayList<T1_Antecedent> ants = new ArrayList<T1_Antecedent>();
	private static ArrayList<T1_Consequent> cons = new ArrayList<T1_Consequent>();
	private static T1_Antecedent[] newAnts;
	private static T1_Consequent[] newCons;
	private static String[] rulebaseNames;
	private static int[] rulebaseSizes;
	private static PrintWriter newFIS;
	private static Map<String, ArrayList<T1_Rule>> ruleMap = new HashMap<String, ArrayList<T1_Rule>>();
	private static Map<String, ArrayList<T1MF_Prototype>> mappedFunctions = new HashMap<String, ArrayList<T1MF_Prototype>>();
	
	/**
	 * Creates an instance of FMLParser
	 * Gets the name of the system and creates the system folder and file.
	 * 
	 * @param file the folder to be created for storing the system files
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public FMLParser(File file) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(file.getAbsolutePath());
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		String systemName = getSingleResult(xpath.compile("//FuzzyController/@name"), doc);
		File directory = new File(systemName);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		newFIS = new PrintWriter(System.getProperty("user.dir") + File.separator + systemName + File.separator + systemName + ".java", "UTF-8");
	}
	
	/**
	 * Parses the specified file and fills the class variables with the information describing the Fuzzy inference system.
	 * Afterwards it calls the rest of the methods of this class to build the system file.
	 * 
	 * @param file the Fuzzy Markup Language file to be processed
	 * @param paths the file containing the XPath expressions to be evaluated over the first file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws InvalidFMLException 
	 */
	public static void parseFile(File file, ArrayList<String> paths) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, InvalidFMLException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(file.getAbsolutePath());
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
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
						ArrayList<T1MF_Prototype> functions = new ArrayList<T1MF_Prototype>();
						fuzzyVariableName = res.item(j).getNodeValue();
						fuzzyVariableDomainLeft = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/@domainleft"), doc);
						fuzzyVariableDomainRight = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+fuzzyVariableName+"\"]/@domainright"), doc);
						fuzzyVariableScale = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+fuzzyVariableName+"\"]/@scale"), doc);
						fuzzyVariableType = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+fuzzyVariableName+"\"]/@type"), doc);
						Tuple domain = new Tuple(Double.parseDouble(fuzzyVariableDomainLeft), Double.parseDouble(fuzzyVariableDomainRight));
						
						if (fuzzyVariableType.equals("input")) {
							Input inVar = new Input(fuzzyVariableName, domain);
							inVar.setScale(fuzzyVariableScale);
							collectedInputs.add(inVar);
						}
						
						if (fuzzyVariableType.equals("output")) {
							Output outVar = new Output(fuzzyVariableName, domain);
							outVar.setScale(fuzzyVariableScale);
							collectedOutputs.add(outVar);
						}
						
						NodeList variableTerms = getMultipleResults(xpath.compile("//FuzzyVariable[@name=\""+res.item(j).getNodeValue()+"\"]/FuzzyTerm/@name"), doc);
						for (int k=0; k<variableTerms.getLength(); k++) {
							fuzzyTermName = getSingleResult(xpath.compile("//FuzzyVariable[@name=\""+fuzzyVariableName+"\"]/FuzzyTerm[@name=\""+variableTerms.item(k).getNodeValue()+"\"]/@name"), doc);
							functionName = getSingleResult(xpath.compile("name(//FuzzyVariable[@name=\""+fuzzyVariableName+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/*)"), doc);
							NodeList functionParams = getMultipleResults(xpath.compile("//FuzzyVariable[@name=\""+fuzzyVariableName+"\"]/FuzzyTerm[@name=\""+fuzzyTermName+"\"]/*/@*"), doc);
							parameters = new double[functionParams.getLength()];
							
							for (int l=0; l<functionParams.getLength(); l++) {
								parameters[l] = Double.parseDouble(functionParams.item(l).getNodeValue());
							}
							
							String newVarName = fuzzyVariableName.replace(fuzzyVariableName.substring(0, 1), fuzzyVariableName.substring(0, 1).toUpperCase());
							
							switch (functionName) {
								case "TrapezoidShape":
									T1MF_Trapezoidal trapezoidal = new T1MF_Trapezoidal(fuzzyTermName, parameters);
									functions.add(trapezoidal);
									break;
								case "TriangularShape":
									T1MF_Triangular triangular = new T1MF_Triangular(fuzzyTermName, parameters[0], parameters[1], parameters[2]);
									functions.add(triangular);
									break;
								case "GaussianShape":
									T1MF_Gaussian gaussian = new T1MF_Gaussian(fuzzyTermName, parameters[0], parameters[1]);
									functions.add(gaussian);
									break;
								case "SingletonShape":
									T1MF_Singleton singleton = new T1MF_Singleton(fuzzyTermName, parameters[0]);
									functions.add(singleton);
									break;
								case "RightLinearShape":
									break;
								case "LeftLinearShape":
									break;
								case "PiShape":
									break;
								case "RightGaussianShape":
									break;
								case "SShape":
									break;
								case "ZShape":
									break;
								case "RectangularShape":
									break;
								default:
									break;
							}
						}
						mappedFunctions.put(fuzzyVariableName, functions);
					}
					break;
				case "//RuleBase/@name":
					NodeList ruleBases = getMultipleResults(xpath.compile(xpaths.get(i)), doc);
					rulebaseNames = new String[ruleBases.getLength()];
					rulebaseSizes = new int[ruleBases.getLength()];
					for (int j=0; j<ruleBases.getLength(); j++) {
						ArrayList<T1_Rule> rulesList = new ArrayList<T1_Rule>();
						rulebaseNames[j] = getVariableName(ruleBases.item(j).getNodeValue().toString());
						ruleBaseName = getSingleResult(xpath.compile(xpaths.get(i)), doc);
						ruleBaseAndMethod = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@andMethod"), doc);
						ruleBaseOrMethod = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@orMethod"), doc);
						ruleBaseActivationMethod = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@activationMethod"), doc);
						ruleBaseType = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/@type"), doc);
						NodeList rules = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule/@name"), doc);
						rulebaseSizes[j] = rules.getLength();
						for (int k=0; k<rules.getLength(); k++) {
							ants.clear();
							cons.clear();
							ruleName = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@name"), doc);
							ruleConnector = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@connector"), doc);
							ruleOperator = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@operator"), doc);
							ruleWeight = getSingleResult(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+rules.item(k).getNodeValue()+"\"]/@weight"), doc);
							
							clauseVariables = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause/Variable/text()"), doc);
							clauseTerms = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Antecedent/Clause/Term/text()"), doc);
							for (int n=0; n<clauseVariables.getLength(); n++) {
								ArrayList<T1MF_Prototype> func = mappedFunctions.get(clauseVariables.item(n).getNodeValue());
								for (int o=0; o<func.size(); o++) {
									if (clauseTerms.item(n).getNodeValue().equals(func.get(o).getName())) {
										for (int p=0;  p<collectedInputs.size(); p++) {
											if (clauseVariables.item(n).getNodeValue().equals(collectedInputs.get(p).getName())) {
												T1_Antecedent ant = new T1_Antecedent(getVariableName(clauseTerms.item(n).getNodeValue().toString() + clauseVariables.item(n).getNodeValue().toString()), func.get(o), collectedInputs.get(p));
												ants.add(ant);
											}
										}
									}
								}
							}					
							
							clauseVariables = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause/Variable/text()"), doc);
							clauseTerms = getMultipleResults(xpath.compile("//RuleBase[@name=\""+ruleBaseName+"\"]/Rule[@name=\""+ruleName+"\"]/Consequent/Clause/Term/text()"), doc);
							for (int n=0; n<clauseVariables.getLength(); n++) {
								ArrayList<T1MF_Prototype> func = mappedFunctions.get(clauseVariables.item(n).getNodeValue());
								for (int o=0; o<func.size(); o++) {
									if (clauseTerms.item(n).getNodeValue().equals(func.get(o).getName())) {
										for (int p=0;  p<collectedOutputs.size(); p++) {
											if (clauseVariables.item(n).getNodeValue().equals(collectedOutputs.get(p).getName())) {
												T1_Consequent con = new T1_Consequent(getVariableName(clauseTerms.item(n).getNodeValue().toString() + clauseVariables.item(n).getNodeValue().toString()), func.get(o), collectedOutputs.get(p));
												cons.add(con);
											}
										}
									}
								}
							}
							newAnts = new T1_Antecedent[ants.size()];
							for (int y=0; y<ants.size(); y++) {
								newAnts[y] = ants.get(y);
							}
							
							newCons = new T1_Consequent[cons.size()];
							for (int u=0; u<cons.size(); u++) {
								newCons[u] = cons.get(u);
							}
							
							T1_Rule rule = new T1_Rule(newAnts, newCons);
							rulesList.add(rule);
						}
						ruleMap.put(ruleBaseName, rulesList);
					}
					break;
				default:
					break;
			}
		}
		
		// Prepare file - writing variables
		if (!fuzzyControllerName.equals("") && collectedInputs.size() > 0 && collectedOutputs.size() > 0 && rulebaseNames.length > 0) {
			prepare(fuzzyControllerName, collectedInputs, collectedOutputs, rulebaseNames);
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		// Write input variable declarations
		writeInputs(collectedInputs);
		
		// Write output variable declarations
		writeOutputs(collectedOutputs);
		
		// Write membership functions
		if (mappedFunctions.size() > 0) {
			for (Map.Entry<String, ArrayList<T1MF_Prototype>> entry : mappedFunctions.entrySet()) {
				for (int x=0; x<entry.getValue().size(); x++) {
					writeMembershipFunction(entry.getKey(), entry.getValue().get(x));
				}
			}
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		// Write antecedents
		if (newAnts.length > 0) {
			for (int y=0; y<newAnts.length; y++) {
				writeAntecedent(newAnts[y]);
			}
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		// Write consequents
		if (newCons.length > 0) {
			for (int z=0; z<newCons.length; z++) {
				writeConsequent(newCons[z]);
			}
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		// Write rulebases
		if (rulebaseNames.length > 0) {
			for (int h=0; h<rulebaseNames.length; h++) {
				writeRuleBase(rulebaseNames[h], rulebaseSizes[h]);
			}
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		// Write rules
		if (ruleMap.size() > 0) {
			for (Map.Entry<String, ArrayList<T1_Rule>> entry : ruleMap.entrySet()) {
			 	for (int f=0; f<entry.getValue().size(); f++) {
			 		writeRule(entry.getKey(), entry.getValue().get(f));
				}
			 }
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		// Write result method
		if (rulebaseNames.length > 0 ) {
			for (int b=0; b<rulebaseNames.length; b++) {
				writeResult(rulebaseNames[b]);
			}
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		if (!fuzzyControllerName.equals("")) {
			writeMain(fuzzyControllerName);
		} else {
			throw new InvalidFMLException("The Fuzzy Markup Language file provided is invalid. Please select again.");
		}
		
		closeUp();
	}
	
	/**
	 * Closes the file created in the constructor after all of the information has been stored.
	 */
	public static void closeUp() {
		newFIS.println("\t}");
		newFIS.println("}");
		newFIS.close();
	}

	/**
	 * Method aiding in retrieving results of an XPath expression for when the result is singular
	 * 
	 * @param expr the XPath expression to be evaluated
	 * @param doc the Document object the expression is evaluated over
	 * @return the resulting String
	 * @throws XPathExpressionException
	 */
	private static String getSingleResult(XPathExpression expr, Document doc) throws XPathExpressionException {
		Object result = expr.evaluate(doc);
		String singleResult = result.toString();
		return singleResult;
	}
	
	/**
	 * Method aiding in retrieving results of an XPath expression for when the result is a list
	 * 
	 * @param expr the XPath expression to be evaluated
	 * @param doc the Document object the expression is evaluated over
	 * @return the NodeList containing the results
	 * @throws XPathExpressionException
	 */
	private static NodeList getMultipleResults(XPathExpression expr, Document doc) throws XPathExpressionException {
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList multipleResults = (NodeList) result;
		return multipleResults;
	}
	
	/**
	 * Method for parsing over a certain pre-defined file to retrieve the XPath expressions to evaluate
	 * 
	 * @param mapping the XML file containing the expressions
	 * @return the ArrayList containing the expressions
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
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
	
	/**
	 * Copies entire certain folders to the specified location
	 * 
	 * @param source the folder to be copied
	 * @param target the location to be copied to
	 * @throws IOException
	 */
	private static void copyFolder(File source, File target) throws IOException {
		if(source.isDirectory()){
			 
    		//if directory does not exist, create it
    		if(!target.exists()){
    			target.mkdir();
 
    			//list all the directory contents
    			String files[] = source.list();
 
	    		for (String file : files) {
	    		   //construct the src and dest file structure
	    		   File srcFile = new File(source, file);
	    		   File destFile = new File(target, file);
	    		   //recursive copy
	    		   copyFolder(srcFile,destFile);
	    		}
    		}
    	} else {
    		//if file, then copy it
    		//Use bytes stream to support all file types
	    		InputStream in = new FileInputStream(source);
    	        OutputStream out = new FileOutputStream(target); 
 
    	        byte[] buffer = new byte[1024];
 
    	        int length;
    	        //copy the file content in bytes 
    	        while ((length = in.read(buffer)) > 0){
    	    	   out.write(buffer, 0, length);
    	        }
 
    	        in.close();
    	        out.close();
	    }
	}
	
	/**
	 * A method for constructing the variable name from a String,
	 * used for when the system needs to write variable names to a file without knowing the actual variable name.
	 * 
	 * @param name the name of the variable
	 * @return the variable name 
	 */
	private static String getVariableName(String name) {
		name = name.toLowerCase();
		String[] words = name.split(" ");
		for (int i=1; i<words.length; i++) {
			words[i] = words[i].replace(words[i].substring(0, 1), words[i].substring(0, 1).toUpperCase());
		}
		String newName = "";
		for (int j=0; j<words.length; j++) {
			newName = newName + words[j];
		}
		return newName;
	}
	
	/**
	 * This method sets up the file structure for exporting code
	 * It copies the source folders to a specified location and writes the
	 * variable declarations to file.
	 * 
	 * @param systemName the name of the system
	 * @param inputs the list of input variables
	 * @param outputs the list of output variables
	 * @param rbNames the list of rulebase names
	 */
	public static void prepare(String systemName, ArrayList<Input> inputs, ArrayList<Output> outputs, String[] rbNames) {
		try {
			File directory = new File(systemName);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			File[] sources  = { new File(System.getProperty("user.dir") + "/src/generic/"), 
								new File(System.getProperty("user.dir") + "/src/tools/"), 
								new File(System.getProperty("user.dir") + "/src/type1/") };

			File[] targets = { 	new File(System.getProperty("user.dir") + File.separator + systemName + "/generic/"), 
								new File(System.getProperty("user.dir") + File.separator + systemName + "/tools/"), 
								new File(System.getProperty("user.dir") + File.separator + systemName + "/type1/") };

			for (int i=0; i<sources.length; i++) {
				copyFolder(sources[i], targets[i]);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		newFIS.println("import generic.*;");
		newFIS.println("import type1.sets.*;");
		newFIS.println("import type1.system.*;");
		newFIS.println();
		newFIS.println("public class " + systemName + " { ");
		newFIS.println();
		for (int i=0; i<inputs.size(); i++) {
			newFIS.println("\tInput " + inputs.get(i).getName() + ";");
		}
		
		newFIS.println();
		for (int i=0; i<outputs.size(); i++) {
			newFIS.println("\tOuput " + outputs.get(i).getName() + ";");
		}
		newFIS.println();
		for (int i=0; i<rbNames.length; i++) {
			newFIS.println("\tT1_Rulebase " + rbNames[i] + ";");
			newFIS.println();
		}
		
		newFIS.println("\tpublic " + systemName + "() { ");
		newFIS.println();
	}
	
	/**
	 * This method writes input variable declarations to file
	 * 
	 * @param vars the list of input variables
	 */
	public static void writeInputs(ArrayList<Input> vars) {
		for (int i=0; i<vars.size(); i++) {
			newFIS.println("\t\t" + vars.get(i).getName() + " = new Input(\""+vars.get(i).getName()+"\", new Tuple("+ vars.get(i).getDomain().getLeft() + ", " + vars.get(i).getDomain().getRight() + "));");
		}
		newFIS.println();
		collectedInputs = vars;
	}
	
	/**
	 * This method writes output variable declarations to file
	 * 
	 * @param vars the list of output variables
	 */
	public static void writeOutputs(ArrayList<Output> vars) {
		for (int i=0; i<vars.size(); i++) {
			newFIS.println("\t\t" + vars.get(i).getName() + " = new Output(\""+vars.get(i).getName()+"\", new Tuple("+ vars.get(i).getDomain().getLeft() + ", " + vars.get(i).getDomain().getRight() + "));");
		}
		newFIS.println();
		collectedOutputs = vars;
	}
	
	/**
	 * This method writes membership function declarations to file
	 * 
	 * @param varName the name of the membership function
	 * @param mf the membership function
	 */
	public static void writeMembershipFunction(String varName, T1MF_Prototype mf) {
		String variableName = getVariableName(mf.getName()) + getVariableName(varName) + "MF";
		String mfType = mf.getClass().getSimpleName();
		switch (mfType) {
			case "T1MF_Singleton":
				T1MF_Singleton singleton = (T1MF_Singleton) mf;
				newFIS.println("\t\t" + mfType + " " + variableName + " = new " + mfType + "(\"" + singleton.getName() + "\", " + singleton.getValue() + ");");
				newFIS.println();
				break;
			case "T1MF_Gaussian":
				T1MF_Gaussian gaussian = (T1MF_Gaussian) mf;
				newFIS.println("\t\t" + mfType + " " + variableName + " = new " + mfType + "(\"" + gaussian.getName() + "\", " + gaussian.getMean() + ", " + gaussian.getSpread() + ");");
				newFIS.println();
				break;
			case "T1MF_Gauangle":
				T1MF_Gauangle gauangle = (T1MF_Gauangle) mf;
				newFIS.println("\t\t" + mfType + " " + variableName + " = new " + mfType + "(\"" + gauangle.getName() + "\", " + gauangle.getStart() + ", " + gauangle.getMean() + ", " + gauangle.getEnd() + ");");
				newFIS.println();
				break;
			case "T1MF_Triangular":
				T1MF_Triangular triangular = (T1MF_Triangular) mf;
				newFIS.println("\t\t" + mfType + " " + variableName + " = new " + mfType + "(\"" + triangular.getName() + "\", " + triangular.getStart() + ", " + triangular.getPeak() + ", " + triangular.getEnd() + ");");
				newFIS.println();
				break;
			case "T1MF_Trapezoidal":
				T1MF_Trapezoidal trapezoidal = (T1MF_Trapezoidal) mf;
				newFIS.println("\t\tdouble[] " + variableName + "Parameters = new double[4];");
				newFIS.println("\t\tparameters[0] = " + trapezoidal.getA());
				newFIS.println("\t\tparameters[1] = " + trapezoidal.getB());
				newFIS.println("\t\tparameters[2] = " + trapezoidal.getC());
				newFIS.println("\t\tparameters[3] = " + trapezoidal.getD());
				newFIS.println();
				newFIS.println("\t\t" + mfType + " " + variableName + " = new " + mfType + "(\"" + trapezoidal.getName() + "\", " + "parameters);");
				newFIS.println();
				break;
		}
	}
	
	/**
	 * This method writes antecedent declarations to file.
	 * 
	 * @param antecedent the antecedent to be written
	 */
	public static void writeAntecedent(T1_Antecedent antecedent) {
		newFIS.println("\t\tT1_Antecedent " + getVariableName(antecedent.getName()) + " = new T1_Antecedent(\"" + antecedent.getName() + "\", " + getVariableName(antecedent.getMF().getName()) + antecedent.getInput().getName() + "MF, " + getVariableName(antecedent.getInput().getName()) + ");");
		newFIS.println();
	}

	/**
	 * This method writes consequent declarations to file.
	 * 
	 * @param consequent the consequent to be written
	 */
	public static void writeConsequent(T1_Consequent consequent) {
		newFIS.println("\t\tT1_Consequent " + getVariableName(consequent.getName()) + " = new T1_Consequent(\"" + consequent.getName() + "\", " + getVariableName(consequent.getMF().getName()) + consequent.getOutput().getName() + "MF, " + getVariableName(consequent.getOutput().getName()) + ");");
		newFIS.println();
	}
	
	/**
	 * This method writes rulebase declarations to file
	 * 
	 * @param name the name of the rulebase
	 * @param size the size of the rulebase
	 */
	public static void writeRuleBase(String name, int size) {
		newFIS.println("\t\t" + name + " = new T1_Rulebase(" + size +");");
		newFIS.println();
	}
	
	/**
	 * This method writes rule declarations to file
	 * 
	 * @param rulebase the rulebase name the rule to be written belongs to
	 * @param rule the rule object
	 */
	public static void writeRule(String rulebase, T1_Rule rule) {
		String antecedentNames = "";
		for (int i=0; i<rule.getAntecedents().length; i++) {
			if (i==0) {
				antecedentNames += getVariableName(rule.getAntecedents()[i].getName()) + "MF";
			} else {
				antecedentNames += ", " + getVariableName(rule.getAntecedents()[i].getName()) + "MF";
			}
		}
		
		String consequentNames = "";
		for (int j=0; j<rule.getConsequents().length; j++) {
			if (j==0) {
				consequentNames += getVariableName(rule.getConsequents()[j].getName());
			} else {
				consequentNames += ", " + getVariableName(rule.getConsequents()[j].getName());
			}
		}
		newFIS.println("\t\t" + getVariableName(rulebase) + ".addRule(new T1_Rule(new T1_Antecedent[]{" + antecedentNames + "}, new T1_Consequent[]{" + consequentNames + "}));");
		newFIS.println();
	}

	/**
	 * This method writes the getResult() method to file
	 * 
	 * @param rulebaseName the rulebase name the inputs are evaluated over.
	 */
	public static void writeResult(String rulebaseName) {
		String inputString = "";
		String[] setInputs = new String[collectedInputs.size()];
		String[] systemOuts = new String[collectedInputs.size() + 2];
		for (int i=0; i<collectedInputs.size(); i++) {
			if (i==0) {
				inputString += "double " + getVariableName(collectedInputs.get(i).getName()) + "Input";
			} else {
				inputString += ", double " + getVariableName(collectedInputs.get(i).getName()) + "Input";
			}
			setInputs[i] = getVariableName("\t\t\t" + collectedInputs.get(i).getName()) + ".setInput(" + getVariableName(collectedInputs.get(i).getName()) + "Input);";
			systemOuts[i] = "\t\t\tSystem.out.println(\"The "+ collectedInputs.get(i).getName() +" was: \" + " + getVariableName(collectedInputs.get(i).getName()) + ".getInput());";
		}
		
		newFIS.println("\t\tpublic void getResult(" + inputString + ") {");
		for (int j=0; j<setInputs.length; j++) {
			newFIS.println(setInputs[j]);
		}
		
		newFIS.println("\t\t\tTreeMap<Output, Double> output;");
		newFIS.println("\t\t\toutput = " + rulebaseName + ".evaluate(0);");
		
		String defuzz = "";
		for (int l=0; l<collectedOutputs.size(); l++) {
			if (l==0) {
				defuzz += collectedOutputs.get(l).getName() + " of\" + output.get(" + getVariableName(collectedOutputs.get(l).getName()) + "));";
			} else {
				defuzz += ", " + collectedOutputs.get(l).getName() + " of\" + output.get(" + getVariableName(collectedOutputs.get(l).getName()) + "));";
			}
		}
		
		systemOuts[collectedInputs.size()] = "\t\t\tSystem.out.println(\"Using height defuzzification, the FLS recommends a " + defuzz;
		
		for (int k=0; k<systemOuts.length-1; k++) {
			newFIS.println(systemOuts[k]);
		}
		
		newFIS.println("\t\t\toutput = " + rulebaseName + ".evaluate(1);");
		newFIS.println("\t\t\tSystem.out.println(\"Using centroid defuzzification, the FLS recommends a " + defuzz);
		
		newFIS.println("\t\t}");
		newFIS.println();
	}
	
	/**
	 * Writes the main method of the system class
	 * 
	 * @param name the system(class) name
	 */
	public static void writeMain(String name) {
		newFIS.println("\t\tpublic static void main(String[] args) {");
		newFIS.println("\t\t\t new " + name + "();" );
		newFIS.println("\t\t}");
	}
	
	// The main method of the FMLParser, used for testing purposes
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, InvalidFMLException {
		File exampleFML = new File("tipperNew.fml");
		File mapping = new File("fmlMapping.xml");

		new FMLParser(exampleFML);
		xpaths = getExpressions(mapping);
		parseFile(exampleFML, xpaths);		
	}
}

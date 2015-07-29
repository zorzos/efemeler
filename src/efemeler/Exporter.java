package efemeler;

import generic.*;
import type1.sets.*;
import type1.system.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.*;

public class Exporter {
	
	private static PrintWriter newFIS = null;
	private static String rulebaseName;
	private static ArrayList<Input> inputs;
	private static ArrayList<Output> outputs;
	
	public Exporter(String name) throws IOException {
		try {
			File systemFolder = new File(name);
			systemFolder.mkdirs();
			newFIS = new PrintWriter(System.getProperty("user.dir") + File.separator + name + File.separator + name + ".java", "UTF-8");
			File[] sources  = { new File(System.getProperty("user.dir") + "/src/generic/"), 
								new File(System.getProperty("user.dir") + "/src/tools/"), 
								new File(System.getProperty("user.dir") + "/src/type1/") };

			File[] targets = { 	new File(System.getProperty("user.dir") + File.separator + name + "/generic/"), 
								new File(System.getProperty("user.dir") + File.separator + name + "/tools/"), 
								new File(System.getProperty("user.dir") + File.separator + name + "/type1/") };

			for (int i=0; i<sources.length; i++) {
				copyFolder(sources[i], targets[i]);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public PrintWriter getFile() {
		return newFIS;
	}
	
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
	
	public static void prepare(String systemName, ArrayList<Input> inputs, ArrayList<Output> outputs, String[] rbNames) {
		try {
			File systemFolder = new File(systemName);
			systemFolder.mkdirs();
			newFIS = new PrintWriter(System.getProperty("user.dir") + File.separator + systemName + File.separator + systemName + ".java", "UTF-8");
			File[] sources  = { new File(System.getProperty("user.dir") + "/src/generic/"), 
								new File(System.getProperty("user.dir") + "/src/tools/"), 
								new File(System.getProperty("user.dir") + "/src/type1/") };

			File[] targets = { 	new File(System.getProperty("user.dir") + File.separator + systemName + "/generic/"), 
								new File(System.getProperty("user.dir") + File.separator + systemName + "/tools/"), 
								new File(System.getProperty("user.dir") + File.separator + systemName + "/type1/") };

			for (int i=0; i<sources.length; i++) {
				copyFolder(sources[i], targets[i]);
			}
			
			newFIS.println("import generic.*;");
			newFIS.println("import type1.sets.*;");
			newFIS.println("import type1.system.*;");
			newFIS.println();
			newFIS.println("public class " + systemName + " { ");
			newFIS.println();
			if (inputs.size() != 0) {
				for (int i=0; i<inputs.size(); i++) {
					newFIS.println("\tInput " + inputs.get(i).getName() + ";");
				}
			} else {
				System.out.println("empty inputs");
				newFIS.println("empty inputs");
			}
			
			newFIS.println();
			for (int i=0; i<outputs.size(); i++) {
				newFIS.println("\tOuput " + outputs.get(i).getName() + ";");
			}
			newFIS.println();
			for (int i=0; i<rbNames.length; i++) {
				newFIS.println("Rulebase " + rbNames[i] + ";");
				newFIS.println();
			}
			
			newFIS.println("\tpublic " + systemName + "() { ");
			newFIS.println();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeInputs(ArrayList<Input> vars) {
		for (int i=0; i<vars.size(); i++) {
			newFIS.println("\t\t" + vars.get(i).getName() + " = new Input(\""+vars.get(i).getName()+"\", new Tuple("+ vars.get(i).getDomain().getLeft() + ", " + vars.get(i).getDomain().getRight() + "));");
		}
		newFIS.println();
		inputs = vars;
	}
	
	public static void writeOutputs(ArrayList<Output> vars) {
		for (int i=0; i<vars.size(); i++) {
			newFIS.println("\t\t" + vars.get(i).getName() + " = new Output(\""+vars.get(i).getName()+"\", new Tuple("+ vars.get(i).getDomain().getLeft() + ", " + vars.get(i).getDomain().getRight() + "));");
		}
		newFIS.println();
		outputs = vars;
	}
	
	public static void writeMembershipFunction(T1MF_Prototype mf) {
		String variableName = getVariableName(mf.getName()) + "MF";
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
				newFIS.println("\t\tdouble[] parameters;");
				newFIS.println("\t\tparameters[0] = " + trapezoidal.getA());
				newFIS.println("\t\tparameters[1] = " + trapezoidal.getB());
				newFIS.println("\t\tparameters[2] = " + trapezoidal.getC());
				newFIS.println("\t\tparameters[3] = " + trapezoidal.getD());
				newFIS.println("\t\tparameters[0] = " + trapezoidal.getA());
				newFIS.println("\t\t" + mfType + " " + variableName + " = new " + mfType + "(\"" + trapezoidal.getName() + "\", " + "parameters);");
				newFIS.println();
				break;
		}
	}
	
	public static void writeAntecedent(T1_Antecedent antecedent) {
		newFIS.println("\t\tT1_Antecedent " + getVariableName(antecedent.getName()) + " = new T1_Antecedent(\"" + antecedent.getName() + "\", " + getVariableName(antecedent.getMF().getName()) + "MF, " + getVariableName(antecedent.getInput().getName()) + ");");
		newFIS.println();
	}

	public static void writeConsequent(T1_Consequent consequent) {
		newFIS.println("\t\tT1_Consequent " + getVariableName(consequent.getName()) + " = new T1_Consequent(\"" + consequent.getName() + "\", " + getVariableName(consequent.getMF().getName()) + "MF, " + getVariableName(consequent.getOutput().getName()) + ");");
		newFIS.println();
	}
	
	public static void writeRuleBase(String name, int size) {
		newFIS.println("\t\t" + name + " = new T1_Rulebase(" + size +");");
		newFIS.println();
	}
	
	public static void writeRule(String rulebase, T1_Rule rule) {
		String antecedentNames = "";
		for (int i=0; i<rule.getAntecedents().length; i++) {
			if (i==0) {
				antecedentNames += getVariableName(rule.getAntecedents()[i].getName());
			} else {
				antecedentNames += ", " + getVariableName(rule.getAntecedents()[i].getName());
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
		newFIS.println("\t\t" + rulebase + ".addRule(new T1_Rule(new T1_Antecedent[]{" + antecedentNames + "}, new T1_Consequent[]{" + consequentNames + "}));");
		newFIS.println();
	}

	public static void writeResult(String rulebaseName) {
		String inputString = "";
		String[] setInputs = new String[inputs.size()];
		String[] systemOuts = new String[inputs.size() + 2];
		for (int i=0; i<inputs.size(); i++) {
			if (i==0) {
				inputString += "double " + getVariableName(inputs.get(i).getName()) + "Input";
			} else {
				inputString += ", double " + getVariableName(inputs.get(i).getName()) + "Input";
			}
			setInputs[i] = getVariableName("\t\t\t" + inputs.get(i).getName()) + ".setInput(" + getVariableName(inputs.get(i).getName()) + "Input);";
			systemOuts[i] = "\t\t\tSystem.out.println(\"The "+ inputs.get(i).getName() +" was: \" + " + getVariableName(inputs.get(i).getName()) + ".getInput());";
		}
		
		newFIS.println("\t\tpublic void getResult(" + inputString + ") {");
		for (int j=0; j<setInputs.length; j++) {
			newFIS.println(setInputs[j]);
		}
		
		newFIS.println("\t\t\tTreeMap<Output, Double> output;");
		newFIS.println("\t\t\toutput = " + rulebaseName + ".evaluate(0);");
		
		String defuzz = "";
		for (int l=0; l<outputs.size(); l++) {
			if (l==0) {
				defuzz += outputs.get(l).getName() + " of output.get(" + getVariableName(outputs.get(l).getName()) + ");";
			} else {
				defuzz += ", " + outputs.get(l).getName() + " of output.get(" + getVariableName(outputs.get(l).getName()) + ");";
			}
		}
		
		systemOuts[inputs.size()] = "\t\t\tSystem.out.println(\"Using height defuzzification, the FLS recommends a " + defuzz;
		
		for (int k=0; k<systemOuts.length-1; k++) {
			newFIS.println(systemOuts[k]);
		}
		
		newFIS.println("\t\t\toutput = " + rulebaseName + ".evaluate(1);");
		newFIS.println("\t\t\tSystem.out.println(\"Using centroid defuzzification, the FLS recommends a " + defuzz);
		
		newFIS.println("\t\t}");
		newFIS.println();
	}
	
	public static void writeMain(String name) {
		newFIS.println("\t\tpublic static void main(String[] args) {");
		newFIS.println("\t\t\t new " + name + "();" );
		newFIS.println("\t\t}");
	}
	
	public static void closeUp() {
		newFIS.println("\t}");
		newFIS.println("}");
		newFIS.close();
	}

	public static void main(String args[]) throws IOException {
		String systemName = "SampleSystem";
		File directory = new File(systemName);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		//newFIS = new PrintWriter(System.getProperty("user.dir") + "/example/" + systemName + ".java", "UTF-8");
		new Exporter(systemName);
		ArrayList<Input> in = new ArrayList<Input>();
		Input food = new Input("food", new Tuple(5.0, 6.0));
		Input service = new Input("service", new Tuple(7.0, 8.0));
		in.add(food);
		in.add(service);
		ArrayList<Output> out = new ArrayList<Output>();
		Output tip = new Output("tip", new Tuple(4.0, 8.0));
		out.add(tip);
		String[] names = new String[1];
		names[0] = "rulebase";
		prepare(systemName, in, out, names);
		writeInputs(in);
		writeOutputs(out);
		T1MF_Triangular badFoodMF = new T1MF_Triangular("Bad Food",0.0, 0.0, 10.0);
		T1MF_Gauangle unfriendlyServiceMF = new T1MF_Gauangle("Unfriendly Service",0.0, 0.0, 6.0);
		T1MF_Gaussian lowTipMF = new T1MF_Gaussian("Low tip", 0.0, 6.0);
		writeMembershipFunction(badFoodMF);
		writeMembershipFunction(unfriendlyServiceMF);
		writeMembershipFunction(lowTipMF);
		T1_Antecedent badFood = new T1_Antecedent("BadFood",badFoodMF, food);
		writeAntecedent(badFood);
		T1_Antecedent unfriendlyService = new T1_Antecedent("UnfriendlyService",unfriendlyServiceMF, service);
		writeAntecedent(unfriendlyService);
		T1_Consequent lowTip = new T1_Consequent("LowTip", lowTipMF, tip);
		writeConsequent(lowTip);
		T1_Antecedent[] sampleAnts = new T1_Antecedent[2];
		T1_Consequent[] sampleCons = new T1_Consequent[1];
		sampleCons[0] = lowTip;
		sampleAnts[0] = badFood;
		sampleAnts[1] = unfriendlyService;
		T1_Rule rule = new T1_Rule(sampleAnts, sampleCons);
		for (int i=0; i<names.length; i++) {
			writeRuleBase(names[i], 6);
		}
		writeRule(names[0], rule);
		writeResult(names[0]);
		writeMain(systemName);
		closeUp();
	}
}

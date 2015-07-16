package efemeler;

import generic.*;
import type1.sets.*;
import type1.system.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Exporter {
	
	private static PrintWriter newFIS;
	private static String rulebaseName;
	private static Input[] inputs;
	private static Output output;
	
	public Exporter(String name) {
		try {
			newFIS = new PrintWriter(name, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public static void prepare(String systemName, Input[] inputs, Output output, String rbName) {
		newFIS.println("import generic.*;");
		newFIS.println("import type1.sets.*;");
		newFIS.println("import type1.system.*;");
		newFIS.println();
		newFIS.println("public class " + systemName + " { ");
		newFIS.println();
		for (int i=0; i<inputs.length; i++) {
			newFIS.println("\tInput " + inputs[i].getName() + ";");
		}
		
		newFIS.println("\tOuput " + output.getName() + ";");
		newFIS.println("\tT1_Rulebase " + rbName + ";");
		newFIS.println();
		newFIS.println("\tpublic " + systemName + "() { ");
		newFIS.println();
		rulebaseName = rbName;
	}
	
	public static void writeInputs(Input[] vars) {
		for (int i=0; i<vars.length; i++) {
			newFIS.println("\t\t" + vars[i].getName() + " = new Input(\""+vars[i].getName()+"\", new Tuple("+ vars[i].getDomain().getLeft() + ", " + vars[i].getDomain().getRight() + "));");
		}
		newFIS.println();
		inputs = vars;
	}
	
	public static void writeOutputVariable(Output var) {
		newFIS.println("\t\t" + var.getName() + " = new Output(\""+var.getName()+"\", new Tuple("+ var.getDomain().getLeft() + ", " + var.getDomain().getRight() + "));");
		newFIS.println();
		output = var;
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
	
	public static void writeRuleBase(int size) {
		newFIS.println("\t\t" + rulebaseName + " = new T1_Rulebase(" + size +");");
		newFIS.println();
	}
	
	public static void writeRule(T1_Antecedent[] antecedents, T1_Consequent consequent) {
		String antecedentNames = "";
		for (int i=0; i<antecedents.length; i++) {
			if (i==0) {
				antecedentNames += getVariableName(antecedents[i].getName());
			} else {
				antecedentNames += ", " + getVariableName(antecedents[i].getName());
			}
		}
		newFIS.println("\t\t" + rulebaseName + ".addRule(new T1_Rule(new T1_Antecedent[]{" + antecedentNames + "}, " + getVariableName(consequent.getName()) + "));");
		newFIS.println();
	}
	
	public static void writeResult() {
		String inputString = "";
		String[] setInputs = new String[inputs.length];
		String[] systemOuts = new String[inputs.length + 2];
		for (int i=0; i<inputs.length; i++) {
			if (i==0) {
				inputString += "double " + getVariableName(inputs[i].getName()) + "Input";
			} else {
				inputString += ", double " + getVariableName(inputs[i].getName()) + "Input";
			}
			setInputs[i] = getVariableName("\t\t\t" + inputs[i].getName()) + ".setInput(" + getVariableName(inputs[i].getName()) + "Input);";
			systemOuts[i] = "\t\t\tSystem.out.println(\"The "+ inputs[i].getName() +" was: \" + " + getVariableName(inputs[i].getName()) + ".getInput());";
		}
		
		newFIS.println("\t\tpublic void getResult(" + inputString + ") {");
		for (int j=0; j<setInputs.length; j++) {
			newFIS.println(setInputs[j]);
		}
		
		systemOuts[inputs.length] = "\t\t\tSystem.out.println(\"Using height defuzzification, the FLS recommends a tip of:\" + " + rulebaseName +".evaluate(0).get(" + getVariableName(output.getName()) + "));";
		systemOuts[inputs.length + 1] = "\t\t\tSystem.out.println(\"Using centroid defuzzification, the FLS recommends a tip of:\" + " + rulebaseName +".evaluate(1).get(" + getVariableName(output.getName()) + "));";
		
		for (int k=0; k<systemOuts.length; k++) {
			newFIS.println(systemOuts[k]);
		}
		
		newFIS.println("\t\t}");
		newFIS.println();
	}
	
	public static void writeMain() {
		newFIS.println("\t\tpublic static void main(String[] args) {");
		newFIS.println("\t\t}");
	}
	
	public static void closeUp() {
		newFIS.println("\t}");
		newFIS.println("}");
		newFIS.close();
	}
	
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
		boolean directory = new File("example").mkdirs();
		if (!directory) {
			System.out.println("failure");
		} else {
			System.out.println("success");
			String systemName = "SampleSystem";
			newFIS = new PrintWriter(System.getProperty("user.dir") + "/example/" + systemName + ".java", "UTF-8");
			Input[] sampleIn = new Input[2];
			Input food = new Input("food", new Tuple(5.0, 6.0));
			Input service = new Input("service", new Tuple(7.0, 8.0));
			sampleIn[0] = food;
			sampleIn[1] = service;
			Output tip = new Output("tip", new Tuple(4.0, 8.0));
			prepare(systemName, sampleIn, tip, "rulebase");
			writeInputs(sampleIn);
			writeOutputVariable(tip);
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
			sampleAnts[0] = badFood;
			sampleAnts[1] = unfriendlyService;
			writeRuleBase(6);
			writeRule(sampleAnts, lowTip);
			writeResult();
			closeUp();
		}
	}
}

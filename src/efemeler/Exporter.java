package efemeler;

import generic.*;
import type1.sets.*;
import type1.system.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Exporter {
	
	private static PrintWriter newFIS;
	private String rulebaseName;
	
	private String getVariableName(String name) {
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
	
	public void prepare(String systemName, Input[] inputs, Output output) {
		newFIS.println("public class " + systemName + " { ");
		
		for (int i=0; i<inputs.length; i++) {
			newFIS.println("\t Input " + inputs[i].getName());
		}
		
		newFIS.println("\t Ouput " + output.getName());
		newFIS.println("\t T1_Rulebase " + rulebaseName);
		
		newFIS.println("\t public " + systemName + " { ");
	}
	
	public void writeInputs(Input[] inputs) {
		for (int i=0; i<inputs.length; i++) {
			newFIS.println("\t \t " + inputs[i].getName() + " = new Input(\""+inputs[i].getName()+"\", new Tuple("+ inputs[i].getDomain().getLeft() + ", " + inputs[i].getDomain().getRight() + "));");
		}
	}
	
	public void writeOutputVariable(Output output) {
		newFIS.println("\t \t " + output.getName() + " = new Output(\""+output.getName()+"\", new Tuple("+ output.getDomain().getLeft() + ", " + output.getDomain().getRight() + "));");
	}
	
	public void writeMembershipFunction(T1MF_Prototype mf) {
		String variableName = getVariableName(mf.getName()) + "MF";
		String mfType = mf.getClass().getSimpleName();
		switch (mfType) {
			case "T1MF_Singleton":
				T1MF_Singleton singleton = (T1MF_Singleton) mf;
				newFIS.println("\t \t " + mfType + " " + variableName + " = new " + mfType + "(\"" + singleton.getName() + "\", " + singleton.getValue() + ");");
				break;
			case "T1MF_Gaussian":
				T1MF_Gaussian gaussian = (T1MF_Gaussian) mf;
				newFIS.println("\t \t " + mfType + " " + variableName + " = new " + mfType + "(\"" + gaussian.getName() + "\", " + gaussian.getMean() + ", " + gaussian.getSpread() + ");");
				break;
			case "T1MF_Gauangle":
				T1MF_Gauangle gauangle = (T1MF_Gauangle) mf;
				newFIS.println("\t \t " + mfType + " " + variableName + " = new " + mfType + "(\"" + gauangle.getName() + "\", " + gauangle.getStart() + ", " + gauangle.getMean() + ", " + gauangle.getEnd() + ");");
				break;
			case "T1MF_Triangular":
				T1MF_Triangular triangular = (T1MF_Triangular) mf;
				newFIS.println("\t \t " + mfType + " " + variableName + " = new " + mfType + "(\"" + triangular.getName() + "\", " + triangular.getStart() + ", " + triangular.getPeak() + ", " + triangular.getEnd() + ");");
				break;
			case "T1MF_Trapezoidal":
				T1MF_Trapezoidal trapezoidal = (T1MF_Trapezoidal) mf;
				newFIS.println("\t \t double[] parameters;");
				newFIS.println("\t \t parameters[0] = " + trapezoidal.getA());
				newFIS.println("\t \t parameters[1] = " + trapezoidal.getB());
				newFIS.println("\t \t parameters[2] = " + trapezoidal.getC());
				newFIS.println("\t \t parameters[3] = " + trapezoidal.getD());
				newFIS.println("\t \t parameters[0] = " + trapezoidal.getA());
				newFIS.println("\t \t " + mfType + " " + variableName + " = new " + mfType + "(\"" + trapezoidal.getName() + "\", " + "parameters);");
				break;
		}
	}
	
	public void writeAntecedent(String name, T1MF_Prototype mf, Input input) {
		String variableName = getVariableName(name);
		newFIS.println("\t \t T1_Antecedent " + variableName + " = new T1_Antecedent(\"" + name + "\", " + getVariableName(mf.getName()) + ", " + input.getName() + ");");
	}

	public void writeConsequent(String name, T1MF_Prototype mf, Output output) {
		String variableName = getVariableName(name);
		newFIS.println("\t \t T1_Consequent " + variableName + " = new T1_Consequent(\"" + name + "\", " + getVariableName(mf.getName()) + ", " + output.getName() + ");");
	}
	
	public void writeRuleBase(String name, int size) {
		newFIS.println("\t \t " + getVariableName(name) + " new T1_Rulebase(" + size +");");
		this.rulebaseName = getVariableName(name);
	}
	
	public void writeRule(T1_Antecedent[] antecedents, T1_Consequent consequent) {
		String antecedentNames = "";
		for (int i=0; i<antecedents.length; i++) {
			if (!(i==0)) {
				antecedentNames += getVariableName(antecedents[i].getName());
			} else {
				antecedentNames += ", " + getVariableName(antecedents[i].getName());
			}
		}
		newFIS.println("\t \t " + rulebaseName + ".addRule(new T1_Rule(new T1_Antecedent[]{" + antecedentNames + "}, " + getVariableName(consequent.getName()) + "));");
	}
	
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
		newFIS = new PrintWriter("system.txt", "UTF-8");
		newFIS.println("line 1");
		newFIS.println("line 2");
		newFIS.close();
	}
}

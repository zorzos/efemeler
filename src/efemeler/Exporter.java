package efemeler;

import generic.*;
import type1.sets.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Exporter {
	
	private static PrintWriter newFIS;
	private String rulebaseName;
	
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
		newFIS = new PrintWriter("system.txt", "UTF-8");
		newFIS.println("line 1");
		newFIS.println("line 2");
		newFIS.close();
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
		String name = mf.getName();
	}
	
	public void writeAntecedent(String name, T1MF_Prototype mf, Input var) {
	}

	public void writeConsequent(String name, T1MF_Prototype mf, Output var) {
	}
	
	public void writeRuleBase(int size) {
		newFIS.println("\t\t " + rulebaseName + " new T1_Rulebase(" + size +");");
	}
	
	public void writeRule() {
	}
}

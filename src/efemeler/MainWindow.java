package efemeler;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
import java.util.List;
import java.util.Map;

import javax.swing.JSeparator;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import generic.*;

import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import type1.sets.T1MF_Gauangle;
import type1.sets.T1MF_Gaussian;
import type1.sets.T1MF_Prototype;
import type1.sets.T1MF_Singleton;
import type1.sets.T1MF_Trapezoidal;
import type1.sets.T1MF_Triangular;
import type1.system.T1_Antecedent;
import type1.system.T1_Consequent;
import type1.system.T1_Rule;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow {

	public JFrame frmEfemeler;
	private int inputCount;
	private int outputCount;
	private int functionCount;
	private int ruleCount;
	private DefaultListModel inputListModel;
	private DefaultListModel outputListModel;
	private DefaultListModel functionListModel;
	private DefaultListModel rulebaseListModel;
	
	JList inputVarsList = new JList();
	JLabel lblInputVariables = new JLabel("Input Variables");
	JSeparator separator = new JSeparator();
	JLabel lblOutputVariables = new JLabel("Output Variables");
	JList outputVarsList = new JList();
	JButton addVarButton = new JButton("Add variable");
	JLabel lblMembershipFunctions = new JLabel("Membership Functions");
	JList mfList = new JList();
	JButton btnAddMf = new JButton("Add MF");
	JSeparator separator_3 = new JSeparator();
	JLabel lblRules = new JLabel("Rule Bases");
	JList ruleBaseList = new JList();
	JButton btnAddRule = new JButton("Add");
	JMenuBar menuBar = new JMenuBar();
	JMenu mnOptions = new JMenu("Options");
	JMenuItem mntmExportCode = new JMenuItem("Export code");
	JMenuItem mntmExportEclipseProject = new JMenuItem("Export Eclipse project");
	JSeparator separator_1 = new JSeparator();
	JMenuItem mntmSystemFromFml = new JMenuItem("System from FML");
	JSeparator separator_2 = new JSeparator();
	JMenuItem mntmClose = new JMenuItem("Close");
	
	private static ArrayList<Input> inputVars = new ArrayList<Input>();
	private static ArrayList<Output> outputVars = new ArrayList<Output>();
	private ArrayList<String> xpaths = new ArrayList<String>();
	private ArrayList<T1MF_Prototype> functions = new ArrayList<T1MF_Prototype>();
	private ArrayList<T1_Rule> rules = new ArrayList<T1_Rule>();
	
	private File mapping = new File("fmlMapping.xml");
	private static PrintWriter newFIS;
	
	private static Map<String, ArrayList<T1_Rule>> ruleMap = new HashMap<String, ArrayList<T1_Rule>>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmEfemeler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	
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
	
	public static void writeInputs(ArrayList<Input> vars) {
		for (int i=0; i<vars.size(); i++) {
			newFIS.println("\t\t" + vars.get(i).getName() + " = new Input(\""+vars.get(i).getName()+"\", new Tuple("+ vars.get(i).getDomain().getLeft() + ", " + vars.get(i).getDomain().getRight() + "));");
		}
		newFIS.println();
		inputVars = vars;
	}
	
	public static void writeOutputs(ArrayList<Output> vars) {
		for (int i=0; i<vars.size(); i++) {
			newFIS.println("\t\t" + vars.get(i).getName() + " = new Output(\""+vars.get(i).getName()+"\", new Tuple("+ vars.get(i).getDomain().getLeft() + ", " + vars.get(i).getDomain().getRight() + "));");
		}
		newFIS.println();
		outputVars = vars;
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

	public static void writeResult(String rulebaseName) {
		String inputString = "";
		String[] setInputs = new String[inputVars.size()];
		String[] systemOuts = new String[inputVars.size() + 2];
		for (int i=0; i<inputVars.size(); i++) {
			if (i==0) {
				inputString += "double " + getVariableName(inputVars.get(i).getName()) + "Input";
			} else {
				inputString += ", double " + getVariableName(inputVars.get(i).getName()) + "Input";
			}
			setInputs[i] = getVariableName("\t\t\t" + inputVars.get(i).getName()) + ".setInput(" + getVariableName(inputVars.get(i).getName()) + "Input);";
			systemOuts[i] = "\t\t\tSystem.out.println(\"The "+ inputVars.get(i).getName() +" was: \" + " + getVariableName(inputVars.get(i).getName()) + ".getInput());";
		}
		
		newFIS.println("\t\tpublic void getResult(" + inputString + ") {");
		for (int j=0; j<setInputs.length; j++) {
			newFIS.println(setInputs[j]);
		}
		
		newFIS.println("\t\t\tTreeMap<Output, Double> output;");
		newFIS.println("\t\t\toutput = " + rulebaseName + ".evaluate(0);");
		
		String defuzz = "";
		for (int l=0; l<outputVars.size(); l++) {
			if (l==0) {
				defuzz += outputVars.get(l).getName() + " of\" + output.get(" + getVariableName(outputVars.get(l).getName()) + "));";
			} else {
				defuzz += ", " + outputVars.get(l).getName() + " of\" + output.get(" + getVariableName(outputVars.get(l).getName()) + "));";
			}
		}
		
		systemOuts[inputVars.size()] = "\t\t\tSystem.out.println(\"Using height defuzzification, the FLS recommends a " + defuzz;
		
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEfemeler = new JFrame();
		frmEfemeler.setTitle("Efemeler");
		frmEfemeler.setBounds(100, 100, 600, 555);
		frmEfemeler.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmEfemeler.getContentPane().setLayout(null);
		inputVarsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					int index = inputVarsList.locationToIndex(arg0.getPoint());
					System.out.println(index);
				}
			}
		});
		
		inputVarsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		inputVarsList.setBounds(10, 36, 106, 125);
		frmEfemeler.getContentPane().add(inputVarsList);
		inputListModel = new DefaultListModel();
		
		lblInputVariables.setBounds(10, 11, 116, 14);
		frmEfemeler.getContentPane().add(lblInputVariables);
		
		separator.setBounds(10, 172, 116, 2);
		frmEfemeler.getContentPane().add(separator);
		
		lblOutputVariables.setBounds(10, 185, 116, 14);
		frmEfemeler.getContentPane().add(lblOutputVariables);
		
		outputVarsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		outputVarsList.setBounds(10, 210, 106, 125);
		frmEfemeler.getContentPane().add(outputVarsList);
		outputListModel = new DefaultListModel();
		
		addVarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddVariable insertDialog = new AddVariable();
				insertDialog.setVisible(true);
				
				int vt = insertDialog.getVariableType();
				if (vt == 1) {
					//System.out.println(insertDialog.getInputVariable().getClass().getSimpleName());
					inputVars.add(insertDialog.getInputVariable());
					inputListModel.addElement(insertDialog.getInputVariable().getName());
					inputVarsList.setModel(inputListModel);
				} else if (vt == 2) {
					//System.out.println(insertDialog.getOutputVariable().getClass().getSimpleName());
					outputVars.add(insertDialog.getOutputVariable());
					outputListModel.addElement(insertDialog.getOutputVariable().getName());
					outputVarsList.setModel(outputListModel);
				}
				update();
			}
		});
		addVarButton.setBounds(10, 346, 116, 23);
		frmEfemeler.getContentPane().add(addVarButton);
		
		lblMembershipFunctions.setBounds(458, 11, 116, 14);
		frmEfemeler.getContentPane().add(lblMembershipFunctions);
		
		mfList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mfList.setBounds(458, 36, 106, 130);
		frmEfemeler.getContentPane().add(mfList);
		
		functionListModel = new DefaultListModel();
		btnAddMf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddMembershipFunction mfDialog = new AddMembershipFunction(inputVars, outputVars);
				mfDialog.setVisible(true);
				
				T1MF_Prototype function = mfDialog.getFunction();
				functions.add(function);
				functionListModel.addElement(mfDialog.getFunction().getName());
				mfList.setModel(functionListModel);
				update();
			}
		});
		btnAddMf.setBounds(468, 172, 89, 23);
		frmEfemeler.getContentPane().add(btnAddMf);
		
		separator_3.setBounds(458, 210, 106, 2);
		frmEfemeler.getContentPane().add(separator_3);
		
		lblRules.setBounds(458, 221, 89, 14);
		frmEfemeler.getContentPane().add(lblRules);
		
		ruleBaseList.setBounds(458, 244, 106, 125);
		frmEfemeler.getContentPane().add(ruleBaseList);
		
		rulebaseListModel = new DefaultListModel();
		btnAddRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddRule ruleDialog = new AddRule(inputVars, outputVars);
				ruleDialog.setVisible(true);
				
				T1_Rule rule = ruleDialog.getRule();
				rules.add(rule);
			}
		});
		
		btnAddRule.setBounds(468, 380, 89, 23);
		frmEfemeler.getContentPane().add(btnAddRule);
		
		frmEfemeler.setJMenuBar(menuBar);
		
		menuBar.add(mnOptions);
		mntmExportCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ExportCode exportCodeDialog = new ExportCode();
				exportCodeDialog.setVisible(true);
				
				String systemName = exportCodeDialog.getSystemName();	
				try {
					newFIS = new PrintWriter(System.getProperty("user.dir") + File.separator + systemName + File.separator + systemName + ".java", "UTF-8");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		mnOptions.add(mntmExportCode);
		
		mnOptions.add(mntmExportEclipseProject);
		
		mnOptions.add(separator_1);
		
		mnOptions.add(mntmSystemFromFml);
		mntmSystemFromFml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					buildFromFML();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (XPathExpressionException e2) {
					e2.printStackTrace();
				} catch (ParserConfigurationException e3) {
					e3.printStackTrace();
				} catch (SAXException e4) {
					e4.printStackTrace();
				}
			}
		});
		
		mnOptions.add(separator_2);
		
		mnOptions.add(mntmClose);
		mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmEfemeler.dispose();
			}
		});
		
		update();
	}
	
	private void buildFromFML() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		JFileChooser browser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Fuzzy Markup Language file", "fml");
		browser.setFileFilter(filter);
		int returnValue = browser.showOpenDialog(frmEfemeler);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String filename = browser.getSelectedFile().getPath();
			int slashIndex = filename.lastIndexOf("\\");
			int dotIndex = filename.lastIndexOf(".");
			filename = filename.substring(slashIndex+1, dotIndex);
			FMLParser parser = new FMLParser(browser.getSelectedFile());
			// parse FML file and build system from there - code should be in FMLParser.java
			xpaths = parser.getExpressions(mapping);
			parser.parseFile(browser.getSelectedFile(), xpaths);
			parser.closeUp();
		}
	}
	
	private void update() {
		inputCount = inputListModel.getSize();
		outputCount = outputListModel.getSize();
		functionCount = functions.size();
//		System.out.println("inputCount: " + inputCount);
//		System.out.println("outputCount: " + outputCount);
		
		if (inputCount <  1 || outputCount < 1) {
			//mntmExportCode.setEnabled(false);
			mntmExportEclipseProject.setEnabled(false);
			btnAddMf.setEnabled(false);
			btnAddRule.setEnabled(false);
		} 
		
		if (inputCount >  0 || outputCount > 0) {
			btnAddMf.setEnabled(true);
		}
		
		if (inputCount >  0 && outputCount > 0) {
			btnAddRule.setEnabled(true);
		}
	}
}

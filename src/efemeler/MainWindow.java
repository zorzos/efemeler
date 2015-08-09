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
import javax.swing.JOptionPane;

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
import type1.sets.T1MF_Interface;
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
	private int inputCount, outputCount, inputMF, outputMF;
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
	JLabel lblMembershipFunctions = new JLabel("Membership");
	JList mfList = new JList();
	JButton btnAddMf = new JButton("Add MF");
	JSeparator separator_3 = new JSeparator();
	JLabel lblRules = new JLabel("Rule Bases");
	JList ruleBaseList = new JList();
	JButton btnAddRule = new JButton("Add");
	JMenuBar menuBar = new JMenuBar();
	JMenu mnOptions = new JMenu("Options");
	JMenuItem mntmExportCode = new JMenuItem("Export code");
	JSeparator separator_1 = new JSeparator();
	JMenuItem mntmSystemFromFml = new JMenuItem("Build system from FML file");
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
	private static Map<T1MF_Prototype, Object> functionMap = new HashMap<T1MF_Prototype, Object>();
	
	private ArrayList<String> varNames = new ArrayList<String>();
	private ArrayList<String> mfNames = new ArrayList<String>();

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
					
					Input selectedVar = null;
					for (int i=0; i<inputVars.size(); i++) {
						if (inputVars.get(i).getName().equals(inputVarsList.getSelectedValue().toString())) {
							selectedVar = inputVars.get(i);
						}
					}

					AddVariable insertDialog = new AddVariable(varNames);
					insertDialog.setInput(selectedVar);
					insertDialog.setVisible(true);
					//System.out.println(index);
					
					
					if (insertDialog.getButton().equals("OK")) {
						varNames.remove(selectedVar.getName());
						inputListModel.removeElement(selectedVar.getName());
						
						for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
							if (entry.getValue().getClass().getSimpleName().equals("Input")) {
								Input comboIn = (Input)entry.getValue();
								if (selectedVar.getName().equals(comboIn.getName())) {
									entry.setValue(insertDialog.getInputVariable());
								}
							}
						}
						
						for (int j=0; j<rules.size(); j++) {
							T1_Antecedent[] ants = rules.get(j).getAntecedents();
							
							for (int a=0; a<ants.length; a++) {
								Input varIn = ants[a].getInput();
								if (selectedVar.getName().equals(varIn.getName())) {
									ants[a] = new T1_Antecedent("some antecedent", ants[a].getMF(), insertDialog.getInputVariable());
								}
							}
						}
						
						inputVars.add(insertDialog.getInputVariable());
						inputListModel.addElement(insertDialog.getInputVariable().getName());
						varNames.add(insertDialog.getInputVariable().getName());
						inputVarsList.setModel(inputListModel);
						inputVars.remove(selectedVar);
						update();
					}
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
		outputVarsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					Output selectedVar = null;
					for (int i=0; i<outputVars.size(); i++) {
						if (outputVars.get(i).getName().equals(outputVarsList.getSelectedValue().toString())) {
							selectedVar = outputVars.get(i);
						}
					}

					AddVariable insertDialog = new AddVariable(varNames);
					insertDialog.setOutput(selectedVar);
					insertDialog.setVisible(true);
										
					
					if (insertDialog.getButton().equals("OK")) {
						varNames.remove(selectedVar.getName());
						outputListModel.removeElement(selectedVar.getName());
						
						for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
							if (entry.getValue().getClass().getSimpleName().equals("Output")) {
								Output comboOut = (Output)entry.getValue();
								if (selectedVar.getName().equals(comboOut.getName())) {
									entry.setValue(insertDialog.getOutputVariable());
								}
							}
						}
						
						for (int j=0; j<rules.size(); j++) {
							T1_Consequent[] cons = rules.get(j).getConsequents();
							
							for (int c=0; c<cons.length; c++) {
								Output varOut = cons[c].getOutput();
								if (selectedVar.getName().equals(varOut.getName())) {
									cons[c] = new T1_Consequent("some consequent", cons[c].getMF(), insertDialog.getOutputVariable());
								}
							}
						}
						
						outputVars.add(insertDialog.getOutputVariable());
						outputListModel.addElement(insertDialog.getInputVariable().getName());
						varNames.add(insertDialog.getOutputVariable().getName());
						outputVarsList.setModel(outputListModel);
						outputVars.remove(selectedVar);
						update();
					}
				}
			}
		});
		
		outputVarsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		outputVarsList.setBounds(10, 210, 106, 125);
		frmEfemeler.getContentPane().add(outputVarsList);
		outputListModel = new DefaultListModel();
		
		// Adds an action listener to the "Add Variable" button, so that it calls the appropriate
		// dialog for the user to fill the variable details. Depending on the variable type added,
		// the application adds it to the appropriate ArrayList and updates the list boxes in the main window.
		addVarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddVariable insertDialog = new AddVariable(varNames);
				insertDialog.setVisible(true);
				
				if (insertDialog.getButton().equals("OK")) {
					int vt = insertDialog.getVariableType();
					if (vt == 1) {
						//System.out.println(insertDialog.getInputVariable().getClass().getSimpleName());
						inputVars.add(insertDialog.getInputVariable());
						inputListModel.addElement(insertDialog.getInputVariable().getName());
						varNames.add(insertDialog.getInputVariable().getName());
						inputVarsList.setModel(inputListModel);
					} else if (vt == 2) {
						//System.out.println(insertDialog.getOutputVariable().getClass().getSimpleName());
						outputVars.add(insertDialog.getOutputVariable());
						outputListModel.addElement(insertDialog.getOutputVariable().getName());
						varNames.add(insertDialog.getOutputVariable().getName());
						outputVarsList.setModel(outputListModel);
					}
					update();
				}
			}
		});
		addVarButton.setBounds(10, 346, 116, 23);
		frmEfemeler.getContentPane().add(addVarButton);
		
		lblMembershipFunctions.setBounds(458, 7, 116, 23);
		frmEfemeler.getContentPane().add(lblMembershipFunctions);
		mfList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					Object var = null;
					T1MF_Prototype mf = null;
					
					for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
						if (mfList.getSelectedValue().toString().equals(entry.getKey().getName())) {
							mf = entry.getKey();
							if (entry.getValue().getClass().getSimpleName().equals("Input")) {
								var = (Input)entry.getValue();
							} else {
								var = (Output)entry.getValue();
							}
						}
					}
					AddMembershipFunction mfDialog = new AddMembershipFunction(inputVars, outputVars, mfNames);
					System.out.println(var);
					mfDialog.setData(var, mf);
					mfDialog.setVisible(true);
					
					if (mfDialog.getButton().equals("OK")) {
						mfNames.remove(mf.getName());
						functionListModel.removeElement(mf.getName());
						
						for (int j=0; j<rules.size(); j++) {
							T1_Antecedent[] ants = rules.get(j).getAntecedents();
							
							for (int a=0; a<ants.length; a++) {
								T1MF_Interface mfIn = ants[a].getMF();
								if (mf.getName().equals(mfIn.getName())) {
									ants[a] = new T1_Antecedent("some antecedent", mfDialog.getFunction(), ants[a].getInput());
								}
							}
							
							T1_Consequent[] cons = rules.get(j).getConsequents();
							
							for (int c=0; c<cons.length; c++) {
								T1MF_Interface mfOut = cons[c].getMF();
								if (mf.getName().equals(mfOut.getName())) {
									cons[c] = new T1_Consequent("some consequent", mfDialog.getFunction(), cons[c].getOutput());
								}
							}
						}
						
						functionMap.put(mfDialog.getFunction(), var);
						functionListModel.addElement(mfDialog.getFunction().getName());
						mfNames.add(mfDialog.getFunction().getName());
						mfList.setModel(functionListModel);
						functionMap.remove(mf);
						update();
					}
				}
			}
		});
		
		mfList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mfList.setBounds(458, 66, 106, 130);
		frmEfemeler.getContentPane().add(mfList);
		functionListModel = new DefaultListModel();
		
		// The following bit of code calls the dialog for adding a new membership function.
		// Afterwards the function is added to the ArrayList containing all the membership functions.
		// Lastly the list box in the main window is updated
		btnAddMf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddMembershipFunction mfDialog = new AddMembershipFunction(inputVars, outputVars, mfNames);
				mfDialog.setVisible(true);
				
				if (mfDialog.getButton().equals("OK")) {
					T1MF_Prototype function = mfDialog.getFunction();
					functions.add(function);
					functionMap.put(function, mfDialog.getVariable());
					functionListModel.addElement(mfDialog.getFunction().getName());
					mfList.setModel(functionListModel);
					mfNames.add(mfDialog.getFunction().getName());
					update();
				}
			}
		});
		btnAddMf.setBounds(468, 202, 89, 23);
		frmEfemeler.getContentPane().add(btnAddMf);
		
		separator_3.setBounds(458, 240, 106, 2);
		frmEfemeler.getContentPane().add(separator_3);
		
		lblRules.setBounds(458, 251, 89, 14);
		frmEfemeler.getContentPane().add(lblRules);
		
		ruleBaseList.setBounds(458, 274, 106, 125);
		frmEfemeler.getContentPane().add(ruleBaseList);
		rulebaseListModel = new DefaultListModel();
		
		// The next bit calls the dialog for adding a new rule for the user to interact with.
		// Then it adds it to the ArrayList containing all the rules
		btnAddRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddRule ruleDialog = new AddRule(inputVars, outputVars, functionMap);
				ruleDialog.setVisible(true);
				
				if (ruleDialog.getButton().equals("OK")) {
					T1_Rule rule = ruleDialog.getRule();
					rules.add(rule);
					rulebaseListModel.addElement(ruleDialog.getName());
					ruleBaseList.setModel(rulebaseListModel);
					update();
				}
			}
		});
		
		btnAddRule.setBounds(468, 410, 89, 23);
		frmEfemeler.getContentPane().add(btnAddRule);
		
		JLabel lblFunctions = new JLabel("Functions");
		lblFunctions.setBounds(458, 37, 89, 14);
		frmEfemeler.getContentPane().add(lblFunctions);
		
		frmEfemeler.setJMenuBar(menuBar);
		
		menuBar.add(mnOptions);
		mntmExportCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ExportCode exportCodeDialog = new ExportCode();
				exportCodeDialog.setVisible(true);
				
				String systemName = exportCodeDialog.getSystemName();
				String[] rbNames = new String[2];
				rbNames[0] = "rulebase1";
				rbNames[1] = "anotherRulebase";
				try {
					
					File directory = new File(systemName);
					if (!directory.exists()) {
						directory.mkdirs();
					}
					
					newFIS = new PrintWriter(System.getProperty("user.dir") + File.separator + systemName + File.separator + systemName + ".java", "UTF-8");
					
					prepare(systemName, inputVars, outputVars, rbNames);
					writeInputs(inputVars);
					writeOutputs(outputVars);
					for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
						if (entry.getValue().getClass().getSimpleName().toString().equals("Input")) {
							Input in = (Input)entry.getValue();
							writeMembershipFunction(in.getName(), entry.getKey());
						} else {
							Output out = (Output)entry.getValue();
							writeMembershipFunction(out.getName(), entry.getKey());
						}
					}
					
					writeRuleBase(rbNames[0], 5);
					
					for (int r=0; r<rules.size(); r++) {
						
						for (int a=0; a<rules.get(r).getAntecedents().length; a++) {
							writeAntecedent(rules.get(r).getAntecedents()[a]);
						}
						
						for (int c=0; c<rules.get(r).getConsequents().length; c++) {
							writeConsequent(rules.get(r).getConsequents()[c]);
						}
						
						writeRule(rbNames[0], rules.get(r));
						
					}
					
					writeMain(systemName);
					closeUp();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
		
		mnOptions.add(mntmExportCode);
		mnOptions.add(separator_1);
		
		// This bit calls the function that builds a Fuzzy inference system from a Fuzzy Markup Language file
		mnOptions.add(mntmSystemFromFml);
		mntmSystemFromFml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					buildFromFML();
				} catch (IOException e1) {
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
	
	/**
	 * Builds a Fuzzy Inference System from a Fuzzy Markup Language file provided by the user via
	 * a file selector dialog box using the code in FMLParser.java
	 * 
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private void buildFromFML() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		JFileChooser browser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Fuzzy Markup Language file", "fml");
		browser.setFileFilter(filter);
		int returnValue = browser.showOpenDialog(frmEfemeler);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			try {	
				String filename = browser.getSelectedFile().getPath();
				int slashIndex = filename.lastIndexOf("\\");
				int dotIndex = filename.lastIndexOf(".");
				filename = filename.substring(slashIndex+1, dotIndex);
				FMLParser parser = new FMLParser(browser.getSelectedFile());
				// parse FML file and build system from there - code should be in FMLParser.java
				xpaths = parser.getExpressions(mapping);
				parser.parseFile(browser.getSelectedFile(), xpaths);
				parser.closeUp();
				JOptionPane.showMessageDialog(frmEfemeler, "System built. Please check " + System.getProperty("user.dir") + File.separator + browser.getSelectedFile().getName());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frmEfemeler, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	// This method serves to update the state of the system.
	// It counts the number of input, output variables as well as the
	// number of rules. The logic behind this is that no membership functions can be added 
	// without any variables and that no rules can be added without at least one
	// input and one output membership function.
	private void update() {
		inputCount = inputVars.size();
		outputCount = outputVars.size();
		inputMF = 0; 
		outputMF = 0;

		// Count input and output variable membership functions 
		if (functionMap.size() > 0) {
			for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
				if (entry.getValue().getClass().getSimpleName().toString().equals("Input")) {
					inputMF++;
				} else {
					outputMF++;
				}
			}
		}
		
		// If no variables present, disable all options
		if (inputCount <  1 || outputCount < 1) {
			mntmExportCode.setEnabled(false);
			btnAddMf.setEnabled(false);
			btnAddRule.setEnabled(false);
		} 
		
		// If variables are present, enable adding membership functions
		if (inputCount >  0 || outputCount > 0) {
			btnAddMf.setEnabled(true);
		}
		
		// If at least one input and one output variable membership function present,
		// enable adding rules
		if (inputMF >  0 && outputMF > 0) {
			btnAddRule.setEnabled(true);
		}
		
		// If rules are present enable exporting options
		if (rules.size() > 0 ) {
			mntmExportCode.setEnabled(true);
		}
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
	 * Closes the file created in the constructor after all of the information has been stored.
	 */
	public static void closeUp() {
		newFIS.println("\t}");
		newFIS.println("}");
		newFIS.close();
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
		inputVars = vars;
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
		outputVars = vars;
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
		newFIS.println("\t\tT1_Antecedent " + getVariableName(antecedent.getName()) + getVariableName(antecedent.getInput().getName()) + " = new T1_Antecedent(\"" + antecedent.getName() + "\", " + getVariableName(antecedent.getMF().getName()) + antecedent.getInput().getName() + "MF, " + getVariableName(antecedent.getInput().getName()) + ");");
		newFIS.println();
	}

	/**
	 * This method writes consequent declarations to file.
	 * 
	 * @param consequent the consequent to be written
	 */
	public static void writeConsequent(T1_Consequent consequent) {
		newFIS.println("\t\tT1_Consequent " + getVariableName(consequent.getName()) + getVariableName(consequent.getOutput().getName()) + " = new T1_Consequent(\"" + consequent.getName() + "\", " + getVariableName(consequent.getMF().getName()) + consequent.getOutput().getName() + "MF, " + getVariableName(consequent.getOutput().getName()) + ");");
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
				antecedentNames += getVariableName(rule.getAntecedents()[i].getName()) + getVariableName(rule.getAntecedents()[i].getInput().getName());
			} else {
				antecedentNames += ", " + getVariableName(rule.getAntecedents()[i].getName()) + getVariableName(rule.getAntecedents()[i].getInput().getName());
			}
		}
		
		String consequentNames = "";
		for (int j=0; j<rule.getConsequents().length; j++) {
			if (j==0) {
				consequentNames += getVariableName(rule.getConsequents()[j].getName()) + getVariableName(rule.getConsequents()[j].getOutput().getName());
			} else {
				consequentNames += ", " + getVariableName(rule.getConsequents()[j].getName()) + getVariableName(rule.getConsequents()[j].getOutput().getName());
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
}

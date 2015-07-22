package efemeler;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class MainWindow {

	public JFrame frmEfemeler;
	private int inputCount;
	private int outputCount;
	private int ruleCount;
	private DefaultListModel inputListModel;
	private DefaultListModel outputListModel;
	
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
	
	private int singletonValue;
	
	private List<Object> variableList = new ArrayList<Object>();
	private ArrayList<String> xpaths = new ArrayList<String>();
	
	private File mapping = new File("fmlMapping.xml");

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
					variableList.add(insertDialog.getInputVariable());
					inputListModel.addElement(insertDialog.getInputVariable().getName());
					inputVarsList.setModel(inputListModel);
					variableList.add(insertDialog.getInputVariable());
				} else if (vt == 2) {
					//System.out.println(insertDialog.getOutputVariable().getClass().getSimpleName());
					variableList.add(insertDialog.getOutputVariable());
					outputListModel.addElement(insertDialog.getOutputVariable().getName());
					outputVarsList.setModel(outputListModel);
					variableList.add(insertDialog.getOutputVariable());
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
		
		btnAddMf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddMembershipFunction dialog = new AddMembershipFunction(variableList);
				dialog.setVisible(true);
				
				singletonValue = Integer.parseInt(dialog.getSingletonValue());
				System.out.println(singletonValue);
				
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
		btnAddRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddRule ruleDialog = new AddRule(variableList);
				ruleDialog.setVisible(true);
			}
		});
		
		btnAddRule.setBounds(468, 380, 89, 23);
		frmEfemeler.getContentPane().add(btnAddRule);
		
		frmEfemeler.setJMenuBar(menuBar);
		
		menuBar.add(mnOptions);
		
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
			FMLParser parser = new FMLParser(filename);
			// parse FML file and build system from there - code should be in FMLParser.java
			xpaths = parser.getExpressions(mapping);
			parser.parseFile(browser.getSelectedFile(), xpaths);
			parser.closeUp();
		}
	}
	
	private void update() {
		inputCount = inputListModel.getSize();
		outputCount = outputListModel.getSize();
//		System.out.println("inputCount: " + inputCount);
//		System.out.println("outputCount: " + outputCount);
		
		if (inputCount <  1 || outputCount < 1) {
			mntmExportCode.setEnabled(false);
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

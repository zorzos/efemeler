package efemeler;

import generic.Input;
import generic.Output;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import type1.sets.T1MF_Prototype;
import type1.system.T1_Rule;
import type1.system.T1_Antecedent;
import type1.system.T1_Consequent;

public class AddRule extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private int buttonClickCount = 0;
	private JTextField ruleNameText;
	private JTextField weightText;
	private JButton btnAddMoreVariables = new JButton("Add variables");
	private static ArrayList<Input> in;
	private static ArrayList<Output> out;
	private static Map<T1MF_Prototype, Object> functions;
	private T1_Rule rule;
	private int varNewPosX = 0, varNewPosY = 0, mfNewPosX = 0, mfNewPosY = 0, lblNewPosX = 0, lblNewPosY = 0;
	private JComboBox inputComboBox = new JComboBox();
	private final JComboBox inputMFBox = new JComboBox();
	private final JComboBox outputComboBox = new JComboBox();
	private final JComboBox outputMFBox = new JComboBox();
	private ArrayList<JComboBox> vars = new ArrayList<JComboBox>();
	private ArrayList<JComboBox> values = new ArrayList<JComboBox>();
	private ArrayList<String> varNames = new ArrayList<String>();
	JLabel lblIs = new JLabel("is");
	
	private int currentActiveInputs = 1;
	private int maximumAvailableInputs;
	private String button;
	private boolean valid;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddRule dialog = new AddRule(in, out, functions);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public T1_Rule getRule() {
		return rule;
	}
	
	public String getName() {
		return ruleNameText.getText();
	}
	
	public String getButton() {
		return button;
	}
 
	/**
	 * Create the dialog.
	 */
	public AddRule(final ArrayList<Input> inputs, final ArrayList<Output> outputs, final Map<T1MF_Prototype, Object> functionMap) {
		maximumAvailableInputs = inputs.size();
		functions = functionMap;
		setTitle("Add Rule");
		setModal(true);
		setBounds(100, 100, 601, 304);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblIf = new JLabel("IF");
			lblIf.setFont(new Font("Tahoma", Font.BOLD, 12));
			lblIf.setBounds(10, 85, 99, 14);
			contentPanel.add(lblIf);
		}
		
		inputComboBox.setBounds(10, 110, 99, 20);
		contentPanel.add(inputComboBox);
		
		lblIs.setBounds(125, 113, 44, 14);
		contentPanel.add(lblIs);
		
		inputMFBox.setBounds(145, 110, 99, 20);
		contentPanel.add(inputMFBox);
		
		btnAddMoreVariables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getContentPane().add(addVars(inputs));
				update();
			}
		});
		btnAddMoreVariables.setBounds(119, 82, 125, 23);
		contentPanel.add(btnAddMoreVariables);
		
		JLabel lblThen = new JLabel("THEN");
		lblThen.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblThen.setBounds(341, 85, 99, 14);
		contentPanel.add(lblThen);
		
		outputComboBox.setBounds(341, 106, 99, 20);
		contentPanel.add(outputComboBox);
		
		int inputCount = inputs.size();
		for (int i=0; i<inputs.size(); i++) {
			inputComboBox.addItem(inputs.get(i).getName() + " (Input)");
		}
		
		for (int j=0; j<outputs.size(); j++) {
			outputComboBox.addItem(outputs.get(j).getName() + " (Output)");;
		}
		
		JLabel lblIs_1 = new JLabel("is");
		lblIs_1.setBounds(456, 109, 44, 14);
		contentPanel.add(lblIs_1);
		
		outputMFBox.setBounds(476, 106, 99, 20);
		contentPanel.add(outputMFBox);
		
		inputComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				inputMFBox.removeAllItems();
				JComboBox inVar = (JComboBox)arg0.getSource();
				String varName = (String)inVar.getSelectedItem();
				int spaceIndex = varName.indexOf(" ");
				for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
					if (entry.getValue().getClass().getSimpleName().equals("Input")) {
						Input ruleIn = (Input)entry.getValue();
						if (varName.substring(0, spaceIndex).equals(ruleIn.getName().toString())) {
							inputMFBox.addItem(entry.getKey().getName());
							varNames.add(ruleIn.getName());
						}
					}
				}
			}
		});
		
		varNewPosX = inputComboBox.getX();
		varNewPosY = inputComboBox.getY() + 40;
		mfNewPosX = inputMFBox.getX();
		mfNewPosY = inputMFBox.getY() + 40;
		lblNewPosX = lblIs.getX();
		lblNewPosY = lblIs.getY() + 40;
		
		outputComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputMFBox.removeAllItems();
				JComboBox outVar = (JComboBox)arg0.getSource();
				String varName = (String)outVar.getSelectedItem();
				int spaceIndex = varName.indexOf(" ");
				for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
					if (entry.getValue().getClass().getSimpleName().equals("Output")) {
						Output ruleOut = (Output)entry.getValue();
						if (varName.substring(0, spaceIndex).equals(ruleOut.getName().toString())) {
							outputMFBox.addItem(entry.getKey().getName());
						}
					}
				}
			}
		});
		
		vars.add(inputComboBox);
		values.add(inputMFBox);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(10, 14, 46, 14);
		contentPanel.add(lblName);
		
		ruleNameText = new JTextField();
		ruleNameText.setBounds(62, 11, 99, 20);
		contentPanel.add(ruleNameText);
		ruleNameText.setColumns(10);
		
		JLabel lblWeight = new JLabel("Weight");
		lblWeight.setBounds(10, 47, 46, 14);
		contentPanel.add(lblWeight);
		
		weightText = new JTextField();
		weightText.setBounds(62, 42, 99, 20);
		contentPanel.add(weightText);
		weightText.setColumns(10);
		
		JLabel lblConnectorOperator = new JLabel("Connector / Operator");
		lblConnectorOperator.setBounds(240, 14, 168, 14);
		contentPanel.add(lblConnectorOperator);
		
		JComboBox ConnOpComboBox = new JComboBox();
		ConnOpComboBox.setBounds(240, 39, 138, 20);
		ConnOpComboBox.addItem("AND / MIN");
		ConnOpComboBox.addItem("OR / MAX");
		contentPanel.add(ConnOpComboBox);
		if (inputCount == 1) {
			btnAddMoreVariables.setEnabled(false);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Add");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (validation()) {	
							button = "OK";
							// build antecedent
							T1_Antecedent[] ants = new T1_Antecedent[inputs.size()];
							Input var = null;
							T1MF_Prototype mf = null;
							Output outVar = null;
							T1MF_Prototype outMF = null;
							int spaceIndex = 0;
							for (int k=0; k<vars.size(); k++) {
								for (int u=0; u<inputs.size(); u++) {
									spaceIndex = vars.get(k).getSelectedItem().toString().indexOf(" ");
									if (vars.get(k).getSelectedItem().toString().substring(0, spaceIndex).equals(inputs.get(u).getName())) {
										var = inputs.get(u);
									}
								}
								
								for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
									if (values.get(k).getSelectedItem().toString().equals(entry.getKey().getName())) {
										mf = entry.getKey();
									}
								}
								ants[k] = new T1_Antecedent("antecedent", mf, var);
							}						
							
							// build consequent
							for (int p=0; p<outputs.size(); p++) {
								spaceIndex = outputComboBox.getSelectedItem().toString().indexOf(" ");
								if (outputComboBox.getSelectedItem().toString().substring(0, spaceIndex).equals(outputs.get(p).getName())) {
									outVar = outputs.get(p);
								}
								
								for (Map.Entry<T1MF_Prototype, Object> entry : functionMap.entrySet()) {
									if (outputMFBox.getSelectedItem().toString().equals(entry.getKey().getName())) {
										outMF = entry.getKey();
									}
								}
							}
							T1_Consequent con = new T1_Consequent("consequent", outMF, outVar);
							rule = new T1_Rule(ants, con);
							dispose();
						} else {
							JOptionPane.showMessageDialog(contentPanel.getParent(), "All fields are required", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						button = "cancel";
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private boolean isNumeric(String s) {  
	    return s.matches("[-+]?\\d*\\.?\\d+");  
	} 
	
	private boolean validation() {
		if (ruleNameText.getText().length() == 0 || weightText.getText().length() == 0 || !isNumeric(weightText.getText())) {
			valid = false;
		} else {
			valid = true;
		}
		
		for (int i=0; i<vars.size(); i++) {
			if (vars.get(i).getSelectedIndex() == -1) {
				valid = false;
			} else {
				valid = true;
			}
		}
		
		for (int j=0; j<values.size(); j++) {
			if (values.get(j).getSelectedIndex() == -1) {
				valid = false;
			} else {
				valid = true;
			}
		}
		
		if (outputComboBox.getSelectedIndex() == -1) {
			valid = false;
		}
		
		if (outputMFBox.getSelectedIndex() == -1) {
			valid = false;
		}
		
		return valid;
	}
	
	private void update() {
		if (currentActiveInputs < maximumAvailableInputs) {
			btnAddMoreVariables.setEnabled(true);
		} else {
			btnAddMoreVariables.setEnabled(false);
		}
	}
	
	private JPanel addVars(final ArrayList<Input> inputs) {
		currentActiveInputs++;
		JComboBox newVar = new JComboBox();
		final JComboBox newValue = new JComboBox();
		JLabel newLbl = new JLabel("is");
		
		newVar.setBounds(varNewPosX, varNewPosY, inputComboBox.getWidth(), inputComboBox.getHeight());
		contentPanel.add(newVar);
		
		newLbl.setBounds(lblNewPosX, lblNewPosY, lblIs.getWidth(), lblIs.getHeight());
		contentPanel.add(newLbl);
		
		newValue.setBounds(mfNewPosX, mfNewPosY, inputMFBox.getWidth(), inputMFBox.getHeight());
		contentPanel.add(newValue);
		
		for (int i=0; i<inputs.size(); i++) {
			newVar.addItem(inputs.get(i).getName() + " (Input)");
		}
		
		newVar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newValue.removeAllItems();
				JComboBox inVar = (JComboBox)arg0.getSource();
				String varName = (String)inVar.getSelectedItem();
				int spaceIndex = varName.indexOf(" ");
				for (Map.Entry<T1MF_Prototype, Object> entry : functions.entrySet()) {
					if (entry.getValue().getClass().getSimpleName().equals("Input")) {
						Input ruleIn = (Input)entry.getValue();
						if (varName.substring(0, spaceIndex).equals(ruleIn.getName().toString()) && inputs.contains(ruleIn.getName())) {
							newValue.addItem(entry.getKey().getName());
						}
						varNames.add(ruleIn.getName());
					}
				}
			}
		});
		
		JButton deleteVariable = new JButton("Delete");
		deleteVariable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		vars.add(newVar);
		values.add(newValue);
		
		varNewPosX = varNewPosX + newVar.getX();
		varNewPosY = varNewPosY + newVar.getY() + 40;
		lblNewPosX = lblNewPosX + newLbl.getX();
		lblNewPosY = lblNewPosY + newLbl.getY() + 40;
		mfNewPosX = mfNewPosX + newValue.getX();
		mfNewPosY = mfNewPosY + newValue.getY() + 40;
		return contentPanel;
	}
}

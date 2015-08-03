package efemeler;

import generic.Input;
import generic.Output;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
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

public class AddRule extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private static List<Object> variableList;
	private int buttonClickCount = 0;
	private JTextField ruleNameText;
	private JTextField textField;
	private static ArrayList<Input> in;
	private static ArrayList<Output> out;
	private static Map<T1MF_Prototype, Object> functions;
	private T1_Rule rule;

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
 
	/**
	 * Create the dialog.
	 */
	public AddRule(ArrayList<Input> inputs, ArrayList<Output> outputs, final Map<T1MF_Prototype, Object> functionMap) {
		System.out.println("functionMap with size: " + functionMap.size());
		System.out.println(functionMap.keySet());
		System.out.println(functionMap.values());
		setTitle("Add Rule");
		setModal(true);
		setBounds(100, 100, 450, 304);
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
		
		JComboBox inputComboBox = new JComboBox();
		inputComboBox.setBounds(10, 103, 99, 20);
		contentPanel.add(inputComboBox);
		
		JLabel lblIs = new JLabel("is");
		lblIs.setBounds(125, 106, 44, 14);
		contentPanel.add(lblIs);
		
		final JComboBox inputMFBox = new JComboBox();
		inputMFBox.setBounds(145, 103, 99, 20);
		contentPanel.add(inputMFBox);
		
		JButton btnAddMoreVariables = new JButton("Add more variables");
		btnAddMoreVariables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addMoreVars();
				pack();
			}
		});
		btnAddMoreVariables.setBounds(286, 102, 138, 23);
		contentPanel.add(btnAddMoreVariables);
		
		JLabel lblThen = new JLabel("THEN");
		lblThen.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblThen.setBounds(10, 160, 99, 14);
		contentPanel.add(lblThen);
		
		JComboBox outputComboBox = new JComboBox();
		outputComboBox.setBounds(10, 181, 99, 20);
		contentPanel.add(outputComboBox);
		
		int inputCount = inputs.size();
		for (int i=0; i<inputs.size(); i++) {
			inputComboBox.addItem(inputs.get(i).getName() + " (Input)");
		}
		
		for (int j=0; j<outputs.size(); j++) {
			outputComboBox.addItem(outputs.get(j).getName() + " (Output)");;
		}
		
		JLabel lblIs_1 = new JLabel("is");
		lblIs_1.setBounds(125, 184, 44, 14);
		contentPanel.add(lblIs_1);
		
		final JComboBox outputMFBox = new JComboBox();
		outputMFBox.setBounds(145, 181, 99, 20);
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
						}
					}
				}
			}
		});
		
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
		
		textField = new JTextField();
		textField.setBounds(62, 42, 99, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		
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
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private void addMoreVars() {
		
	}
}

package efemeler;

import generic.Input;
import generic.Output;
import type1.sets.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

public class AddMembershipFunction extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtName;
	private JComboBox mfTypeBox;
	private Object connectedVariable;
	private String button;
	private final JComboBox variableComboBox = new JComboBox();
	
	
	// Singleton membership value
	private JLabel singletonValueLbl;
	private JTextField singletonValueText;
	
	// Gaussian membership values
	private JLabel gaussianMeanLbl;
	private JTextField gaussianMeanText;
	private JLabel gaussianSpreadLbl;
	private JTextField gaussianSpreadText;
	
	// Discretized
	private JLabel discretizedPeakLbl;
	private JTextField discretizedPeakText;
	private JLabel discretizedLevelLbl;
	private JTextField discretizedLevelText;
	private JCheckBox discretizedLeftShoulder;
	private JCheckBox discretizedRightShoulder;
	private JLabel discretizedLeftShoulderLbl;
	private JTextField discretizedLeftShoulderText;
	private JLabel discretizedRightShoulderLbl;
	private JTextField discretizedRightShoulderText;
	
	// Triangular
	private JLabel triangularStartLbl;
	private JTextField triangularStartText;
	private JLabel triangularPeakLbl;
	private JTextField triangularPeakText;
	private JLabel triangularEndLbl;
	private JTextField triangularEndText;
	private JLabel gauangleStartLbl;
	private JTextField gauangleStartText;
	private JLabel gauangleCentreLbl;
	private JTextField gauangleCentreText;
	private JLabel gauangleEndLbl;
	private JTextField gauangleEndText;
	
	// Trapezoidal
	private JLabel trapezoidalPointALbl;
	private JTextField trapezoidalPointAText;
	private JLabel trapezoidalPointBLbl;
	private JTextField trapezoidalPointBText;
	private JLabel trapezoidalPointCLbl;
	private JTextField trapezoidalPointCText;
	private JLabel trapezoidalPointDLbl;
	private JTextField trapezoidalPointDText;
	
	private static ArrayList<Input> in;
	private static ArrayList<Output> out;
	private static T1MF_Prototype function;
	private boolean valid = false;
	private static ArrayList<String> names = new ArrayList<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddMembershipFunction dialog = new AddMembershipFunction(in, out, names);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddMembershipFunction(final ArrayList<Input> inputs, final ArrayList<Output> outputs, final ArrayList<String> functionNames) {
		names = functionNames;
		setModal(true);
		setTitle("Add Membership Function");
		setBounds(100, 100, 452, 344);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblName = new JLabel("Name");
			lblName.setBounds(10, 11, 46, 14);
			contentPanel.add(lblName);
		}
		{
			txtName = new JTextField();
			txtName.setBounds(75, 8, 166, 20);
			contentPanel.add(txtName);
			txtName.setColumns(10);
		}
		{
			JLabel lblType = new JLabel("Type");
			lblType.setBounds(10, 66, 46, 14);
			contentPanel.add(lblType);
		}
		
		// Singleton
		singletonValueLbl = new JLabel("Value");
		singletonValueLbl.setBounds(10, 97, 46, 14);
		contentPanel.add(singletonValueLbl);
		
		singletonValueText = new JTextField();
		singletonValueText.setBounds(148, 94, 86, 20);
		contentPanel.add(singletonValueText);
		singletonValueText.setColumns(10);
		
		// Gaussian
		gaussianMeanLbl = new JLabel("Mean");
		gaussianMeanLbl.setBounds(10, 97, 46, 14);
		contentPanel.add(gaussianMeanLbl);
		
		gaussianMeanText = new JTextField();
		gaussianMeanText.setBounds(148, 94, 86, 20);
		contentPanel.add(gaussianMeanText);
		gaussianMeanText.setColumns(10);
		
		gaussianSpreadLbl = new JLabel("Spread");
		gaussianSpreadLbl.setBounds(10, 122, 46, 14);
		contentPanel.add(gaussianSpreadLbl);
		
		gaussianSpreadText = new JTextField();
		gaussianSpreadText.setBounds(148, 119, 86, 20);
		contentPanel.add(gaussianSpreadText);
		gaussianSpreadText.setColumns(10);
		
		// Discretized
		discretizedPeakLbl = new JLabel("Peak");
		discretizedPeakLbl.setBounds(10, 97, 46, 14);
		contentPanel.add(discretizedPeakLbl);
		
		discretizedPeakText = new JTextField();
		discretizedPeakText.setBounds(148, 94, 86, 20);
		contentPanel.add(discretizedPeakText);
		discretizedPeakText.setColumns(10);
		
		discretizedLevelLbl = new JLabel("Level");
		discretizedLevelLbl.setBounds(10, 122, 46, 14);
		contentPanel.add(discretizedLevelLbl);
		
		discretizedLevelText = new JTextField();
		discretizedLevelText.setBounds(148, 119, 86, 20);
		contentPanel.add(discretizedLevelText);
		discretizedLevelText.setColumns(10);
		
		discretizedLeftShoulder = new JCheckBox("Left Shoulder");
		discretizedLeftShoulder.setBounds(10, 151, 123, 23);
		contentPanel.add(discretizedLeftShoulder);
		
		discretizedRightShoulder = new JCheckBox("Right Shoulder");
		discretizedRightShoulder.setBounds(10, 185, 123, 23);
		contentPanel.add(discretizedRightShoulder);
		
		discretizedLeftShoulderLbl = new JLabel("Start");
		discretizedLeftShoulderLbl.setBounds(158, 150, 33, 23);
		contentPanel.add(discretizedLeftShoulderLbl);
		
		discretizedLeftShoulderText = new JTextField();
		discretizedLeftShoulderText.setBounds(193, 150, 86, 23);
		contentPanel.add(discretizedLeftShoulderText);
		discretizedLeftShoulderText.setColumns(10);
		discretizedLeftShoulder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AbstractButton abstractButton = (AbstractButton)arg0.getSource();
				boolean checked = abstractButton.getModel().isSelected();
				if (checked) {
					discretizedLeftShoulderLbl.setVisible(true);
					discretizedLeftShoulderText.setVisible(true);
				} else {
					discretizedLeftShoulderLbl.setVisible(false);
					discretizedLeftShoulderText.setVisible(false);
				}
			}
		});
		
		discretizedRightShoulderLbl = new JLabel("Start");
		discretizedRightShoulderLbl.setBounds(158, 184, 33, 23);
		contentPanel.add(discretizedRightShoulderLbl);
		
		discretizedRightShoulderText = new JTextField();
		discretizedRightShoulderText.setBounds(193, 184, 86, 23);
		contentPanel.add(discretizedRightShoulderText);
		discretizedRightShoulderText.setColumns(10);
		discretizedRightShoulder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AbstractButton abstractButton = (AbstractButton)arg0.getSource();
				boolean checked = abstractButton.getModel().isSelected();
				if (checked) {
					discretizedRightShoulderLbl.setVisible(true);
					discretizedRightShoulderText.setVisible(true);
				} else {
					discretizedRightShoulderLbl.setVisible(false);
					discretizedRightShoulderText.setVisible(false);
				}
			}
		});
		
		// Triangular
		
		triangularStartLbl = new JLabel("Start");
		triangularStartLbl.setBounds(10, 97, 46, 14);
		contentPanel.add(triangularStartLbl);
		
		triangularStartText = new JTextField();
		triangularStartText.setBounds(148, 94, 86, 20);
		contentPanel.add(triangularStartText);
		triangularStartText.setColumns(10);
		
		triangularPeakLbl = new JLabel("Peak");
		triangularPeakLbl.setBounds(10, 122, 46, 14);
		contentPanel.add(triangularPeakLbl);
		
		triangularPeakText = new JTextField();
		triangularPeakText.setBounds(148, 119, 86, 20);
		contentPanel.add(triangularPeakText);
		triangularPeakText.setColumns(10);
		
		triangularEndLbl = new JLabel("End");
		triangularEndLbl.setBounds(10, 150, 123, 23);
		contentPanel.add(triangularEndLbl);
		
		triangularEndText = new JTextField();
		triangularEndText.setBounds(148, 150, 86, 23);
		contentPanel.add(triangularEndText);
		triangularEndText.setColumns(10);
		
		gauangleStartLbl = new JLabel("Start");
		gauangleStartLbl.setBounds(10, 91, 123, 23);
		contentPanel.add(gauangleStartLbl);
		
		gauangleStartText = new JTextField();
		gauangleStartText.setBounds(148, 91, 86, 23);
		contentPanel.add(gauangleStartText);
		gauangleStartText.setColumns(10);
		
		gauangleCentreLbl = new JLabel("Centre");
		gauangleCentreLbl.setBounds(10, 125, 123, 23);
		contentPanel.add(gauangleCentreLbl);
		
		gauangleCentreText = new JTextField();
		gauangleCentreText.setBounds(148, 125, 86, 23);
		contentPanel.add(gauangleCentreText);
		gauangleCentreText.setColumns(10);
		
		gauangleEndLbl = new JLabel("End");
		gauangleEndLbl.setBounds(10, 159, 86, 23);
		contentPanel.add(gauangleEndLbl);
		
		gauangleEndText = new JTextField();
		gauangleEndText.setBounds(148, 159, 86, 23);
		contentPanel.add(gauangleEndText);
		gauangleEndText.setColumns(10);
		
		// Trapezoidal
		trapezoidalPointALbl = new JLabel("Point A");
		trapezoidalPointALbl.setBounds(10, 97, 46, 14);
		contentPanel.add(trapezoidalPointALbl);
		
		trapezoidalPointAText = new JTextField();
		trapezoidalPointAText.setBounds(148, 94, 86, 20);
		contentPanel.add(trapezoidalPointAText);
		trapezoidalPointAText.setColumns(10);
		
		trapezoidalPointBLbl = new JLabel("Point B");
		trapezoidalPointBLbl.setBounds(10, 122, 46, 14);
		contentPanel.add(trapezoidalPointBLbl);
		
		trapezoidalPointBText = new JTextField();
		trapezoidalPointBText.setBounds(148, 119, 86, 20);
		contentPanel.add(trapezoidalPointBText);
		
		trapezoidalPointCLbl = new JLabel("Point C");
		trapezoidalPointCLbl.setBounds(10, 150, 123, 23);
		contentPanel.add(trapezoidalPointCLbl);
		
		trapezoidalPointCText = new JTextField();
		trapezoidalPointCText.setBounds(148, 150, 86, 23);
		contentPanel.add(trapezoidalPointCText);
		trapezoidalPointCText.setColumns(10);
		
		trapezoidalPointDLbl = new JLabel("Point D");
		trapezoidalPointDLbl.setBounds(10, 184, 123, 23);
		contentPanel.add(trapezoidalPointDLbl);
		
		trapezoidalPointDText = new JTextField();
		trapezoidalPointDText.setBounds(148, 184, 86, 23);
		contentPanel.add(trapezoidalPointDText);
		trapezoidalPointDText.setColumns(10);
		
		hideAll();
		
		{
			mfTypeBox = new JComboBox();
			mfTypeBox.setBounds(75, 63, 166, 20);
			mfTypeBox.addItem("Discretized");
			mfTypeBox.addItem("Gauangle");
			mfTypeBox.addItem("Gaussian");
			mfTypeBox.addItem("Singleton");
			mfTypeBox.addItem("Trapezoidal");
			mfTypeBox.addItem("Triangular");
			mfTypeBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					hideAll();
					JComboBox mf = (JComboBox)arg0.getSource();
			        String mfName = (String)mf.getSelectedItem();
			        if (mfName.equals("Singleton")) {
			        	singletonValueLbl.setVisible(true);
			        	singletonValueText.setVisible(true);
			        } else if (mfName.equals("Gaussian")) {
			        	gaussianMeanLbl.setVisible(true);
			    		gaussianMeanText.setVisible(true);
			    		gaussianSpreadLbl.setVisible(true);
			    		gaussianSpreadText.setVisible(true);
			        } else if (mfName.equals("Discretized")) {
			        	discretizedPeakLbl.setVisible(true);
			        	discretizedPeakText.setVisible(true);
			        	discretizedLevelLbl.setVisible(true);
			        	discretizedLevelText.setVisible(true);
			        	discretizedLeftShoulder.setVisible(true);
			        	discretizedRightShoulder.setVisible(true);
			        } else if (mfName.equals("Triangular")) {
			        	triangularStartLbl.setVisible(true);
			    		triangularStartText.setVisible(true);
			    		triangularPeakLbl.setVisible(true);
			    		triangularPeakText.setVisible(true);
			    		triangularEndLbl.setVisible(true);
			    		triangularEndText.setVisible(true);
			        } else if (mfName.equals("Gauangle")) {
			        	gauangleStartLbl.setVisible(true);
			    		gauangleStartText.setVisible(true);
			    		gauangleCentreLbl.setVisible(true);
			    		gauangleCentreText.setVisible(true);
			    		gauangleEndLbl.setVisible(true);
			    		gauangleEndText.setVisible(true);
			        } else if (mfName.equals("Trapezoidal")) {
			        	trapezoidalPointALbl.setVisible(true);
			    		trapezoidalPointAText.setVisible(true);
			    		trapezoidalPointBLbl.setVisible(true);
			    		trapezoidalPointBText.setVisible(true);
			    		trapezoidalPointCLbl.setVisible(true);
			    		trapezoidalPointCText.setVisible(true);
			    		trapezoidalPointDLbl.setVisible(true);
			    		trapezoidalPointDText.setVisible(true);
			        }
				}
			});
			contentPanel.add(mfTypeBox);
		}
		
		JLabel lblVariable = new JLabel("Variable");
		lblVariable.setBounds(10, 41, 65, 14);
		contentPanel.add(lblVariable);
		
		variableComboBox.setBounds(85, 39, 156, 20);
		contentPanel.add(variableComboBox);		
		for (int i=0; i<inputs.size(); i++) {
			variableComboBox.addItem(inputs.get(i).getName());
		}
		
		for (int j=0; j<outputs.size(); j++) {
			variableComboBox.addItem(outputs.get(j).getName());;
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Add");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						button = "OK";
						String currentSelection = (String)mfTypeBox.getSelectedItem();
						//System.out.println(currentSelection);
						String variableSelection = (String)variableComboBox.getSelectedItem();
						
						if (validation(currentSelection)) {
							for (int i=0; i<inputs.size(); i++) {
								if (variableSelection.equals(inputs.get(i).getName())) {
									connectedVariable = inputs.get(i);
								}
							}
							
							for (int j=0; j<outputs.size(); j++) {
								if (variableSelection.equals(outputs.get(j).getName())) {
									connectedVariable = outputs.get(j);
								}
							}
							
							String mfType = (String)mfTypeBox.getSelectedItem();
							String name = txtName.getText();
							switch (mfType) {
								case "Singleton":
									double value = Double.parseDouble(singletonValueText.getText());
									function = new T1MF_Singleton(name, value);
									break;
								case "Triangular":
									double triStart = Double.parseDouble(triangularStartText.getText());
									double peak = Double.parseDouble(triangularPeakText.getText());
									double triEnd = Double.parseDouble(triangularEndText.getText());
									function = new T1MF_Triangular(name, triStart, peak, triEnd);
									break;
								case "Gaussian":
									double mean = Double.parseDouble(gaussianMeanText.getText());
									double spread = Double.parseDouble(gaussianSpreadText.getText());
									function = new T1MF_Gaussian(name, mean, spread);
									break;
								case "Gauangle":
									double gauStart = Double.parseDouble(gauangleStartText.getText());
									double center = Double.parseDouble(gauangleCentreText.getText());
									double gauEnd = Double.parseDouble(gauangleEndText.getText());
									function = new T1MF_Gauangle(name, gauStart, center, gauEnd);
									break;
								case "Trapezoidal":
									double[] parameters = new double[4];
									parameters[0] = Double.parseDouble(trapezoidalPointAText.getText());
									parameters[1] = Double.parseDouble(trapezoidalPointBText.getText());
									parameters[2] = Double.parseDouble(trapezoidalPointCText.getText());
									parameters[3] = Double.parseDouble(trapezoidalPointDText.getText());
									function = new T1MF_Trapezoidal(name, parameters);
									break;	
							}
							dispose();
						} else {
							JOptionPane.showMessageDialog(contentPanel.getParent(), "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
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
					public void actionPerformed(ActionEvent arg0) {
						button = "cancel";
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public void setData(Object o, T1MF_Prototype mf) {
		hideAll();
		txtName.setText(mf.getName());
		for (int y=0; y<variableComboBox.getItemCount(); y++) {
			if (o.getClass().getSimpleName().toString().equals("Input")) {
				Input in = (Input)o;
				if (in.getName().equals(variableComboBox.getItemAt(y).toString())) {
					variableComboBox.setSelectedItem(variableComboBox.getItemAt(y).toString());
				}
			} else {
				Output out = (Output)o;
				if (out.getName().equals(variableComboBox.getItemAt(y).toString())) {
					variableComboBox.setSelectedItem(variableComboBox.getItemAt(y).toString());
				}
			}
		}
		
		int underscoreIndex = mf.getClass().getSimpleName().lastIndexOf("_") + 1;
		mfTypeBox.setSelectedItem(mf.getClass().getSimpleName().substring(underscoreIndex));
		if (mf.getClass().getSimpleName().equals("T1MF_Singleton")) {
			T1MF_Singleton singleton = (T1MF_Singleton) mf;
			singletonValueText.setText(Double.toString(singleton.getValue()));
						
        	singletonValueLbl.setVisible(true);
        	singletonValueText.setVisible(true);
        } else if (mf.getClass().getSimpleName().equals("T1MF_Gaussian")) {
        	T1MF_Gaussian gaussian = (T1MF_Gaussian)mf;
        	gaussianMeanText.setText(Double.toString(gaussian.getMean()));
        	gaussianSpreadText.setText(Double.toString(gaussian.getSpread()));
        	
        	gaussianMeanLbl.setVisible(true);
    		gaussianMeanText.setVisible(true);
    		gaussianSpreadLbl.setVisible(true);
    		gaussianSpreadText.setVisible(true);
        } else if (mf.getClass().getSimpleName().equals("T1MF_Discretized")) {
        	T1MF_Discretized discretized = (T1MF_Discretized)mf;
        	discretizedPeakText.setText(Double.toString(discretized.getPeak()));
        	
        	discretizedPeakLbl.setVisible(true);
        	discretizedPeakText.setVisible(true);
        	discretizedLevelLbl.setVisible(true);
        	discretizedLevelText.setVisible(true);
        	discretizedLeftShoulder.setVisible(true);
        	discretizedRightShoulder.setVisible(true);
        } else if (mf.getClass().getSimpleName().equals("T1MF_Triangular")) {
        	T1MF_Triangular triangular = (T1MF_Triangular)mf;
        	triangularStartText.setText(Double.toString(triangular.getStart()));
        	triangularPeakText.setText(Double.toString(triangular.getPeak()));
        	triangularEndText.setText(Double.toString(triangular.getEnd()));
        	
        	triangularStartLbl.setVisible(true);
    		triangularStartText.setVisible(true);
    		triangularPeakLbl.setVisible(true);
    		triangularPeakText.setVisible(true);
    		triangularEndLbl.setVisible(true);
    		triangularEndText.setVisible(true);
        } else if (mf.getClass().getSimpleName().equals("T1MF_Gauangle")) {
        	T1MF_Gauangle gauangle = (T1MF_Gauangle)mf;
        	gauangleStartText.setText(Double.toString(gauangle.getStart()));
        	gauangleCentreText.setText(Double.toString(gauangle.getPeak()));
        	gauangleEndText.setText(Double.toString(gauangle.getEnd()));
        	
        	gauangleStartLbl.setVisible(true);
    		gauangleStartText.setVisible(true);
    		gauangleCentreLbl.setVisible(true);
    		gauangleCentreText.setVisible(true);
    		gauangleEndLbl.setVisible(true);
    		gauangleEndText.setVisible(true);
        } else if (mf.getClass().getSimpleName().equals("T1MF_Trapezoidal")) {
        	T1MF_Trapezoidal trapezoidal = (T1MF_Trapezoidal)mf;
        	trapezoidalPointAText.setText(Double.toString(trapezoidal.getA()));
        	trapezoidalPointBText.setText(Double.toString(trapezoidal.getB()));
        	trapezoidalPointCText.setText(Double.toString(trapezoidal.getC()));
        	trapezoidalPointDText.setText(Double.toString(trapezoidal.getD()));
        	
        	trapezoidalPointALbl.setVisible(true);
    		trapezoidalPointAText.setVisible(true);
    		trapezoidalPointBLbl.setVisible(true);
    		trapezoidalPointBText.setVisible(true);
    		trapezoidalPointCLbl.setVisible(true);
    		trapezoidalPointCText.setVisible(true);
    		trapezoidalPointDLbl.setVisible(true);
    		trapezoidalPointDText.setVisible(true);
        }
	}
	
	private boolean availableName(String s) {
		for (int i=0; i<names.size(); i++) {
			if (names.get(i).equals(s)) {
				return false;
			}
		}
		return true;
	}
	
	public String getButton() {
		return button;
	}
	
	public T1MF_Prototype getFunction() {
		return function;
	}
	
	public Object getVariable() {
		return connectedVariable;
	}
	
	private void hideAll() {
		singletonValueLbl.setVisible(false);
		singletonValueText.setVisible(false);
		
		gaussianMeanLbl.setVisible(false);
		gaussianMeanText.setVisible(false);
		gaussianSpreadLbl.setVisible(false);
		gaussianSpreadText.setVisible(false);
		
		discretizedPeakLbl.setVisible(false);
		discretizedPeakText.setVisible(false);
		discretizedLevelLbl.setVisible(false);
		discretizedLevelText.setVisible(false);
		discretizedLeftShoulder.setVisible(false);
		discretizedLeftShoulderLbl.setVisible(false);
		discretizedLeftShoulderText.setVisible(false);
		discretizedRightShoulder.setVisible(false);
		discretizedRightShoulderLbl.setVisible(false);
		discretizedRightShoulderText.setVisible(false);
		
		triangularStartLbl.setVisible(false);
		triangularStartText.setVisible(false);
		triangularPeakLbl.setVisible(false);
		triangularPeakText.setVisible(false);
		triangularEndLbl.setVisible(false);
		triangularEndText.setVisible(false);
		gauangleStartLbl.setVisible(false);
		gauangleStartText.setVisible(false);
		gauangleCentreLbl.setVisible(false);
		gauangleCentreText.setVisible(false);
		gauangleEndLbl.setVisible(false);
		gauangleEndText.setVisible(false);

		trapezoidalPointALbl.setVisible(false);
		trapezoidalPointAText.setVisible(false);
		trapezoidalPointBLbl.setVisible(false);
		trapezoidalPointBText.setVisible(false);
		trapezoidalPointCLbl.setVisible(false);
		trapezoidalPointCText.setVisible(false);
		trapezoidalPointDLbl.setVisible(false);
		trapezoidalPointDText.setVisible(false);
	}
	
	private boolean isNumeric(String s) {  
	    return s.matches("[-+]?\\d*\\.?\\d+");  
	}
	
	private boolean validation(String type) {
		switch (type) {
			case "Triangular":
				if (!availableName(txtName.getText()) || !isNumeric(triangularStartText.getText()) || !isNumeric(triangularPeakText.getText()) || !isNumeric(triangularEndText.getText()) || txtName.getText().length() == 0 || triangularStartText.getText().length() == 0 || triangularPeakText.getText().length() == 0 || triangularEndText.getText().length() == 0) {
					valid = false;
				} else {
					valid = true;
				}
				break;
			case "Gaussian":
				if (!availableName(txtName.getText()) || !isNumeric(gaussianSpreadText.getText()) || !isNumeric(gaussianMeanText.getText()) || txtName.getText().length() == 0 || gaussianSpreadText.getText().length() == 0 || gaussianMeanText.getText().length() == 0) {
					valid = false;
				}
				break;
			case "Singleton":
				if (!availableName(txtName.getText()) || !isNumeric(singletonValueText.getText()) || txtName.getText().length() == 0 || singletonValueText.getText().length() == 0) {
					valid = false;
				} else {
					valid = true;
				}
				break;
			case "Gauangle":
				if (!availableName(txtName.getText()) || !isNumeric(gauangleStartText.getText()) || !isNumeric(gauangleCentreText.getText()) || !isNumeric(gauangleEndText.getText()) || txtName.getText().length() == 0 || gauangleStartText.getText().length() == 0 || gauangleCentreText.getText().length() == 0 || gauangleEndText.getText().length() == 0) {
					valid = false;
				} else {
					valid = true;
				}
				break;
			case "Trapezoidal":
				if (!availableName(txtName.getText()) || !isNumeric(trapezoidalPointAText.getText()) || !isNumeric(trapezoidalPointBText.getText()) || !isNumeric(trapezoidalPointCText.getText()) || !isNumeric(trapezoidalPointDText.getText()) || txtName.getText().length() == 0 || trapezoidalPointAText.getText().length() == 0 || trapezoidalPointBText.getText().length() == 0 || trapezoidalPointCText.getText().length() == 0 || trapezoidalPointDText.getText().length() == 0) {
					valid = false;
				} else {
					valid = true;
				}
				break;
		}
			
		return valid;
	}
}

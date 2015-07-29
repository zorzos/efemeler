package efemeler;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import generic.*;

public class AddVariable extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtName;
	private JTextField txtScale;
	private JComboBox typeBox;
	private JTextField domainLower;
	private JTextField domainUpper;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddVariable dialog = new AddVariable();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddVariable() {
		setModal(true);
		setTitle("Add Variable");
		setBounds(100, 100, 210, 211);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(10, 11, 46, 14);
		contentPanel.add(lblName);
		
		txtName = new JTextField();
		txtName.setBounds(84, 11, 86, 20);
		contentPanel.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblScale = new JLabel("Scale");
		lblScale.setBounds(10, 70, 46, 14);
		contentPanel.add(lblScale);
		
		txtScale = new JTextField();
		txtScale.setBounds(84, 70, 86, 20);
		contentPanel.add(txtScale);
		txtScale.setColumns(10);
		
		JLabel lblType = new JLabel("Type");
		lblType.setBounds(10, 101, 46, 14);
		contentPanel.add(lblType);
		
		typeBox = new JComboBox();
		typeBox.setBounds(84, 98, 86, 20);
		typeBox.addItem("Input");
		typeBox.addItem("Output");
		contentPanel.add(typeBox);
		
		JLabel lblDomain = new JLabel("Domain");
		lblDomain.setBounds(10, 36, 46, 14);
		contentPanel.add(lblDomain);
		
		domainLower = new JTextField();
		domainLower.setBounds(84, 39, 39, 20);
		contentPanel.add(domainLower);
		domainLower.setColumns(10);
		
		domainUpper = new JTextField();
		domainUpper.setColumns(10);
		domainUpper.setBounds(133, 39, 39, 20);
		contentPanel.add(domainUpper);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Add");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
				        dispose();
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
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public Input getInputVariable() {
		String name = txtName.getText();
		Tuple domain = new Tuple(Double.parseDouble(domainLower.getText()), Double.parseDouble(domainUpper.getText()));
		String scale = txtScale.getText();
		return new Input(name, domain, scale);
	}
	
	public Output getOutputVariable() {
		String name = txtName.getText();
		Tuple domain = new Tuple(Double.parseDouble(domainLower.getText()), Double.parseDouble(domainUpper.getText()));
		String scale = txtScale.getText();
		return new Output(name, domain, scale);		
	}
	
	public int getVariableType() {
		if (typeBox.getSelectedItem().toString().equals("Input")) {
			return 1;
		} else if (typeBox.getSelectedItem().toString().equals("Output")) {
			return 2;
		} else {
			return 0;
		}
	}
}

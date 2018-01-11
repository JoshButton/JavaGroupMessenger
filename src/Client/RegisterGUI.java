package Client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class RegisterGUI extends JFrame implements ActionListener {

    JPanel contentPane;
    private JButton btnRegister;
    private JTextField usernameField, passwordField, screenNameField, confirmPasswordField;
    private JLabel errorLabel;
    private LoginGUI loginGUI;
    private Communication c;

    public RegisterGUI(LoginGUI loginGUI, Communication c) {
        this.c = c;
        this.loginGUI = loginGUI;
        setBackground(Color.LIGHT_GRAY);
        setTitle("Register");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 703, 498);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(211, 211, 211));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBounds(132, 57, 428, 332);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblRegister = new JLabel("Register");
        lblRegister.setForeground(new Color(105, 105, 105));
        lblRegister.setBounds(165, 34, 73, 22);
        panel.add(lblRegister);
        lblRegister.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 17));
        lblRegister.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblScreenName = new JLabel("Screen Name:");
        lblScreenName.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblScreenName.setForeground(SystemColor.textInactiveText);
        lblScreenName.setBounds(91, 91, 93, 16);
        panel.add(lblScreenName);

        lblScreenName.setHorizontalAlignment(SwingConstants.RIGHT);
        screenNameField = new JTextField(15);
        screenNameField.setBounds(215, 90, 125, 20);
        panel.add(screenNameField);
        screenNameField.setHorizontalAlignment(SwingConstants.LEFT);
        screenNameField.setColumns(10);

        JLabel lblUsername = new JLabel("UserName:");
        lblUsername.setForeground(SystemColor.textInactiveText);
        lblUsername.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblUsername.setBounds(91, 123, 93, 14);
        panel.add(lblUsername);
        lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);

        usernameField = new JTextField(15);
        usernameField.setBounds(215, 121, 125, 20);
        panel.add(usernameField);
        usernameField.setHorizontalAlignment(SwingConstants.LEFT);
        usernameField.setColumns(10);

        JLabel lblNewLabel = new JLabel("Password:");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblNewLabel.setForeground(SystemColor.textInactiveText);
        lblNewLabel.setBounds(114, 154, 70, 14);
        panel.add(lblNewLabel);
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        passwordField = new JPasswordField(15);
        passwordField.setBounds(215, 152, 125, 20);
        panel.add(passwordField);
        passwordField.setHorizontalAlignment(SwingConstants.LEFT);
        passwordField.setColumns(10);

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblConfirmPassword.setForeground(SystemColor.textInactiveText);
        lblConfirmPassword.setBounds(59, 187, 125, 14);
        panel.add(lblConfirmPassword);
        lblConfirmPassword.setHorizontalAlignment(SwingConstants.RIGHT);

        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setBounds(215, 185, 125, 20);
        panel.add(confirmPasswordField);
        confirmPasswordField.setHorizontalAlignment(SwingConstants.LEFT);
        confirmPasswordField.setColumns(10);

        errorLabel = new JLabel("");
        errorLabel.setBackground(Color.WHITE);
        errorLabel.setBounds(96, 225, 225, 14);
        panel.add(errorLabel);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(147, 112, 219));
        separator.setBounds(84, 239, 256, 12);
        panel.add(separator);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(165, 277, 93, 20);
        panel.add(btnRegister);

        JLabel lblNewLabel_1 = new JLabel("");
        //		lblNewLabel_1.setIcon(new ImageIcon(RegisterGUI.class.getResource("/Images/generic-user-purple.png")));
        lblNewLabel_1.setBounds(296, 29, 113, 116);
        contentPane.add(lblNewLabel_1);
        btnRegister.addActionListener(this);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegister) {
            if (usernameField.getText().isEmpty() || screenNameField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                errorLabel.setText("Incomplete fields");
            } else if (usernameField.getText().contains("_")) {
                errorLabel.setText("Invalid \"_\" character entered.");
            } else {
                if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                    errorLabel.setText("Passwords do not match");
                } else {
                    c.send(new Message("REGISTER", usernameField.getText(), BCrypt.hashpw(confirmPasswordField.getText(), loginGUI.getSalt()), screenNameField.getText()));
                }
            }
        }
    }

    public JPanel getPane() {
        return contentPane;
    }
}

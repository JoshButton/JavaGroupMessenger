package Client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginGUI implements ActionListener {

    private JFrame frmLogin;
    private JTextField usernameField;
    private JTextField passwordField = new JPasswordField();
    private JButton loginBtn = new JButton("Login");
    private final String salt = "$2a$10$lGOj9fZo88fkWeAoSQscbu";
    private JButton registerBtn = new JButton("Register");
    private String username;
    private JLabel lblUsername, lblChatMessenger, lblSignIn, lblPassword;
    private Communication c;
    private RegisterGUI registerGUI;

    public LoginGUI(Communication c) {
        this.c = c;
        initialize();
        frmLogin.setVisible(true);
    }

    private void initialize() {

        frmLogin = new JFrame();
        frmLogin.setTitle("Login");
        frmLogin.getContentPane().setBackground(new Color(211, 211, 211));
        frmLogin.setBounds(100, 100, 763, 595);
        frmLogin.setMinimumSize(new Dimension(763, 595));
        frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLogin.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBounds(150, 186, 444, 254);
        frmLogin.getContentPane().add(panel);
        panel.setLayout(null);

        lblUsername = new JLabel("USERNAME:");
        lblUsername.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblUsername.setForeground(Color.GRAY);
        lblUsername.setBounds(21, 78, 98, 16);
        panel.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setBounds(120, 73, 240, 28);
        panel.add(usernameField);
        usernameField.setColumns(10);

        lblPassword = new JLabel("PASSWORD:");
        lblPassword.setForeground(Color.GRAY);
        lblPassword.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblPassword.setBounds(21, 128, 83, 22);
        panel.add(lblPassword);

        passwordField.setBounds(120, 126, 240, 28);
        panel.add(passwordField);

        registerBtn.setBounds(122, 181, 112, 23);
        panel.add(registerBtn);

        loginBtn.setBounds(238, 181, 122, 23);
        loginBtn.setBackground(new Color(255, 255, 255));
        panel.add(loginBtn);

        lblSignIn = new JLabel("Sign in ");
        lblSignIn.setBounds(192, 11, 58, 22);
        panel.add(lblSignIn);
        lblSignIn.setForeground(Color.GRAY);
        lblSignIn.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 17));

        lblChatMessenger = new JLabel();
        lblChatMessenger.setForeground(new Color(105, 105, 105));
        lblChatMessenger.setBounds(150, 40, 450, 106);
        lblChatMessenger.setIcon(new ImageIcon(MainActivityGUI.class.getResource("/images/jsm.png")));
        frmLogin.getContentPane().add(lblChatMessenger);

        loginBtn.addActionListener(this);
        registerBtn.addActionListener(this);
        frmLogin.setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(211, 211, 211));
        frmLogin.setJMenuBar(menuBar);

        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);
        JMenu mnAbout = new JMenu("About");
        menuBar.add(mnAbout);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Incomplete fields!", "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                username = usernameField.getText();
                c.send(new Message("LOGIN", usernameField.getText(), BCrypt.hashpw(passwordField.getText(), salt)));
            }
        }
        if (e.getSource() == registerBtn) {
            registerGUI = new RegisterGUI(this, c);
        }

    }

    public String getUsername() {
        return username;
    }

    public String getSalt() {
        return salt;
    }

    public void disposeGUI() {
        frmLogin.dispose();
    }

    public RegisterGUI getRegisterGUI() {
        return registerGUI;
    }

    public JFrame getloginGUIFrame() {
        return frmLogin;
    }
}

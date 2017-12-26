package GroupChat.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatDialog extends JDialog implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    JFrame parentFrame;
    JEditorPane editor;
    JButton chat_sendButton;
    JTextField inputField;
    PrintWriter writer;
    Socket socket;

    public static String SEND_DIRECT_MESSAGE = "5ae38464206a415b881044126a46dbda";
    public static String ACTIVATE_DIALOG = "d11b5ac490c246a6bb60e7a96bb06af5";
    public static String DEACTIVATE_DIALOG = "e078af7523934789818b6522738253cb";

    public ChatDialog(JFrame frame, Socket socket, String title) {
        super(frame, title, Dialog.ModalityType.MODELESS);
        parentFrame = frame;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }

        setSize(350, 350);
        setLocationRelativeTo(parentFrame);

        int bottomHeight = 35;
        int bottomWidth = 50;
        Dimension d = new Dimension(bottomWidth, bottomHeight);

        JPanel p = new JPanel(new BorderLayout());
        editor = new JEditorPane();
        editor.setEditable(false);
        editor.setFont(new Font("Arial", Font.PLAIN, 14));
        editor.setText("Connected..");
        editor.setBackground(new Color(226, 226, 226));
        editor.setForeground(new Color(43, 43, 43));

        JScrollPane sp = new JScrollPane(editor);

        JPanel bottomPane = new JPanel(new GridBagLayout());
        chat_sendButton = new JButton("Send");
        chat_sendButton.addActionListener(this);
        chat_sendButton.setPreferredSize(d);

        inputField = new JTextField(100);
        inputField.setPreferredSize(d);
        inputField.addActionListener(this);
        addWindowListener(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        bottomPane.add(inputField, gbc);

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        bottomPane.add(chat_sendButton, gbc);

        p.add(sp, "Center");
        p.add(bottomPane, "South");
        add(p);

        setVisible(true);
    }

    public void write2Editor(String in) {
        write2Editor(in, false);
    }

    public void activateDialog(boolean b) {

        if (b == true) {
            chat_sendButton.setEnabled(true);
        } else {
            chat_sendButton.setEnabled(false);
        }
    }

    public void write2Editor(String in, boolean nextLine) {
        if (nextLine) {
            editor.setText(editor.getText() + "\n" + in + "\n");
        } else {
            editor.setText(editor.getText() + "\n" + in);
        }
        inputField.requestFocus();
        editor.setCaretPosition(editor.getText().length());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == chat_sendButton || event.getSource() == inputField) {

            if (chat_sendButton.isEnabled()) {
                String text = inputField.getText();
                if (text.trim().length() == 0) {
                    return;
                }

                writer.println(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + text);
                write2Editor("Me: " + inputField.getText());
                inputField.setText("");
            }
        }
    }

    @Override
    public void windowClosed(WindowEvent a10) {
    }

    @Override
    public void windowActivated(WindowEvent a11) {
    }

    @Override
    public void windowDeactivated(WindowEvent a12) {
    }

    @Override
    public void windowIconified(WindowEvent a15) {
    }

    @Override
    public void windowDeiconified(WindowEvent a16) {
    }

    @Override
    public void windowOpened(WindowEvent a17) {
    }

    @Override
    public void windowClosing(WindowEvent a13) {
        System.out.println("dialog closing");
        writer.println(DEACTIVATE_DIALOG + " " + getTitle());
        setVisible(false);
        dispose();
    }

}

package GroupChat.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;

public class Client extends JFrame implements ActionListener, ListSelectionListener, MouseListener {

    private static final long serialVersionUID = 1L;

    int screen_width = 900, screen_height = 600;
    int side_panel_width = 150;
    int bottom_panel_height = 45;
    int server_port = 8081;
    String server_ip = "localhost";
    String clientName = "client";
    JDialog oneTimeDialog;
    JDialog chatDialog;

    Thread receiver, sender;
    HashMap<String, ChatDialog> channels;
    Socket socket;	// server socket

    JButton sendButton, loginbutton, registerbutton;
    JTextField inputField;
    JEditorPane editor;
    JTextField name_field, password_field;
    JLabel label_name;

    JList<String> list;
    DefaultListModel<String> model;
    boolean clickedAlready = false;

    static boolean threads_running_flag = false;
    public static String ADD_CLIENT = "74d228153c5e4554a3f706337978f718";
    public static String REMOVE_CLIENT = "8b8b77255d4443ccbe3032f57b8fe3a5";
    public static String LIST_CLIENTS = "d34638d4Bcd9453db6631e4B64f6c376";
    public static String START_CHAT_DIALOG = "78e11ad4850b4cce825d2bf86dd57cec";
    public static String SEND_DIRECT_MESSAGE = "5ae38464206a415b881044126a46dbda";
    public static String ACTIVE_DIALOG = "d11b5ac490c246a6bb60e7a96bb06af5";
    public static String DEACTIVE_DIALOG = "e078af7523934789818b6522738253cb";

    Client() {
        try {
            socket = new Socket("localhost", 8081);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        setBackground(Color.white);
        setSize(screen_width, screen_height);
        setTitle("Group Messenger");
        channels = new HashMap<>();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellWidth(side_panel_width);
        list.setFixedCellHeight(30);
        list.addListSelectionListener(this);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.addMouseListener(this);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setPreferredSize(new Dimension(side_panel_width, bottom_panel_height));

        createOneTimeDialog();

        createUsersPanel();
        createMainPanel();

        inputField.requestFocus();
        setVisible(true);
    }

    public void createOneTimeDialog() {
        oneTimeDialog = new JDialog(this, "LOGIN", Dialog.ModalityType.APPLICATION_MODAL);
        oneTimeDialog.setLocationRelativeTo(null);
        oneTimeDialog.setSize(450, 220);
        oneTimeDialog.setLocation(screen_width / 2 - 300 / 2, screen_height / 2 - 300 / 2);
        oneTimeDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                dispose();
                System.exit(1);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        JPanel p = new JPanel(new GridLayout(3, 2));
        label_name = new JLabel("Username: ");
        JLabel label_ip = new JLabel("Password: ");
        name_field = new JTextField(15);
        password_field = new JPasswordField(15);
        password_field.setText("test");
        name_field.addActionListener(this);
        password_field.addActionListener(this);
        password_field.setPreferredSize(new Dimension(400, 28));
        name_field.setPreferredSize(new Dimension(400, 28));
        loginbutton = new JButton("Login");
        loginbutton.addActionListener(this);
        loginbutton.setPreferredSize(new Dimension(110, 30));
        loginbutton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginbuttonMouseClicked(evt);
            }
        });
        registerbutton = new JButton("Register");
        registerbutton.addActionListener(this);
        registerbutton.setPreferredSize(new Dimension(110, 30));
        registerbutton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                registerbuttonMouseClicked(evt);
            }
        });

        JPanel p1 = new JPanel(new GridBagLayout());
        JPanel p2 = new JPanel(new GridBagLayout());
        JPanel p3 = new JPanel(new GridBagLayout());
        JPanel p4 = new JPanel(new GridBagLayout());
        JPanel p5 = new JPanel(new GridBagLayout());
        JPanel p6 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Insets insets = new Insets(10, 0, 10, 30);
        gbc.insets = insets;

        p1.add(label_name);
        p2.add(name_field, gbc);
        p3.add(label_ip);
        p4.add(password_field, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 15, 5);
        p5.add(loginbutton, gbc);
        p6.add(registerbutton, gbc);

        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        p.add(p5);
        p.add(p6);

        panel.add(p, "Center");

        oneTimeDialog.add(panel);
        oneTimeDialog.setVisible(true);
    }

    public void createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel listPanel = new JPanel(new GridLayout());
        JLabel label_online = new JLabel("Online");
        JScrollPane listScroll = new JScrollPane(listPanel);

        topPanel.setPreferredSize(new Dimension(side_panel_width, 50));
        topPanel.add(label_online);
        listPanel.add(list);

        panel.add(topPanel, "North");
        panel.add(listScroll, "Center");
        panel.add(sendButton, "South");

        add(panel, "East");
    }

    public void createMainPanel() {
        JPanel main_panel = new JPanel(new BorderLayout());
        inputField = new JTextField("");
        inputField.setPreferredSize(new Dimension(100, bottom_panel_height));
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputField.addActionListener(this);

        editor = new JEditorPane();
        editor.setEditable(false);
        editor.setFont(new Font("Arial", Font.PLAIN, 15));
        editor.setText("Welcome...");
        editor.setBackground(new Color(226, 226, 226));
        editor.setForeground(new Color(43, 43, 43));

        JScrollPane sp = new JScrollPane(editor);

        main_panel.add(inputField, "South");
        main_panel.add(sp, "Center");
        add(main_panel, "Center");
    }

    public void write2Editor(String in) {
        write2Editor(in, false);
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

    public void test(Object in) {
        System.out.println(in.toString());
    }

    public static void main(String args[]) {
        Client client = new Client();

    }

    public void addAliasToList(String alias) {
        //test("request received for adding "+alias);

        if (!model.contains(alias) && (!alias.trim().equals(clientName))) {
            model.addElement(alias);
            channels.put(alias, null);
        }
    }

    public void removeAliasFromList(String alias) {
        //test("request received for removing "+alias);

        model.removeElement(alias);
        ChatDialog d = channels.get(alias);
        if (d != null) {
            if (d.isVisible()) {
                d.activateDialog(false);
                d.write2Editor(alias + " disconnects permanently");
            }
        }
        channels.remove(alias);
    }

    public void valueChanged(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            JList source = (JList) event.getSource();
            String item = null;

            if (source.getSelectedValue() != null) {
                item = source.getSelectedValue().toString().trim();
            }
            //test("item selected "+item);
        }
    }

    public void mouseClicked(MouseEvent e) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            if (e.getClickCount() == 2) {
                int index = list.locationToIndex(e.getPoint());
                String title = clientName + " to " + list.getSelectedValue();
                System.out.println("Double clicked on Item " + index + " " + title);

                ChatDialog d = channels.get(list.getSelectedValue());
                if (d == null) {
                    channels.put(list.getSelectedValue(), new ChatDialog(this, socket, title));
                    writer.println(START_CHAT_DIALOG + " " + title);
                } else {
                    if (!d.isVisible()) {
                        channels.put(list.getSelectedValue(), new ChatDialog(this, socket, title));
                        writer.println(START_CHAT_DIALOG + " " + title);
                    } else {
                        d.setVisible(true);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }
    }

    public void loginbuttonMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String username = name_field.getText();
            clientName = username;

            String password = password_field.getText();

            writer.println("Login");
            writer.println(username);
            writer.println(password);

            System.out.println(password);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String fromServer = reader.readLine();

            if (fromServer.equals("error")) {
                JOptionPane.showMessageDialog(this, "USERNAME/PASSWORD DO NOT MATCH.\n OR YOU ARE ALREADY LOGGED IN SOMEWHERE ELSE.", "Error!", JOptionPane.ERROR_MESSAGE);
                name_field.setText("");
                password_field.setText("");
                return;
            } else {
                connect();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registerbuttonMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String username = name_field.getText();
            clientName = username;

            String password = password_field.getText();

            writer.println("Register");
            writer.println(username);
            writer.println(password);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String fromServer = reader.readLine();

            if (fromServer.equals("error")) {
                JOptionPane.showMessageDialog(this, "USERNAME ALREADY EXHISTS", "Error!", JOptionPane.ERROR_MESSAGE);
                name_field.setText("");
                password_field.setText("");
                return;
            } else {
                connect();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    private void connect() {
        BufferedReader reader = null;
        try {

            setTitle("Group Messenger (Logged in as: " + clientName + ")");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String list_all_actives = reader.readLine();
            if (list_all_actives.contains(LIST_CLIENTS)) {
                test("list recieved " + list_all_actives);

                String[] aliases = list_all_actives.split(" ");
                for (int i = 1; i < aliases.length; i++) {
                    addAliasToList(aliases[i]);
                }
            }
            final Socket socket2 = socket;
            // sender and reciever threads
            sender = new Thread(new Runnable() {
                public void run() {
                    //
                }
            });
            receiver = new Thread(new Runnable() {
                public void run() {

                    try {
                        boolean flag = true;
                        String str = null;
                        int index_add_client = -1;
                        int index_remove_client = -1;
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));

                        while (flag) {
                            str = in.readLine();
                            test("log: " + str);

                            if (str.contains(START_CHAT_DIALOG + "")) {
                                String[] splits = str.split(" ");
                                ChatDialog d = channels.get(splits[1]);

                                if (d != null) {
                                    if (!d.isVisible()) {
                                        channels.put(splits[1], new ChatDialog(Client.this, socket, splits[3] + " to " + splits[1]));
                                    } else {
                                        d.activateDialog(true);
                                        d.write2Editor(splits[1] + " connects");
                                    }

                                } else {
                                    channels.put(splits[1], new ChatDialog(Client.this, socket, splits[3] + " to " + splits[1]));
                                }

                                continue;
                            } else if (str.contains(SEND_DIRECT_MESSAGE + "")) {
                                String[] splits = str.split(" ");
                                ChatDialog d = channels.get(splits[1]);
                                if (d != null) {
                                    d.write2Editor(splits[1] + ": " + str.substring(str.indexOf(splits[4])));
                                }

                                continue;
                            } else if (str.contains(ACTIVE_DIALOG + "")) {
                                String[] splits = str.split(" ");
                                ChatDialog d = channels.get(splits[1]);
                                if (d != null) {
                                    d.activateDialog(true);
                                    d.write2Editor(splits[1] + " connects");
                                }

                                continue;
                            } else if (str.contains(DEACTIVE_DIALOG + "")) {
                                String[] splits = str.split(" ");
                                ChatDialog d = channels.get(splits[1]);
                                if (d != null) {
                                    d.activateDialog(false);
                                    d.write2Editor(splits[1] + " disconnects");
                                }

                                continue;
                            }

                            index_add_client = str.indexOf(ADD_CLIENT + "");
                            boolean old_client = false;

                            if (index_add_client != -1) {
                                int index = str.indexOf(" has entered in the room");
                                if (index == -1) {

                                    index = index_add_client;
                                    old_client = true;
                                    addAliasToList(str.substring(0, index));
                                    str = str.substring(0, index);

                                } else {
                                    addAliasToList(str.substring(0, index));
                                    str = str.substring(0, index_add_client);
                                }

                            }

                            if ((str == null) || (old_client == true)) {
                                continue;
                            }

                            index_remove_client = str.indexOf(REMOVE_CLIENT + "");
                            if (index_remove_client != -1) {
                                int index = str.indexOf(" disconnects");
                                if (index == -1) {
                                    index = index_remove_client;
                                    old_client = true;

                                    removeAliasFromList(str.substring(0, index));
                                    str = str.substring(0, index);
                                } else {
                                    removeAliasFromList(str.substring(0, index));
                                    str = str.substring(0, index_remove_client);
                                }
                            }

                            if ((str.trim().length() == 0) || (old_client == true)) {
                                continue;
                            }

                            System.out.println("$ " + str);
                            write2Editor(str);

                            if (str.equals("END")) {
                                flag = false;
                            }
                        }

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });
            threads_running_flag = true;
            //sender.start();
            receiver.start();
            addAliasToList(clientName);
            oneTimeDialog.setVisible(false);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

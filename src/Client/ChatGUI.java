package Client;

import java.awt.*;
import java.awt.event.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatGUI extends JFrame implements ActionListener {

    private JPanel contentPane;
    private String chatName, chatType;
    private String nmessages = "";
    private JLabel chatNameLabel;
    private JButton sendBtn = new JButton("Send");
    private JButton attachFileBtn;
    private JTextArea listOfUsers, messages;
    private JTextArea text = new JTextArea();
    private ArrayList<String> users;
    private int numberOfNewMessages = 1;
    private JButton btnEmoji;
    private MainActivityGUI mainWindow;
    private Communication c;
    //private chat

    /**
     * @wbp.parser.constructor
     */
    public ChatGUI(String user, MainActivityGUI mainWindow, Communication c) {
        setTitle("Private Chat");
        chatType = "private";
        users = new ArrayList<String>();
        this.users.add(cutName(user));
        this.chatName = user;
        this.mainWindow = mainWindow;
        this.c = c;
        initialize();
    }

    //groupChat
    public ChatGUI(String chatName, ArrayList<String> users, MainActivityGUI mainWindow, Communication c) {
        setTitle("Group Chat");
        chatType = "group";
        this.users = users;
        this.chatName = chatName;
        this.mainWindow = mainWindow;
        this.c = c;
        initialize();
    }

    public void initialize() {

        setBounds(100, 100, 594, 466);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();
        panel_2.setPreferredSize(new Dimension(10, 40));
        panel_2.setMinimumSize(new Dimension(23, 29));
        contentPane.add(panel_2, BorderLayout.NORTH);

        chatNameLabel = new JLabel(cutName(chatName));
        chatNameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_2.add(chatNameLabel);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setEnabled(false);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane);

        JSplitPane splitPane_2 = new JSplitPane();
        splitPane_2.setEnabled(false);
        splitPane_2.setPreferredSize(new Dimension(179, 37));
        splitPane.setLeftComponent(splitPane_2);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        splitPane_2.setLeftComponent(scrollPane_1);

        messages = new JTextArea();
        messages.setFont(new Font("OpenSansEmoji", Font.PLAIN, 15));
        messages.setWrapStyleWord(true);
        messages.setLineWrap(true);
        messages.setMargin(new Insets(10, 5, 10, 5));
        messages.setEditable(false);
        scrollPane_1.setViewportView(messages);

        JScrollPane scrollPane_2 = new JScrollPane();
        splitPane_2.setRightComponent(scrollPane_2);

        listOfUsers = new JTextArea();
        listOfUsers.setFont(new Font("Tahoma", Font.PLAIN, 13));
        listOfUsers.setMargin(new Insets(10, 10, 10, 10));
        listOfUsers.setEditable(false);
        scrollPane_2.setViewportView(listOfUsers);
        fillUsers();

        splitPane_2.setDividerLocation(375);

        JPanel panel = new JPanel();
        splitPane.setRightComponent(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_3 = new JPanel();
        panel.add(panel_3, BorderLayout.NORTH);

        attachFileBtn = new JButton("");
        attachFileBtn.setBackground(SystemColor.control);
        attachFileBtn.setBorderPainted(false);
        attachFileBtn.setMargin(new Insets(0, 15, 0, 5));
        attachFileBtn.setIcon(new ImageIcon(ChatGUI.class.getResource("/images/paperclip.png")));
        attachFileBtn.addActionListener(this);
        panel_3.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        panel_3.add(attachFileBtn);

        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(3, 15));
        separator.setOpaque(true);
        separator.setOrientation(SwingConstants.VERTICAL);
        panel_3.add(separator);

        btnEmoji = new JButton("");
        btnEmoji.setBackground(SystemColor.control);
        btnEmoji.setBorderPainted(false);
        btnEmoji.setIcon(new ImageIcon(ChatGUI.class.getResource("/images/emojis.png")));
        btnEmoji.addActionListener(this);
        panel_3.add(btnEmoji);

        JPopupMenu popupMenu = new JPopupMenu();

        JPanel panel_4 = new JPanel();
        popupMenu.add(panel_4);
        panel_4.setLayout(new GridLayout(2, 2, 0, 0));

        JButton btnNewButton = new JButton("New button");
        panel_4.add(btnNewButton);

        JButton btnNewButton_1 = new JButton("New button");
        panel_4.add(btnNewButton_1);

        JButton btnNewButton_2 = new JButton("New button");
        panel_4.add(btnNewButton_2);

        JButton btnNewButton_3 = new JButton("New button");
        panel_4.add(btnNewButton_3);

        JSeparator separator_1 = new JSeparator();
        separator_1.setPreferredSize(new Dimension(3, 15));
        separator_1.setOrientation(SwingConstants.VERTICAL);
        separator_1.setOpaque(true);
        panel_3.add(separator_1);

        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setEnabled(false);
        panel.add(splitPane_1, BorderLayout.CENTER);

        JPanel panel_1 = new JPanel();
        splitPane_1.setRightComponent(panel_1);
        text.setFont(new Font("OpenSansEmoji", Font.PLAIN, 14));
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!text.getText().trim().isEmpty()) {
                        mainWindow.updatechat(chatName, mainWindow.getScreenName(), text.getText());
                        c.send(new Message("MESSAGE", mainWindow.getScreenName(), users, text.getText(), chatName));
                        text.setText("");
                    }
                }
            }
        });
        sendBtn.addActionListener(this);
        sendBtn.setBounds(10, 21, 87, 38);
        panel_1.setLayout(null);
        panel_1.add(sendBtn);

        JScrollPane scrollPane = new JScrollPane();
        splitPane_1.setLeftComponent(scrollPane);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);

        text.setMargin(new Insets(10, 10, 10, 10));
        scrollPane.setViewportView(text);

        splitPane_1.setDividerLocation(450);
        splitPane.setDividerLocation(250);
        setContentPane(contentPane);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendBtn) {
            if (!text.getText().trim().isEmpty()) {
                mainWindow.updatechat(chatName, mainWindow.getScreenName(), text.getText());
                if (chatType.equals("group")) {
                    if (!users.contains(mainWindow.getScreenName())) {
                        users.add(mainWindow.getScreenName());
                    }
                }
                c.send(new Message("MESSAGE", mainWindow.getScreenName(), users, text.getText(), chatName));
            }
            text.setText("");
        }
        if (e.getSource() == attachFileBtn) {
            if (chatType.equals("group")) {
                if (!users.contains(mainWindow.getScreenName())) {
                    users.add(mainWindow.getScreenName());
                }
            }
            FileSelector f = new FileSelector(getFullChatName(), users, mainWindow, c);
        }
        if (e.getSource() == btnEmoji) {
            byte[] emojiBytes = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x8A};
            String emojiAsString = new String(emojiBytes, Charset.forName("UTF-8"));
            text.append(emojiAsString);

        }
    }

    public String getChatName() {
        return cutName(chatName);
    }

    public void setMessages(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messages.append("\n\r" + message);
            }
        });
    }

    public String getFullChatName() {
        return chatName;
    }

    public String cutName(String fc) {
        int index = fc.indexOf("_");
        String date = fc.substring(index);
        String chatName = fc.substring(0, index);
        return chatName;
    }

    public String getChatType() {
        return this.chatType;
    }

    public void newMessage() {
        nmessages = "[NEW MESSAGE  " + "(" + numberOfNewMessages++ + ")" + "]";
    }

    public void resetMessageCount() {
        nmessages = "";
        numberOfNewMessages = 1;
    }

    public String toString() {
        return chatNameLabel.getText() + "  " + nmessages;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setChatName(String screenName) {
        chatNameLabel.setText(screenName);
        int index = chatName.indexOf("_");
        String date = chatName.substring(index);
        chatName = screenName + date;
    }

    public void removeUsers(String screenName) {
        users.remove(screenName);
    }

    public void addUsers(String screenName) {
        users.add(screenName);
    }

    public void fillUsers() {
        listOfUsers.setText("");
        for (String a : users) {
            listOfUsers.append('\n' + a);
        }
    }
}

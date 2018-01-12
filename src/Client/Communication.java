package Client;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import javax.swing.JOptionPane;

public class Communication extends Thread {

    private Socket s;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Server.Message m;
    LoginGUI loginGUI;

    private MainActivityGUI mainWindow;

    public Communication() {
        try {
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream("clientProperties.prop")) {
                p.load(fis);
            }
            s = new Socket(p.getProperty("SERVERIPADDRESS"), Integer.parseInt(p.getProperty("PORT")));
            loginGUI = new LoginGUI(this);
            ois = new ObjectInputStream(s.getInputStream());
            oos = new ObjectOutputStream(s.getOutputStream());
            while ((m = (Server.Message) ois.readObject()) != null) {
//				if( {
                readMessage(m);
//				}
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        try {
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMessage(Server.Message m) {
        switch (m.getType()) {
            case "LOGIN":
                if (m.getResponse()) {
                    loginGUI.disposeGUI();
                    mainWindow = new MainActivityGUI(m.getOnlineUser(), m.getOfflineUser(), m.getScreenName(), loginGUI.getUsername(), loginGUI.getSalt(), this);
                    send(new Message("UPDATEUSERS"));
                }
                if (!m.getResponse()) {
                    JOptionPane.showMessageDialog(loginGUI.getloginGUIFrame(), m.getReason(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "REGISTER":
                if (m.getResponse()) {
                    JOptionPane.showMessageDialog(this.loginGUI.getRegisterGUI().getPane(), "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loginGUI.getRegisterGUI().dispose();
                }
                if (!m.getResponse()) {
                    JOptionPane.showMessageDialog(null, m.getReason(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "MESSAGE":
                if (m.getDestUsers().size() > 1) {
                    if (mainWindow.chatExists(m.getChatName())) {
                        mainWindow.updatechat(m.getChatName(), m.getSrcUser(), m.getMessageBody());
                    } else {
                        ChatGUI a = new ChatGUI(m.getChatName(), m.getDestUsers(), mainWindow, this);
                        mainWindow.addchat(a);
                        mainWindow.updatechat(m.getChatName(), m.getSrcUser(), m.getMessageBody());
                    }
                    mainWindow.getChat(m.getChatName()).newMessage();
                } else {
                    int index = m.getChatName().indexOf("_");
                    String date = m.getChatName().substring(index);
                    String chatName = m.getSrcUser() + date;
                    if (mainWindow.chatExists(chatName)) {
                        mainWindow.updatechat(chatName, m.getSrcUser(), m.getMessageBody());
                    } else {
                        ChatGUI q = new ChatGUI(chatName, mainWindow, this);
                        mainWindow.addchat(q);
                        mainWindow.updatechat(chatName, m.getSrcUser(), m.getMessageBody());
                    }
                    mainWindow.getChat(chatName).newMessage();
                }
                break;
            case "FILE":
                byte[] fileBytes = m.getFileBytes();
                try {
                    File f = new File(m.getFileName());
                    System.out.print(f.getAbsolutePath());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                    for (byte a : fileBytes) {
                        bos.write(a);
                    }
                    bos.flush();
                    bos.close();
                    if (m.getDestUsers().size() > 1) {
                        if (mainWindow.chatExists(m.getChatName())) {
                            if (!mainWindow.getScreenName().equals(m.getSrcUser())) {
                                mainWindow.updatechat(m.getChatName(), m.getSrcUser(), "File " + m.getFileName() + " received. \n\rFile Location: " + f.getAbsolutePath() + "");
                            }
                        } else {
                            ChatGUI a = new ChatGUI(m.getChatName(), m.getDestUsers(), mainWindow, this);
                            mainWindow.addchat(a);
                            mainWindow.updatechat(m.getChatName(), m.getSrcUser(), "File " + m.getFileName() + " received.\n\rFile Location: " + f.getAbsolutePath() + "");
                        }
                        mainWindow.getChat(m.getChatName()).newMessage();
                    } else {
                        int index = m.getChatName().indexOf("_");
                        String date = m.getChatName().substring(index);
                        String chatName = m.getSrcUser() + date;
                        if (mainWindow.chatExists(chatName)) {
                            mainWindow.updatechat(chatName, m.getSrcUser(), "File " + m.getFileName() + " received.\n\rFile Location: " + f.getAbsolutePath() + "");
                        } else {
                            ChatGUI q = new ChatGUI(chatName, mainWindow, this);
                            mainWindow.addchat(q);
                            mainWindow.updatechat(chatName, m.getSrcUser(), "File " + m.getFileName() + " received.\n\rFile Location: " + f.getAbsolutePath() + "");
                        }
                        mainWindow.getChat(chatName).newMessage();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "UPDATEUSERS":
                mainWindow.fillUserLists(m.getOnlineUser(), m.getOfflineUser());
                break;
            case "SCREENNAME":
                System.out.println(m.getOldScreenName());
                System.out.print(m.getNewScreenName());
                for (ChatGUI a : mainWindow.getAllChats()) {
                    if (a.getUsers().contains(m.getOldScreenName())) {
                        a.removeUsers(m.getOldScreenName());
                        a.addUsers(m.getNewScreenName());
                        a.fillUsers();
                        if (a.getChatType().equals("private")) {
                            a.setChatName(m.getNewScreenName());
                        }
                        a.setMessages("\n\r" + m.getOldScreenName() + " changed Screen Name to " + m.getNewScreenName() + "\n\r");
                    }
                }
                break;
        }

    }

    public void closeSocket() {
        try {
            s.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

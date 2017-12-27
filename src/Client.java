
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Client extends ServerClient {

    private JFrame frame = new JFrame();
    private JPanel panel = new JPanel();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name = "";                // name of client
    private String password;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JPanel tabbedPanePanel = new JPanel();
    private List<Chat> chats = new ArrayList<Chat>();     // all chats open in the client
    private List<String> names = new ArrayList<String>(); // all users that are online
    private boolean isDone = false;  // used to check if server is done writing list of names to client

    @SuppressWarnings("fallthrough")
    public Client() {

        tabbedPanePanel.setLayout(new GridLayout(1, 2));
        tabbedPanePanel.add(tabbedPane);

        // add gui elements
        panel.setLayout(new BorderLayout());

        frame.setLayout(new BorderLayout());
        frame.add(tabbedPanePanel, BorderLayout.CENTER);

        frame.setTitle("Client");
        frame.setSize(500, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // try to instantiate socket and input/output stream
        try {
            socket = new Socket(getHost(), getPortNumber());
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(frame, "Can not connect to server.");
            System.exit(1);
        }

        chats.add(new Chat(this, "Global")); // global chat

        // keep asking for user name until server accepts the unique name
        try {
            String s = in.readUTF();
            while (!s.equals("[ACCEPTED]")) {
                name = JOptionPane.showInputDialog(frame, "Enter your name:").trim();
                out.writeUTF(name);
                s = in.readUTF();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error with server. Try again later.");
            System.exit(1);
        } catch (NullPointerException npe) {
            // close application if user did not enter name
            System.exit(1);
        }

        //keep asking for password
        try {
            String s = in.readUTF();
            switch (s) {
                case "[LOGIN]": {
                    while (!s.equals("[ACCEPTED]")) {
                        JPanel passwordPanel = new JPanel();
                        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
                        JLabel passwordLabel = new JLabel("Welcome back " + name);
                        JLabel passwordLabel2 = new JLabel("Enter your password:");
                        JPasswordField passwordField = new JPasswordField(10);
                        passwordPanel.add(passwordLabel);
                        passwordPanel.add(passwordLabel2);
                        passwordPanel.add(passwordField);
                        String[] options = new String[]{"Login", "Cancel"};
                        int option = JOptionPane.showOptionDialog(null, passwordPanel, "LOGIN",
                                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, options, options[1]);
                        if (option == 0) // pressing OK button
                        {
                            password = new String(passwordField.getPassword());
                        }
                        out.writeUTF(password);
                        s = in.readUTF();
                    }
                }
                case "[REGISTER]": {
                    while (!s.equals("[ACCEPTED]")) {
                        JPanel passwordPanel = new JPanel();
                        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
                        JLabel passwordLabel = new JLabel("Welcome to the chat room " + name);
                        JLabel passwordLabel2 = new JLabel("Lets create your account, choose a password:");
                        JPasswordField passwordField = new JPasswordField(10);
                        passwordPanel.add(passwordLabel);
                        passwordPanel.add(passwordLabel2);
                        passwordPanel.add(passwordField);
                        String[] options = new String[]{"Register", "Cancel"};
                        int option = JOptionPane.showOptionDialog(null, passwordPanel, "REGISTER",
                                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, options, options[1]);
                        if (option == 0) // pressing OK button
                        {
                            password = new String(passwordField.getPassword());
                        }
                        out.writeUTF(password);
                        s = in.readUTF();
                    }
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error with server. Try again later.");
            System.exit(1);
        } catch (NullPointerException npe) {
            // close application if user did not enter name
            System.exit(1);
        }

        frame.setTitle("COMPANY CHAT (LOGGED IN AS: " + name + ")");

        while (true) {
            StyledDocument doc; // holds the styleddocument to append text/image

            try {
                String input = in.readUTF();

                switch (input) {
                    case "[PICTURE]": {
                        // image received
                        /*
                            * Make array of bytes and get bytes from image.
                            * Convert array to actual image and display image
                         */
                        int length = in.readInt();
                        byte[] bytes = new byte[length];
                        in.readFully(bytes, 0, length);
                        String fromWho = in.readUTF(); // name of client that sent picture
                        String tab = in.readUTF();
                        doc = getStyledDocument(fromWho, tab);
                        BufferedImage b = ImageIO.read(new ByteArrayInputStream(bytes));
                        StyleConstants.setIcon(doc.getStyle("Picture"), new ImageIcon(b));
                        doc.insertString(doc.getLength(), fromWho + ":\n\t", doc.getStyle("Regular"));
                        doc.insertString(doc.getLength(), "ignored", doc.getStyle("Picture"));
                        doc.insertString(doc.getLength(), "\n", doc.getStyle("Regular"));
                        break;
                    }
                    case "[LIST]":
                        // get size of array, add all names in list and remove client's own name
                        int size = in.readInt();
                        names.clear();
                        for (int i = 0; i < size; i++) {
                            names.add(in.readUTF());
                        }
                        names.remove(name); // remove own client's name from list
                        isDone = true;
                        break;
                    default: {
                        // normal text
                        input = decryptMessage(input);
                        String fromWho = in.readUTF();
                        String tab = in.readUTF();
                        doc = getStyledDocument(fromWho, tab);
                        doc.insertString(doc.getLength(), fromWho + ": " + input + "\n", doc.getStyle("Regular"));
                        break;
                    }
                }
            } catch (BadLocationException ble) { // when using insertString
                ble.printStackTrace();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(frame, "Error with server. Try again later.");
                System.exit(1);
            }
        }
    }

    /**
     * Retrieve the correct styleddocument of the client to append the message
     *
     * @param fromWho Who sent the message
     * @param tab the tab name when sender sent the message
     * @return styleddocument to append the message
     */
    private StyledDocument getStyledDocument(String fromWho, String tab) {
        if (name.equals(fromWho)) {  // sender is receiving its own copy
            return chats.get(tabbedPane.getSelectedIndex()).getStyledDoc();

        } else if (tab.equals("Global")) { // get styleddocument from first tab
            return chats.get(0).getStyledDoc();

        } else {
            // check to see if tab is already open
            int index = -1;
            int size = tabbedPane.getTabCount();
            for (int i = 0; i < size; i++) {
                if (tabbedPane.getTitleAt(i).equals(fromWho)) {
                    index = i;
                }
            }

            if (index != -1) { // tab is open
                return chats.get(index).getStyledDoc();

            } else { // tab is not open, create new chat and get that styleddocument
                Chat c = new Chat(this, fromWho);
                chats.add(c);
                return c.getStyledDoc();
            }
        }
    }

    // getters and setters methods...
    public List<String> getNames() {
        return names;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public boolean getDone() {
        return isDone;
    }

    public void setDone(boolean choice) {
        isDone = choice;
    }

    public DataOutputStream getDos() {
        return out;

    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public static void main(String[] args) {
        new Client();
    }
}

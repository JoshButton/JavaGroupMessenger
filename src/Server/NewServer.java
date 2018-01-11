package Server;

import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;

public class NewServer {
    // TODO Auto-generated method stub

    private JFrame frame = new JFrame();
    private JTextPane textPane = new JTextPane();
    private ServerSocket serverSocket = null;
    String LoginRegister;
    static DatabaseAccess db;
    private StyledDocument doc = (StyledDocument) textPane.getDocument();
    Dictionary<String, ObjectOutputStream> onlineUsersWithSockets = new Hashtable<String, ObjectOutputStream>();

    public NewServer() {
        // set up gui components
        frame.setLayout(new BorderLayout());

        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // automatically scroll to bottom

        frame.add(new JScrollPane(textPane), BorderLayout.CENTER);
        textPane.setEditable(false); // user can't edit info
        frame.setTitle("Server");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        doc.addStyle("Regular", null);
        doc.addStyle("Picture", null);

        try {
            // create socket with PORT_NO
            Properties p = new Properties();
            FileInputStream fis = new FileInputStream("serverProperties.prop");
            p.load(fis);
            fis.close();
            serverSocket = new ServerSocket(Integer.parseInt(p.getProperty("PORT")));
            doc.insertString(doc.getLength(), "Server started at " + new Date() + "\n", doc.getStyle("Regular"));
            db = new DatabaseAccess();
            while (true) {
                // accept all clients and give each their own thread to run
                Socket socket = serverSocket.accept();
                ThreadedClient tc = new ThreadedClient(socket, this);
                new Thread(tc).start();
                doc.insertString(doc.getLength(), "Connection accepted from " + socket.getInetAddress() + "\n", doc.getStyle("Regular"));
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // nothing we can do here
            }
        }
    }

    public synchronized void insertToDoc(InetAddress a) {
        try {
            doc.insertString(doc.getLength(), "Connection from " + a + "closed" + "\n", doc.getStyle("Regular"));
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void addToDictionary(String screenName, ObjectOutputStream out) {
        onlineUsersWithSockets.put(screenName, out);
    }

    public synchronized void removeFromDictionary(String screenName) {
        onlineUsersWithSockets.remove(screenName);
    }

    public synchronized ObjectOutputStream getObjectOutputStream(String screenName) {
        return onlineUsersWithSockets.get(screenName);
    }

    public synchronized Enumeration<String> getAllKeys() {
        Enumeration<String> e = onlineUsersWithSockets.keys();
        return e;
    }

    public static void main(String[] args) {
        new NewServer();
    }

}

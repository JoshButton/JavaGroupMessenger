
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;



import java.awt.BorderLayout;

public class NewServer extends ServerClient{

	
		// TODO Auto-generated method stub
		    private JFrame frame = new JFrame();
		    private JTextPane textPane = new JTextPane();
		    private ServerSocket serverSocket = null;
		    String LoginRegister;
		    private StyledDocument doc = (StyledDocument) textPane.getDocument();
		    
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
		            serverSocket = new ServerSocket(getPortNumber());

		            doc.insertString(doc.getLength(), "Server started at " + new Date() + "\n", doc.getStyle("Regular"));

		            while (true) {
		                // accept all clients and give each their own thread to run
		                Socket socket = serverSocket.accept();
		                ThreadedClient tc = new ThreadedClient(socket);
		                new Thread(tc).start();
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
		    
		   
		    public static void main(String[] args) {
		    	new NewServer();
		    }

		    }

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class ThreadedClient extends Thread{
	private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Message message;
    private Dictionary<String,Socket>onlineUsersWithSockets = new Hashtable<String,Socket>();
    private ArrayList<String>toWho;
    private String reason;
    private Boolean result;
    public ThreadedClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //start database class
            Database db = new Database();
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
          
            while(socket.isConnected()){
            	message = (Message) in.readObject();
            	switch(message.getType()){
            	case ("LOGIN"):
            		result = db.checkLogin(message.getUserName(), message.getPassword());
            		reason = db.getResponse();
            		Message loginMessage = new Message("LOGIN",result,reason);
            		out.writeObject(loginMessage);
            		if(result){
            			//IF RESULT IS TRUE THEN ADD SOCKET AND USERNAME TO DICTIONARY
            			onlineUsersWithSockets.put(message.getUserName(), socket);
            		}
            		break;
            	case ("REGISTER"):
            		result = db.registerUser(message.getUserName(), message.getPassword());
            		reason = db.getResponse();
            		Message registerMessage = new Message("REGISTER",result,reason); 
            		out.writeObject(registerMessage); 
            		if(result){
            			//IF RESULT IS TRUE THEN ADD SOCKET AND USERNAME TO DICTIONARY
            			onlineUsersWithSockets.put(message.getUserName(), socket);
            		}
            		break;
            	case ("GET USERS"):
            		Message getUserMessage = new Message("USER",db.getOnlineUsers(),db.getOfflineUsers());
            		out.writeObject(getUserMessage);
            		break;
            	case ("FILE"):
            		toWho = message.getDestUsers();
            		byte[]b = message.getFile();
            		for(String dest: toWho){
            		//RETRIEVING THE SOCKET OF THE USERNAME THEN SENDING FILE TO USER
            		out = new ObjectOutputStream(onlineUsersWithSockets.get(dest).getOutputStream());
            		Message fileMessage = new Message("FILE",message.getsrcUser(),toWho,b,message.getFileName(),message.getChatName());
            		out.writeObject(fileMessage);
            		}
            		break;
            	case ("MESSAGE"):
            		toWho = message.getDestUsers();
            		for(String dest: toWho){
            			//RETRIEVING THE SOCKET OF THE USERNAME THEN SENDING FILE TO USER
            		out = new ObjectOutputStream(onlineUsersWithSockets.get(dest).getOutputStream());
            		Message textMessage = new Message("MESSAGE",message.getsrcUser(),toWho,message.getMessageBody(),message.getChatName());
            		out.writeObject(textMessage);
            		}
            		break;
            	default:     	
            	}
            }
        }catch(Exception ex){
        	ex.printStackTrace();
        }

    }


}

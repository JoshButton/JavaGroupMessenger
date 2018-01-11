package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

public class ThreadedClient extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Client.Message message;
    private ArrayList<String> toWho;
    private String reason;
    private Boolean result;

    NewServer newServer;

    public ThreadedClient(Socket socket, NewServer ns) {
        this.socket = socket;
        this.newServer = ns;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());

            in = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {

                message = (Client.Message) in.readObject();
                switch (message.getType()) {
                    case ("LOGIN"):
                        result = DatabaseAccess.checkLogin(message.getUserName(), message.getPassword());
                        reason = DatabaseAccess.getReason();
                        if (result) {
                            //            			//IF RESULT IS TRUE THEN ADD SOCKET AND USERNAME TO DICTIONARY
                            DatabaseAccess.setState(message.getUserName(), true);
                            newServer.addToDictionary(DatabaseAccess.getScreenName(message.getUserName()), out);
                        }
                        Message loginMessage = new Message("LOGIN", result, reason, DatabaseAccess.getScreenName(), DatabaseAccess.getOnlineUsers(), DatabaseAccess.getOfflineUsers());
                        out.writeObject(loginMessage);
                        break;
                    case ("REGISTER"):
                        result = DatabaseAccess.registerUser(message.getUserName(), message.getPassword(), message.getScreenName());
                        reason = DatabaseAccess.getReason();
                        Message registerMessage = new Message("REGISTER", result, reason, message.getScreenName());
                        out.writeObject(registerMessage);
                        for (Enumeration<ObjectOutputStream> e = newServer.onlineUsersWithSockets.elements(); e.hasMoreElements();) {
                            ObjectOutputStream ot = (ObjectOutputStream) e.nextElement();
                            ot.writeObject(new Message("UPDATEUSERS", DatabaseAccess.getOnlineUsers(), DatabaseAccess.getOfflineUsers()));
                        }
                        break;
                    case ("FILE"):
                        toWho = message.getDestUsers();
                        byte[] b = message.getFileBytes();
                        for (String dest : toWho) {
                            //RETRIEVING THE SOCKET OF THE USERNAME THEN SENDING FILE TO USER
                            out = newServer.getObjectOutputStream(dest);
                            Message fileMessage = new Message("FILE", message.getSrcUser(), toWho, b, message.getFileName(), message.getChatName());
                            out.writeObject(fileMessage);
                        }
                        break;
                    case ("MESSAGE"):
                        toWho = message.getDestUsers();
                        Message textMessage = new Message("MESSAGE", message.getSrcUser(), toWho, message.getMessageBody(), message.getChatName());
                        for (String dest : toWho) {
                            //RETRIEVING THE SOCKET OF THE USERNAME THEN SENDING FILE TO USER
                            if (!message.getSrcUser().equals(dest)) {
                                if (newServer.getObjectOutputStream(dest) != null) {
                                    ObjectOutputStream out = newServer.getObjectOutputStream(dest);
                                    out.writeObject(textMessage);
                                }
                            }
                        }
                        break;
                    case ("LOGOUT"):
                        DatabaseAccess.setState(DatabaseAccess.getScreenName(message.getUserName()), false);
                        newServer.removeFromDictionary(DatabaseAccess.getScreenName(message.getUserName()));
                        for (Enumeration<ObjectOutputStream> e = newServer.onlineUsersWithSockets.elements(); e.hasMoreElements();) {
                            ObjectOutputStream o = (ObjectOutputStream) e.nextElement();
                            o.writeObject(new Message("UPDATEUSERS", DatabaseAccess.getOnlineUsers(), DatabaseAccess.getOfflineUsers()));
                        }
                        break;
                    case ("SCREENNAME"):
                        DatabaseAccess.changeScreenName(message.getOldScreenName(), message.getNewScreenName());
                        newServer.removeFromDictionary(message.getOldScreenName());
                        newServer.addToDictionary(message.getNewScreenName(), out);
                        for (Enumeration<ObjectOutputStream> e = newServer.onlineUsersWithSockets.elements(); e.hasMoreElements();) {
                            ObjectOutputStream o = (ObjectOutputStream) e.nextElement();
                            o.writeObject(new Message("UPDATEUSERS", DatabaseAccess.getOnlineUsers(), DatabaseAccess.getOfflineUsers()));
                        }

                        for (Enumeration<ObjectOutputStream> e = newServer.onlineUsersWithSockets.elements(); e.hasMoreElements();) {
                            ObjectOutputStream o = (ObjectOutputStream) e.nextElement();
                            o.writeObject(new Message("SCREENNAME", message.getOldScreenName(), message.getNewScreenName(), true));
                        }
                        break;
                    case ("PASSWORD"):
                        DatabaseAccess.changePassword(message.getUserName(), message.getPassword());
                        break;
                    case ("UPDATEUSERS"):
                        for (Enumeration<ObjectOutputStream> e = newServer.onlineUsersWithSockets.elements(); e.hasMoreElements();) {
                            ObjectOutputStream t = (ObjectOutputStream) e.nextElement();
                            t.writeObject(new Message("UPDATEUSERS", DatabaseAccess.getOnlineUsers(), DatabaseAccess.getOfflineUsers()));
                        }
                        break;
                    case ("CLOSE"):
                        socket.close();
                        break;
                    default:
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
                newServer.insertToDoc(socket.getInetAddress());
                socket.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void logOut() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

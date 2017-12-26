package GroupChat.server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public static final int PORT = 8081;
    static ServerSocket serverSocket;
    static HashMap<Socket, String> hashMap = new HashMap<>();
    static Socket socket;
    static String name = null;
    static int count = 0;

    public static String ADD_CLIENT = "74d228153c5e4554a3f706337978f718";
    public static String REMOVE_CLIENT = "8b8b77255d4443ccbe3032f57b8fe3a5";
    public static String LIST_CLIENTS = "d34638d4Bcd9453db6631e4B64f6c376";
    public static String START_CHAT_DIALOG = "78e11ad4850b4cce825d2bf86dd57cec";
    public static String SEND_DIRECT_MESSAGE = "5ae38464206a415b881044126a46dbda";
    public static String ACTIVE_DIALOG = "d11b5ac490c246a6bb60e7a96bb06af5";
    public static String DEACTIVE_DIALOG = "e078af7523934789818b6522738253cb";

    public static void main(String[] args) throws Exception {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            Statement st = conn.createStatement();

            //st.executeUpdate("DROP TABLE CREDENTIALS IF EXISTS;"); //uncomment this to create a new table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS CREDENTIALS (USERNAME VARCHAR(255) PRIMARY KEY, PASSWORD VARCHAR(255));");

            st.close();
            conn.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        serverSocket = new ServerSocket(PORT);
        System.out.println("Started : " + serverSocket);

        Thread server_accepter = new Thread(new Runnable() {
            public void run() {

                while (true) {

                    try {
                        socket = serverSocket.accept();
                        System.out.println("Connection Accepted: " + socket);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String type = reader.readLine();
                        String name = reader.readLine();
                        String password = reader.readLine();

                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        if (type.equals("login")) {
                            if (!Login(name, password)) {
                                // write to that socket informing of login error
                                out.println("error");
                                System.out.println("LOGIN ERROR");
                                continue;
                            }
                        } else {
                            if (!Register(name, password)) {
                                // write to that socket informing of login error
                                out.println("error");
                                System.out.println("LOGIN ERROR");
                                continue;
                            }
                        }

                        Socket socket1 = socket;

                        count = count + 1;

                        hashMap.put(socket1, name);
                        System.out.println(hashMap.get(socket1) + " has entered in the room");
                        out.println(name + " you are now part of group");
                        remotePrint(socket1, hashMap.get(socket1) + " has entered in the room" + ADD_CLIENT);

                        //tellRoomMembers(socket1);
                        Set<Socket> ss = hashMap.keySet();
                        Iterator<Socket> it1 = ss.iterator();
                        String list_all_active_users = LIST_CLIENTS;

                        while (it1.hasNext()) {
                            String alias = hashMap.get(it1.next());
                            list_all_active_users = list_all_active_users + " " + alias;
                        }

                        out.println(list_all_active_users);
                        System.out.println("list send : " + list_all_active_users);

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }

                    final Socket socket2 = socket;

                    Thread receiver = new Thread(new Runnable() {
                        public void run() {

                            try {
                                String str = null;
                                BufferedReader in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));

                                while (true) {
                                    str = in.readLine();

                                    if ((str == null) | (str.equals("null")) | (str.equals("END"))) {
                                        System.out.println(hashMap.get(socket2) + " disconnects");
                                        remotePrint(socket2, hashMap.get(socket2) + " disconnects" + REMOVE_CLIENT);
                                        hashMap.remove(socket2);
                                        break;
                                    }

                                    boolean flag_nextloop = false;

                                    if ((str.contains(START_CHAT_DIALOG + "")) || (str.contains(SEND_DIRECT_MESSAGE + "")) || (str.contains(ACTIVE_DIALOG + "")) || (str.contains(DEACTIVE_DIALOG + ""))) {
                                        String[] splits = str.split(" ");
                                        System.out.println("@server : " + str);
                                        String to_client = splits[3];

                                        // to find to_client's socket and forward start chat message to it only
                                        Set<Socket> sockets = hashMap.keySet();
                                        Iterator<Socket> iterator = sockets.iterator();

                                        while (iterator.hasNext()) {
                                            Socket sc1 = iterator.next();
                                            String temp = hashMap.get(sc1);
                                            if (temp.equals(to_client)) {
                                                PrintWriter out1 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sc1.getOutputStream())), true);
                                                out1.println(str);
                                                flag_nextloop = true;
                                                break;
                                            }
                                        }
                                    }

                                    if (flag_nextloop == true) {
                                        continue;
                                    }

                                    System.out.println("" + hashMap.get(socket2) + ": " + str);
                                    remotePrint(socket2, "" + hashMap.get(socket2) + ": " + str);
                                }

                            } catch (Exception e) {
                                System.out.println(hashMap.get(socket2) + " disconnects");
                                remotePrint(socket2, hashMap.get(socket2) + " disconnects" + REMOVE_CLIENT);
                                hashMap.remove(socket2);
                                //e.printStackTrace();
                            }
                        }
                    });

                    receiver.start();
                }
            }

            public void remotePrint(Socket current, String text) {
                try {
                    Set<Socket> sockets = hashMap.keySet();
                    Iterator<Socket> iterator = sockets.iterator();

                    while (iterator.hasNext()) {
                        Socket temp = iterator.next();
                        if (temp != current) {
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(temp.getOutputStream())), true);
                            out.println(text);
                        }
                    }
                } catch (Exception exp) {
                    //
                }
            }

            public boolean nameAlreadyPresent(String name) {
                boolean present = false;
                Set<Socket> sockets = hashMap.keySet();
                Iterator<Socket> iterator = sockets.iterator();

                while (iterator.hasNext()) {
                    String alias = hashMap.get(iterator.next());
                    if (alias.equals(name)) {
                        present = true;
                        break;
                    }
                }

                return present;
            }

            private boolean Login(String name, String password) throws IOException {
                try {
                    Class.forName("org.h2.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
                    Statement st = conn.createStatement();

                    String salt = BCrypt.gensalt();
                    String hashpassword = BCrypt.hashpw(password, salt);

                    String query = "SELECT * FROM CREDENTIALS;";
                    ResultSet rs = st.executeQuery(query);

                    boolean userexists = false;

                    while (rs.next()) {
                        String cusername = rs.getString("USERNAME");
                        String cpassword = rs.getString("PASSWORD");

                        if (cusername.equals(name)) {
                            userexists = true;

                            if (hashpassword.trim().equals(cpassword.trim())) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                    }

                    rs.close();

                    if (!userexists) {
                        return false;
                    }

                    st.close();
                    conn.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    return false;
                }
                return true;
            }

            private boolean Register(String name, String password) throws IOException {
                try {
                    Class.forName("org.h2.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
                    Statement st = conn.createStatement();

                    String salt = BCrypt.gensalt();
                    String hashpassword = BCrypt.hashpw(password, salt);

                    st.executeUpdate("INSERT INTO CREDENTIALS VALUES('" + name + "', '" + hashpassword + "');");

                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    System.out.println("Registered " + name);

                    st.close();
                    conn.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    return false;
                }
                return true;
            }

        });

        server_accepter.start();

        server_accepter.join();

        System.out.println("Closing Client Connections");
        socket.close();
        serverSocket.close();

    }
}

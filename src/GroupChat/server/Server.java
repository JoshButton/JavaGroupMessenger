package GroupChat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    private static final int portNumber = 4444;

    private final int serverPort;
    private List<ServerThread> clients; // or "protected static List<ClientThread> clients;"

    public static void main(String[] args) {
        Server server = new Server(portNumber);
        server.startServer();
    }

    public Server(int portNumber) {
        this.serverPort = portNumber;
    }

    public List<ServerThread> getClients() {
        return Collections.unmodifiableList(clients);
    }

    private void startServer() {
        clients = new ArrayList<>();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            acceptClients(serverSocket);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + serverPort);
            System.exit(1);
        }
    }

    private void acceptClients(ServerSocket serverSocket) {

        System.out.println("server starts port = " + serverSocket.getLocalSocketAddress());
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("accepts : " + socket.getRemoteSocketAddress());
                ServerThread client = new ServerThread(this, socket);
                Thread thread = new Thread(client);
                thread.start();
                clients.add(client);
            } catch (IOException ex) {
                System.out.println("Accept failed on : " + serverPort);
            }
        }
    }
}

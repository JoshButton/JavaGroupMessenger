package GroupChat.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread implements Runnable {

    private final Socket socket;
    private PrintWriter clientOut;
    private final Server server;

    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    private PrintWriter getWriter() {
        return clientOut;
    }

    @Override
    public void run() {
        try {
            // setup
            this.clientOut = new PrintWriter(socket.getOutputStream(), false);
            Scanner in = new Scanner(socket.getInputStream());

            // start communicating
            while (!socket.isClosed()) {
                if (in.hasNextLine()) {
                    String input = in.nextLine();
                    for (ServerThread thatClient : server.getClients()) {
                        PrintWriter thatClientOut = thatClient.getWriter();
                        if (thatClientOut != null) {
                            thatClientOut.write(input + "\r\n");
                            thatClientOut.flush();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

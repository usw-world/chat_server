package chatserver;

import java.io.*;
import java.net.*;

public class SocketIO {
    public Socket socket;
    public BufferedReader reader;
    public BufferedWriter writer;
    public SocketIO(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(
            new InputStreamReader(
                socket.getInputStream()
            )
        );
        writer = new BufferedWriter(
            new OutputStreamWriter(
                socket.getOutputStream()
            )
        );
    }
}

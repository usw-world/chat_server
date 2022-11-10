package chatserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class TemporaryClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int PORT_NUMBER = 9910;
        try (Socket socket = new Socket(hostname, PORT_NUMBER)) {

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                out.write("This message is from Temporary Client.\n");
                out.flush();
    
                String res = reader.readLine();
                System.out.println(res);

                System.in.read();
            }

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
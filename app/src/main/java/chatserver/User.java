package chatserver;

import java.io.*;
import java.net.*;

import org.json.JSONObject;

import chatserver.Message.MessageManager;

import java.lang.Thread;

public class User {
    /* these is for test on one PC >> */
    String mode = "product";
    // String mode = "test";
    static int ccount = 0;
    int count;
    public String getIP() {
        if(mode == "test")
            return "127.0.0." + count;
        return address.getHostString();
    }
    /* << these is for test on one PC */
    boolean isConnect = false;

    Thread inputThread;
    SocketIO io;

    public InetSocketAddress address;
    public MessageManager messageManager;

    public User(SocketIO io, MessageManager messageManager) {
        /* these is for test on one PC >> */
        count = ++ccount;
        if(ccount >= 4) ccount = 0;
        /* << these is for test on one PC */

        User self = this;
        if(!io.socket.isClosed()) {
            isConnect = true;
        }
        this.io = io;
        this.messageManager = messageManager;
        address = (InetSocketAddress)io.socket.getRemoteSocketAddress();
        inputThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(isConnect) {
                        String message = io.reader.readLine();
                        messageManager.onMessage(self, message);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        inputThread.start();
    }
    public boolean getConnectState() {
        try {
            JSONObject json = new JSONObject();
            json.put("type", MessageType.CHECK_CONNECT);
            io.writer.write(json.toString() + "\n");
            io.writer.flush();
            return true;
        } catch(IOException ioe) {
            System.out.println("IOException in 'boolean getConnectState()'");
            ioe.printStackTrace();
            return false;
        } catch(Exception e) {
            System.out.println("Exception in 'boolean getConnectState()'");
            return false;
        }
    }
}

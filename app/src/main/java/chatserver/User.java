package chatserver;

import java.io.*;
import java.net.*;

import org.json.JSONObject;

import chatserver.Message.MessageManager;

import java.lang.Thread;

public class User {
    boolean isConnect = false;

    Thread inputThread;
    SocketIO io;

    public InetSocketAddress address;
    public MessageManager messageManager;

    public User(SocketIO io, MessageManager messageManager) {
        User self = this;
        if(!io.socket.isClosed()) {
            isConnect = true;
        }
        this.io = io;
        this.messageManager = messageManager;
        inputThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(isConnect) {
                        String message = io.reader.readLine();
                        address = (InetSocketAddress)io.socket.getRemoteSocketAddress();
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
            Message message = new Message(MessageManager.TYPE_CHECK_CONNECT, null, null);
            JSONObject json = new JSONObject(message);
            io.writer.write(json.toString()+"\n");
            io.writer.flush();
            return true;
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }
}

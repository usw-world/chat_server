package chatserver;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import org.json.simple.parser.JSONParser;
import chatserver.Message.MessageManager;

public class App {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.run();
    }
}
class ChatServer {
    JSONObject userInfo;
    final int PORT_NUMBER = 9910;
    Socket socket;

    static ArrayList<User> users = new ArrayList<>();
    UserInformation userMap;

    public void run() {
        try(ServerSocket server = new ServerSocket(PORT_NUMBER)) {
            userMap = new UserInformation();
            System.out.println("class room chat server is running on port " + PORT_NUMBER);
            while(true) {
                socket = server.accept();
                SocketIO io = new SocketIO(socket);
                MessageManager ms = new MessageManager() {
                    @Override
                    public void onMessage(User user, String message) {
                        messageHandler(user, message);
                    };
                    @Override
                    public void sendMessage(User user, String message) {
                        try {
                            io.writer.write(message + "\n");
                            io.writer.flush();
                        } catch(IOException ioe) {
                            ioe.printStackTrace();
                        }
                    };
                };
                users.add(new User(io, ms));
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void messageHandler(User user, String message) {
        JSONObject request = new JSONObject();
        try {
            request = new JSONObject(message);
            switch(request.getString("type")) {
                case MessageManager.TYPE_REQUEST_USERS:
                    ArrayList<String> userList = new ArrayList<>();
                    ArrayList<String> ipList = new ArrayList<>();
                    for(int i=0; i<users.size(); i++) {
                        User item = users.get(i);
                        if(!item.getConnectState()) {
                            users.remove(item);
                            i--;
                        } else {
                            String address = user.address.getHostString();
                            String name = userMap.getNameWithAddress(address);
                            System.out.println(address);
                            userList.add(name);
                            ipList.add(address);
                        }
                    }
                    JSONObject json = new JSONObject();
                    json.put("type", MessageManager.TYPE_RESPONSE_USERS);
                    json.put("user_list", userList);
                    json.put("ip_list", ipList);
                    sendMessage(user, json.toString());
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public boolean sendMessage(User user, String message) {
        if(user.getConnectState()) {
            user.messageManager.sendMessage(user, message);
            return true;
        } else {
            return false;
        }
    }
    public int broacastMessage(String message) {
        int count = 0;
        for(int i=0; i<users.size(); i++) {
            User user = users.get(i);
            if(user.getConnectState()) {
                user.messageManager.sendMessage(user, message);
                count ++;
            }
        }
        return count;
    }
}

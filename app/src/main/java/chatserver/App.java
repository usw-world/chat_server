package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.*;

import com.google.common.net.MediaType;

import chatserver.Message.MessageManager;
import chatserver.Room.*;

public class App {
    static public ChatServer server;
    public static void main(String[] args) {
        server = new ChatServer();
        server.run();
    }
}
class ChatServer {

    JSONObject userInfo;
    final int PORT_NUMBER = 9910;
    Socket socket;

    static public ArrayList<User> users = new ArrayList<>();
    public UserInformation userMap;

    static ArrayList<PrivateRoom> privateRooms = new ArrayList<>();
    static ArrayList<GroupRoom> groupRooms = new ArrayList<>();

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
                User newUser = new User(io, ms);
                User alreadyUser = null;
                for(int i=0; i<users.size(); i++) {
                    if(users.get(i).getIP().equals(newUser.getIP())) {
                        alreadyUser = users.get(i);
                    }
                }
                if(alreadyUser != null)
                    users.remove(alreadyUser);
                    
                users.add(newUser);
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
    public void messageHandler(User sender, String message) {
        JSONObject request = new JSONObject();
        try {
            request = new JSONObject(message);
            System.out.println(request);
            switch(request.getInt("type")) {
                case MessageType.REQUEST_USERS: {
                    ArrayList<String> userList = new ArrayList<>();
                    ArrayList<String> ipList = new ArrayList<>();
                    for(int i=0; i<users.size(); i++) {
                        User user = users.get(i);
                        if(user.getConnectState()) {
                            String address = user.getIP();
                            String name = userMap.getNameWithAddress(address);
                            if(!address.equals(sender.getIP())) {
                                userList.add(name);
                                ipList.add(address);
                            }
                        }
                    }
                    JSONObject json = new JSONObject();
                    json.put("userList", userList);
                    json.put("ipList", ipList);
                    sendMessage(MessageType.RESPONSE_USERS, sender, json);
                } break;
                case MessageType.START_CHAT: {
                    User targetUser = null;
                    for(int i=0; i<users.size(); i++) {
                        if(request.getString("targetIp").equals(users.get(i).getIP())) {
                            targetUser = users.get(i);
                        }
                    }
                    if(targetUser != null && targetUser.getConnectState()) {
                        PrivateRoom responseRoom = null;
                        JSONObject json = new JSONObject();
                        for(PrivateRoom room : privateRooms) {
                            if(room.contains(sender) && room.contains(targetUser)) {
                                responseRoom = room;
                            }
                        }
                        if(responseRoom == null) {
                            ArrayList<User> inners = new ArrayList<>();
                            inners.add(sender);
                            inners.add(targetUser);
                            responseRoom = new PrivateRoom(inners);
                            privateRooms.add(responseRoom);
                            json.put("existRoom", false);
                        } else {
                            json.put("existRoom", true);
                        }
                        json.put("roomNumber", responseRoom.roomNumber);
                        sendMessage(MessageType.REPONSE_ROOM_NUMBER, sender, json);
                    }
                } break;
                case MessageType.SEND_CHAT: {
                    int roomNumber = request.getInt("roomNumber");
                    Room room = getRoom(roomNumber);
                    if(room != null) {
                        room.addChat(new ChatItem(sender, request.getString("text")));
                        for(User user : room.innerUsers) {
                            JSONObject json = new JSONObject();
                            String speaker = userMap.getNameWithAddress(sender.getIP());
                            boolean myOwn = user.getIP().equals(sender.getIP());
                            json.put("text", request.getString("text"));
                            json.put("speaker", speaker);
                            json.put("speakerIp", sender.getIP());
                            json.put("roomNumber", roomNumber);
                            json.put("myOwn", myOwn);
                            User t = getUserWithAddress(user.getIP());
                            if(t != null)
                                sendMessage(MessageType.RECEIVE_CHAT, t, json);
                        }
                    }
                } break;
                case MessageType.REQUEST_INVITE_LIST: {
                    JSONObject json = new JSONObject();
                    ArrayList<String> userList = new ArrayList<>();
                    ArrayList<String> ipList = new ArrayList<>();
                    Room room = getRoom(request.getInt("roomNumber"));
                    for(int i=0; i<users.size(); i++) {
                        User user = users.get(i);
                        if(user.getConnectState()) {
                            String address = user.getIP();
                            String name = userMap.getNameWithAddress(address);
                            if(!address.equals(sender.getIP())
                            && !room.contains(user)) {
                                userList.add(name);
                                ipList.add(address);
                            }
                        }
                    }
                    json.put("userList", userList);
                    json.put("ipList", ipList);
                    json.put("roomNumber", room.roomNumber);
                    sendMessage(MessageType.RESPONSE_INVITE_LIST, sender, json);
                } break;
                case MessageType.INVITE_ROOM: {
                    User target = getUserWithAddress(request.getString("targetIp"));
                    Room originRoom = getRoom(request.getInt("roomNumber"));
                    JSONObject json = new JSONObject();
                    if(isPrivateRoom(originRoom)) {
                        ArrayList<User> userList = new ArrayList<>();
                        userList.add(target);
                        for(User u : originRoom.innerUsers) {
                            userList.add(u);
                        }
                        GroupRoom newRoom = new GroupRoom(userList);
                        groupRooms.add(newRoom);
                        json.put("roomNumber", newRoom.roomNumber);
                        for(User user : userList) {
                            User u = getUserWithAddress(user.getIP());
                            sendMessage(MessageType.INVITE_ROOM, u, json);
                        }
                        originRoom = newRoom;
                    } else {
                        originRoom.innerUsers.add(target);
                        json.put("roomNumber", originRoom.roomNumber);
                        sendMessage(MessageType.INVITE_ROOM, target, json);
                    }
                    originRoom.chatLog.add(new ChatItem(target, true));
                    JSONObject joinJson = new JSONObject();
                    joinJson.put("roomNumber", originRoom.roomNumber);
                    joinJson.put("userName", getUserName(target));
                    for(User u : originRoom.innerUsers) {
                        sendMessage(MessageType.JOIN_ALERT, u, joinJson);
                    }
                } break;
                case MessageType.REQUEST_CHAT_LOG: {
                    int roomNumber = request.getInt("roomNumber");

                    Room room = getRoom(roomNumber);
                    JSONObject json = room.getChatListJson();
                    JSONArray ips = json.getJSONArray("speakerIpList");
                    ArrayList<String> speakerList = new ArrayList<>();

                    for(int i=0; i<ips.length(); i++) {
                        speakerList.add(userMap.getNameWithAddress(ips.getString(i)));
                    }
                    json.put("speakerList", speakerList);
                    json.put("receiver", userMap.getNameWithAddress(sender.getIP()));
                    json.put("roomNumber", roomNumber);
                    sendMessage(MessageType.REQUEST_CHAT_LOG, sender, json);
                } break;
                case MessageType.REQUEST_GROUP_ROOM: {
                    JSONObject json = new JSONObject();
                    JSONArray rooms = new JSONArray();
                    for(GroupRoom room : groupRooms) {
                        if(room.contains(sender)) {
                            JSONObject r = new JSONObject();
                            r.put("roomNumber", room.roomNumber);
                            JSONArray userArray = new JSONArray();
                            for(User user : room.innerUsers) {  
                                userArray.put(userMap.getNameWithAddress(user.getIP()));
                            }
                            r.put("userList", userArray);
                            rooms.put(r);
                        }
                    }
                    json.put("roomList", rooms);
                    sendMessage(MessageType.REQUEST_GROUP_ROOM, sender, json);
                } break;
                default:
                    System.out.println("Message Handler hasn't run any proccess.");
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public String getUserName(User user) {
        return userMap.getNameWithAddress(user.getIP());
    }
    public User getUserWithAddress(String ip) {
        for(User user : users) {
            if(user.getIP().equals(ip) && user.getConnectState())
                return user;
        }
        return null;
    }
    public boolean isPrivateRoom(Room target) {
        for(PrivateRoom room : privateRooms) {
            if(room.roomNumber == target.roomNumber) {
                return true;
            }
        }
        return false;
    }
    public Room getRoom(int roomNumber) {
        Room room;
        for(int i=0; i<privateRooms.size(); i++) {
            room = privateRooms.get(i);
            if(room.roomNumber == roomNumber)
                return room;
        }
        for(int i=0; i<groupRooms.size(); i++) {
            room = groupRooms.get(i);
            if(room.roomNumber == roomNumber)
                return room;
        }
        return null;
    }
    public Room createRoom(ArrayList<User> inners) {
        Room room = new Room(inners);
        return room;
    }
    public boolean sendMessage(int type, User user, JSONObject json) {
        json.put("type", type);
        if(user.getConnectState()) {
            user.messageManager.sendMessage(user, json.toString());
            return true;
        } else {
            System.out.println("fail to send message.");
            return false;
        }
    }
    // public int sendMessageAll(String type, JSONObject json) {
    //     json.put("type", type);
    //     int count = 0;
    //     for(int i=0; i<users.size(); i++) {
    //         User user = users.get(i);
    //         if(user.getConnectState()) {
    //             user.messageManager.sendMessage(user, json.toString());
    //             count ++;
    //         }
    //     }
    //     return count;
    // }
}

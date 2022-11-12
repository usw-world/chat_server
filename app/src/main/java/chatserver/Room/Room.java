package chatserver.Room;

import java.util.*;

import org.json.*;

import chatserver.ChatItem;
import chatserver.User;

public class Room {
    static public int currentRoomNumber = 0;
    public int roomNumber;
    public ArrayList<User> innerUsers;
    public ArrayList<ChatItem> chatLog = new ArrayList<>();
    public Room(ArrayList<User> inners) {
        roomNumber = currentRoomNumber++;
        innerUsers = inners;
    }
    public void addChat(ChatItem item) {
        chatLog.add(item);
    }
    public JSONObject getChatListJson() {
        JSONObject json = new JSONObject();
        ArrayList<String> speakerIpList = new ArrayList<>();
        ArrayList<String> chatList = new ArrayList<>();

        for(ChatItem item : chatLog) {
            speakerIpList.add(item.author.getIP());
            chatList.add(item.text);
        }

        json.put("speakerIpList", speakerIpList);
        json.put("chatList", chatList);
        return json;
    } 
}
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
        ArrayList<Boolean> isAlertList = new ArrayList<>();

        for(ChatItem item : chatLog) {
            speakerIpList.add(item.author.getIP());
            chatList.add(item.text);
            isAlertList.add(item.isAlert);
        }

        json.put("speakerIpList", speakerIpList);
        json.put("chatList", chatList);
        json.put("isAlertList", isAlertList);
        return json;
    } 
    public boolean contains(User target) {
        for(User user : innerUsers) {
            if(user.getIP().equals(target.getIP()))
                return true;
        }
        return false;
    }
}
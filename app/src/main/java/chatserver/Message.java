package chatserver;

public class Message {
    
    public interface MessageManager {
        void onMessage(User user, String message);
        void sendMessage(User user, String message);
    }
}
class MessageType {
    static final int REQUEST_USERS = 0;
    static final int RESPONSE_USERS = 1;
    static final int SEND_CHAT = 2;
    static final int RECEIVE_CHAT = 3;
    static final int START_CHAT = 4;
    static final int CHECK_CONNECT = 5;
    static final int REPONSE_ROOM_NUMBER = 6;
    static final int INVITE_ROOM = 7;
    static final int REQUEST_ROOMS = 8;
    static final int REQUEST_INVITE_LIST = 9;
    static final int RESPONSE_INVITE_LIST = 10;
    static final int REQUEST_CHAT_LOG = 11;

    static final int REQUEST_GROUP_ROOM = 12;

    static final int JOIN_ALERT = 13;
}
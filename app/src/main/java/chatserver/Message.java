package chatserver;

public class Message {
    private String type;
    private String content;
    private String sender;
    
    public Message(String type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public interface MessageManager {
        static final String TYPE_REQUEST_USERS = "type_request_users";
        static final String TYPE_RESPONSE_USERS = "type_response_users";
        static final String TYPE_MESSAGE = "type_message";
        static final String TYPE_CHECK_CONNECT = "type_check_connect";

        void onMessage(User user, String message);
        void sendMessage(User user, String message);
    }
}
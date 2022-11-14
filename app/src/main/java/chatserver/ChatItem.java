package chatserver;

public class ChatItem {
    public User author;
    public String text;
    public boolean isAlert = false;
    public ChatItem(User author, String text) {
        this.author = author;
        this.text = text;
    }
    public ChatItem(User author, boolean isAlert) {
        this.author = author;
        this.text = "";
        this.isAlert = isAlert;
    }
}

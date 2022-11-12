package chatserver;

public class ChatItem {
    public User author;
    public String text;
    public ChatItem(User author, String text) {
        this.author = author;
        this.text = text;
    }
}

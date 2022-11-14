package chatserver.Room;

import java.util.ArrayList;

import chatserver.User;

public class PrivateRoom extends Room {
    public PrivateRoom(ArrayList<User> inners) {
        super(inners);
    }
}

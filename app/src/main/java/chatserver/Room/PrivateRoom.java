package chatserver.Room;

import java.util.ArrayList;

import chatserver.User;

public class PrivateRoom extends Room {
    public User[] eachUser = new User[2];
    public PrivateRoom(ArrayList<User> inners) {
        super(inners);
        eachUser[0] = inners.get(0);
        eachUser[1] = inners.get(1);
        if(eachUser[0] == eachUser[1]) {
            System.out.println("Each user is same one.");
        }
    }
    public boolean contains(User user) {
        if(eachUser[0] == user || eachUser[1] == user)
            return true;
        return false;
    }
}

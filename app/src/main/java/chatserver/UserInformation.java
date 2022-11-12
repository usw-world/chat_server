package chatserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.*;
import java.io.Reader;
import java.io.FileReader;

public class UserInformation {
    private HashMap<String, String> userInformations;
    public UserInformation() throws Exception {
        Reader reader = new FileReader(System.getProperty("user.dir") + "/user_map.json");
        JSONObject json = (JSONObject) new JSONParser().parse(reader);
        userInformations = (HashMap<String, String>)json;
    }
    public String getNameWithAddress(String address) {
        return userInformations.get(address);
    }
    public String getUserName(User user) {
        return getNameWithAddress(user.getIP());
    }
}

import java.util.*;

public class User {
    private String userDataColumns[] = { "name-s", "username-s", "uid-i", "lastLoggedIn-s", "JCal-i" };
    private Map<String, Object> userInfo = new HashMap<>();
    CommandUtil cu = new CommandUtil();
    boolean accountCreated = false;

    public User() {

    }

    public void SetInfoFromLogin(Map<Integer, List<Object>> userInfo) {
        if (userInfo == null || userInfo.size() != 1)
            return;
        List<Object> info = new ArrayList<>(userInfo.values()).get(0);
        for (int i = 0; i < userDataColumns.length; i++) {
            this.userInfo.put(userDataColumns[i], info.get(i));
        }
        this.userInfo.put("lastLoggedIn", cu.GetCurrentLogInTime());
    }

    public Map<String, Object> GetUserInfo() {
        return this.userInfo;
    }

}

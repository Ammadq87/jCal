import java.util.*;

public class User {
    private String userDataColumns[] = { "name-s", "username-s", "uid-i", "lastLoggedIn-s", "JCal-i" };
    private Map<String, Object> userInfo = new HashMap<>();
    private boolean loggedIn = false;
    CommandUtil cu = new CommandUtil();
    boolean accountCreated = false;

    public User() {

    }

    public boolean getLoggedInStatus() {
        return this.loggedIn;
    }

    public void setLoggedInStatus(boolean status) {
        this.loggedIn = status;
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

    public int getUID() {
        if (this.userInfo == null || this.userInfo.isEmpty())
            return -1;
        return (Integer) this.userInfo.get("uid-i");
    }

    public int getJCalID() {
        if (this.userInfo == null || this.userInfo.isEmpty())
            return -1;
        return (Integer) this.userInfo.get("JCal-i");
    }

}

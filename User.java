
import java.util.*;

public class User {
    private Map<String, Object> userInfo = new HashMap<>();

    public User() {
        for (String key : CommandUtil.getUserDataColumns()) {
            userInfo.put(key, null);
        }
    }

    public void setInfoFromLogin(Map<Integer, List<Object>> userInfo) {
        if (userInfo == null || userInfo.size() != 1)
            return;
        List<Object> info = new ArrayList<>(userInfo.values()).get(0);
        for (int i = 0; i < CommandUtil.getUserDataColumns().length; i++) {
            if (CommandUtil.getUserDataColumns()[i].equals("Password-i")) {
                i--;
                continue;
            }
            this.userInfo.put(CommandUtil.getUserDataColumns()[i], info.get(i));
        }
        this.userInfo.put("LastLoggedIn-t", CommandUtil.getCurrentTimestamp());
    }

    public Map<String, Object> getUserInfo() {
        return this.userInfo;
    }

    public int getUID() {
        if (this.userInfo == null || this.userInfo.isEmpty())
            return -1;
        return (Integer) this.userInfo.get("UserId-i");
    }
}

import java.util.*;

public class Login extends DBAccess {

    private String command;

    public Login(String command) {
        this.command = command;
        Execute();
    }

    public void Execute() {
        String text[] = this.command.split(" ");
        boolean newUser = false;
        for (int i = 0; i < text.length; i++) {
            if (text[i].equals("--new")) {
                CreateNewUser();
                newUser = true;
                break;
            }
        }
        if (!newUser) {
            if (VerfiyLogin())
                msg.Print(
                        msg.GetSuccessMessage("lblLoginSuccessful",
                                (String) super.loggedInUser.GetUserInfo().get("username-s")),
                        's');
            else
                msg.Print(msg.GetErrorMessage("lblLoginFailed", null), 'e');
        } else {
            if (super.loggedInUser.accountCreated)
                msg.Print(msg.GetSuccessMessage("lblAccountCreated", null), 's');
        }
    }

    private void CreateNewUser() {
        Scanner input = new Scanner(System.in);
        String sql = "INSERT INTO users VALUES ({0})";
        String values = "";
        for (String field : super.cu.userDataColumns) {

            if (field.equals("uid-i") || field.equals("JCal-i")) {
                values += "0,";
            } else if (field.equals("lastLoggedIn-s")) {
                values += super.cu.GetCurrentLogInTime() + ",";
            } else {
                String key = field.substring(0, field.indexOf('-'));
                System.out.print("\t" + key + ": ");
                values += "\'" + input.nextLine() + "\',";
            }
        }
        sql = sql.replace("{0}", values.substring(0, values.length() - 1));
        super.ExecuteQuery(sql, 0);
    }

    private boolean VerfiyLogin() {
        String username = super.cu.SanitizeArgument(cu.GetArgument(this.command, "-u"));
        String password = super.cu.SanitizeArgument(cu.GetArgument(this.command, "-p"));
        String sql = "SELECT DISTINCT * FROM users WHERE username = " + username + " AND password = " + password;
        Map<Integer, List<Object>> userInfo = super.FetchResults(sql, "name-s", "username-s", "uid-i", "lastLoggedIn-s",
                "JCal-i");
        if (userInfo == null || userInfo.size() != 1)
            return false;
        super.loggedInUser.SetInfoFromLogin(userInfo);
        sql = "UPDATE users SET lastLoggedIn = \'" + super.cu.GetCurrentLogInTime() + "\' WHERE uid = "
                + super.loggedInUser.getUID();
        super.ExecuteQuery(sql, 0);
        return true;
    }
}

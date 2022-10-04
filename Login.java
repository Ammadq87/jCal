import java.util.*;

public class Login extends Validation {

    private String command;

    public Login(String command) {
        super(command);
    }

    private boolean logout() {
        if (!super.getLoggedInUser().getLoggedInStatus()) {
            return false;
        }
        super.output.Print(
                "> " + super.getLoggedInUser().GetUserInfo().get("username-s") + " has successfully logged out",
                's');
        super.setLoggedInUser(new User());
        return true;
    }

    /**
     * Checks if account exists and whether login was successful. Creates new
     * account if user specifies
     * 
     * ToDo: add option to delete and view account
     * 
     * @param n/a
     * @return if logging in was successful
     */
    public boolean Execute() {
        String text[] = super.getCommand().split(" ");
        boolean newUser = false;
        for (int i = 0; i < text.length; i++) {
            if (text[i].equals("--new")) {
                CreateNewUser();
                newUser = true;
                break;
            } else if (text[i].equals("--view")) {
                return viewAccountInfo();
            } else if (text[i].equals("--logout")) {
                return logout();
            }

        }
        if (!newUser) {
            boolean result = VerfiyLogin();
            if (result)
                output.Print(
                        output.GetSuccessMessage("lblLoginSuccessful",
                                (String) super.loggedInUser.GetUserInfo().get("username-s")),
                        's');
            else
                output.Print(output.GetErrorMessage("lblLoginFailed", null), 'e');
            return result;
        } else {
            if (super.getLoggedInUser().accountCreated) {
                output.Print(output.GetSuccessMessage("lblAccountCreated", null), 's');
                return true;
            }
            return false;
        }
    }

    private boolean viewAccountInfo() {
        Map<String, Object> results = super.loggedInUser.GetUserInfo();
        for (String s : results.keySet()) {
            System.out.println(s + ": " + results.get(s));
        }
        return true;
    }

    private boolean accountAlreadyExists(String username) {
        DBAccess db = new DBAccess();
        String sql = "SELECT username FROM users WHERE username = \'" + username + "\';";
        Map<Integer, List<Object>> results = db.FetchResults(sql, "username-s");
        if (results == null || results.size() == 0)
            return false;
        return true;
    }

    /**
     * Creates a query to create a new account. Asks user for field inputs. Used
     * only if --new tag is added
     * 
     * ToDo: username field is UNIQUE. Add check and verification to prevent an
     * account being created with the same username
     * 
     * @param n/a
     * @return n/a
     */
    private void CreateNewUser() {
        Scanner input = new Scanner(System.in);
        String sql = "INSERT INTO users VALUES ({0})";
        String values = "";
        boolean uniqueAccount = true;
        for (String field : super.getEventColumns()) {

            if (field.equals("uid-i") || field.equals("JCal-i")) {
                values += "0,";
            } else if (field.equals("lastLoggedIn-s")) {
                values += "\"" + super.commandUtil.GetCurrentLogInTime() + "\",";
                System.out.println("values: " + values);
            } else {
                String key = field.substring(0, field.indexOf('-'));
                System.out.print("\t" + key + ": ");
                String name = input.nextLine();
                if (field.equals("username-s") && accountAlreadyExists(name)) {
                    output.Print("Account Already Exists", 'e');
                    uniqueAccount = false;
                    break;
                } else {
                    values += "\"" + name + "\",";
                }
            }
        }
        if (uniqueAccount) {
            DBAccess db = new DBAccess();
            sql = sql.replace("{0}", values.substring(0, values.length() - 1));
            db.ExecuteQuery(sql, 0);
        }
    }

    /**
     * Gets username and password from input and fetches results from DB. Determines
     * if account exists with credentials provided
     * 
     * @return true/false if account exists
     */
    private boolean VerfiyLogin() {
        DBAccess db = new DBAccess();
        String username = super.commandUtil.SanitizeArgument(super.commandUtil.GetArgument(super.getCommand(), "-u"));
        String password = super.commandUtil.SanitizeArgument(super.commandUtil.GetArgument(super.getCommand(), "-p"));
        String sql = "SELECT DISTINCT * FROM users WHERE username = " + username + " AND password = " + password;
        Map<Integer, List<Object>> userInfo = db.FetchResults(sql, "name-s", "username-s", "uid-i", "lastLoggedIn-s",
                "JCal-i");
        if (userInfo == null || userInfo.size() != 1)
            return false;
        super.getLoggedInUser().SetInfoFromLogin(userInfo);
        super.getLoggedInUser().setLoggedInStatus(true);
        sql = "UPDATE users SET lastLoggedIn = \"" + super.commandUtil.GetCurrentLogInTime() + "\" WHERE uid = "
                + super.loggedInUser.getUID();
        db.ExecuteQuery(sql, 0);
        return true;
    }
}

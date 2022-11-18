
import java.util.*;

public class Login extends Validation {

    public Login(String command) {
        super(command);
    }

    /**
     * ToDo [Display Today's Events]:
     * - When User logs in, display today's event
     */

    /**
     * Executes the Add command module. If it's a booking event, it will also book
     * the events
     * 
     * @return - if the excution was possible
     */
    public boolean execute() {
        String text[] = CommandUtil.getCommand().split(" ");
        boolean newUser = false;
        boolean createNewUser = false;
        for (int i = 0; i < text.length; i++) {
            if (text[i].equals("--new")) {
                createNewUser = CreateNewUser();
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
                Messages.printMessage(Messages.getSuccessMessage("lblLoginSuccessful",
                        (String) Validation.currentUser.getUserInfo().get("Username-s")),
                        's');
            else
                Messages.printMessage(Messages.getErrorMessage("lblLoginFailed", null), 'e');
            return result;
        } else {
            if (createNewUser) {
                Messages.printMessage(Messages.getSuccessMessage("lblAccountCreated", null), 's');
            }
            return createNewUser;
        }
    }

    private boolean viewAccountInfo() {
        Map<String, Object> results = super.currentUser.getUserInfo();
        for (String s : results.keySet()) {
            System.out.println(s + ": " + results.get(s));
        }
        return true;
    }

    /**
     * Check database to see if an account already exists with a specific username
     * 
     * @param username - Username to check for
     * @return if account exists
     */
    private boolean accountAlreadyExists(String username) {
        String sql = "SELECT username FROM users WHERE Username = \'" + username + "\';";
        Map<Integer, List<Object>> results = DBAccess.FetchResults(sql, "Username-s");
        if (results == null || results.size() == 0)
            return false;
        return true;
    }

    private boolean logout() {
        if (super.currentUser == null) {
            return false;
        }
        Messages.printMessage(
                Messages.getSuccessMessage("lblLogout", (String) super.currentUser.getUserInfo().get("USername-s")),
                's');
        super.currentUser = null;
        return true;
    }

    /**
     * Creates a query to create a new account. Asks user for field inputs. Used
     * only if --new tag is added
     * 
     * ToDo: username field is UNIQUE. Add check and verification to prevent an
     * account being created with the same username
     */
    private boolean CreateNewUser() {
        Scanner input = new Scanner(System.in);
        String sql = "INSERT INTO Users VALUES ({0})";
        String values = "";
        boolean uniqueAccount = true;
        for (String field : CommandUtil.getUserDataColumns()) {

            if (field.equals("UserId-i")) {
                values += "0,";
            } else if (field.equals("LastLoggedIn-t")) {
                values += "CURRENT_TIMESTAMP,";
            } else {
                String key = field.substring(0, field.indexOf('-'));
                System.out.print("\t" + key + ": ");
                String name = input.nextLine();
                if (field.equals("Username-s") && accountAlreadyExists(name)) {
                    Messages.printMessage(Messages.getErrorMessage("lblAccountExists", null), 'e');
                    uniqueAccount = false;
                    break;
                } else {
                    values += "\"" + name + "\",";
                }
            }
        }
        if (uniqueAccount) {
            sql = sql.replace("{0}", values.substring(0, values.length() - 1));
            int execute = DBAccess.ExecuteQuery(sql, 0);
            if (execute != 0) {
                Messages.printMessage(Messages.getErrorMessage("lblSomethingWentWrong", values), 'e');
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Gets username and password from input and fetches results from DB. Determines
     * if account exists with credentials provided
     * 
     * @return true/false if account exists
     */
    private boolean VerfiyLogin() {
        String username = CommandUtil.sanitizedArgument(CommandUtil.getArgument(CommandUtil.getCommand(), "-u"));
        String password = CommandUtil.sanitizedArgument(CommandUtil.getArgument(CommandUtil.getCommand(), "-p"));
        String sql = "SELECT DISTINCT * FROM Users WHERE Username = " + username + " AND Password = " + password;
        Map<Integer, List<Object>> userInfo = DBAccess.FetchResults(sql, "Name-s", "Username-s", "Password-i",
                "UserId-i",
                "LastLoggedIn-s");
        if (userInfo == null || userInfo.size() != 1)
            return false;
        Validation.currentUser = new User();
        Validation.currentUser.setInfoFromLogin(userInfo);
        sql = "UPDATE Users SET LastLoggedIn = CURRENT_TIMESTAMP WHERE UserId = "
                + Validation.currentUser.getUID();
        DBAccess.ExecuteQuery(sql, 0);
        return true;
    }
}

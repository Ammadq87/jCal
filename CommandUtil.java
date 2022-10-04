import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommandUtil {
    private String flags[] = { "-d", "-t", "-s", "-pr", "-delete", "-edit", "-new", "-u", "-p", "-n" };
    private String commands[] = { "add", "find", "ls", "login" };
    String userDataColumns[] = { "name-s", "username-s", "password-s", "uid-i", "lastLoggedIn-s", "JCal-i" };
    String eventColumns[] = { "name-s", "date-s", "startTime-i", "endTime-i", "status-i", "priority-i", "eventID-i",
            "jCal-i" };
    Messages msg = new Messages();

    /**
     * Cleans up command by removing extra spaces
     * 
     * @param command input command from user
     * @return proper formatted command w/o extra spaces
     */
    public String SetCommand(String command) {
        if (isStringNullOrEmpty(command))
            return null;
        String text[] = command.split(" ");
        String output = "";
        for (int i = 0; i < text.length; i++) {
            if (!(text[i].equals(" ") || text[i].equals(""))) {
                output += (i == text.length - 1) ? text[i] : text[i] + " ";
            }
        }
        return output;
    }

    /**
     * Supposed to remove quotes from input command
     * ! Not yet completed
     * 
     * @param command argument needed to be sanitized
     * @returns sanitized command
     */
    public String SanitizeArgument(String command) {
        if (isStringNullOrEmpty(command))
            return null;
        return command.substring(0, command.length());
    }

    public String GetArgument(String command, String flag) {
        String text[] = command.split(" ");
        if (text == null || text.length == 0)
            return null;
        String argument = "";
        for (int i = 0; i < text.length; i++) {
            if (text[i].equals(flag)) {
                for (int j = i + 1; j < text.length; j++) {
                    if (text[j].charAt(0) == '\'' && text[j].charAt(text[j].length() - 1) == '\'')
                        return text[j];
                    else if (text[j].charAt(0) == '\'')
                        argument += text[j] + " ";
                    else if (text[j].charAt(0) != '\'' && text[j].charAt(text[j].length() - 1) != '\'')
                        argument += text[j] + " ";
                    else if (text[j].charAt(text[j].length() - 1) == '\'') {
                        argument += text[j];
                        return argument;
                    }
                }
            }
        }

        return null;
    }

    public boolean isStringNullOrEmpty(String s) {
        return (s == null || s.isBlank() || s.isEmpty() || s.equals(" ") || s.equals(""));
    }

    public String GetCurrentLogInTime() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return myDateObj.format(myFormatObj);
    }

    // public void Prompt() {
    // Map<String, List<Object>> eventInfo = new HashMap<>();
    // String eventName = "";
    // List<Object> values = new ArrayList<>();
    // boolean replaceCurrentEvent = false;
    // for (String key : this.eventColumns) {
    // char type = key.charAt(key.length() - 1);
    // String columnName = key.substring(0, key.indexOf('-'));
    // Scanner input = new Scanner(System.in);
    // if (key.equals("eventID-i") || key.equals("jCal-i"))
    // continue;
    // if (key.equals("status-i"))
    // columnName += " [1:Declined, 2:Attending, 3:Tentative, 4:Busy]";
    // if (key.equals("priority-i"))
    // columnName += " [1:Low, 2:Med, 3:High, 4:Very High]";
    // System.out.print("\t" + columnName + ": ");
    // Validation valid = new Validation(null);
    // if (type == 'i') {
    // boolean mismatch = false;
    // while (!mismatch) {
    // try {
    // Integer value = input.nextInt();
    // input.nextLine();
    // if (key.equals("endTime-i")) {
    // while (!valid.validateTime((int) values.get(values.size() - 1), value)) {
    // msg.Print(msg.GetErrorMessage("lblInvalidTime", null), 'e');
    // System.out.print(" endTime: ");
    // value = input.nextInt();
    // input.nextLine();
    // }
    // }
    // values.add(value);
    // mismatch = true;
    // } catch (InputMismatchException e) {
    // msg.Print(msg.GetErrorMessage("lblNonNumerical", null), 'e');
    // System.out.print("\t" + columnName + ": ");
    // input.nextLine();
    // }
    // }
    // } else {
    // String value = input.nextLine();
    // if (key.equals("name-s")) {
    // if (eventAlreadyExists(value)) {
    // String newValue = value;
    // while (newValue.equals(value)) {
    // msg.Print("> \'" + value
    // + "\' already exists. Would you like to replace the pre-existing event with
    // this information? [Y/N]",
    // 'o');
    // System.out.print("> ");
    // newValue = input.nextLine();
    // if (isStringNullOrEmpty(newValue) || newValue.charAt(0) == 'n') {
    // msg.Print("> Ok. Please choose a different name.", 'o');
    // System.out.print("\tname: ");
    // newValue = input.nextLine();
    // } else {
    // replaceCurrentEvent = true;
    // break;
    // }
    // }
    // }
    // eventName = value;
    // } else if (key.equals("date-s")) {
    // while (value == null || isStringNullOrEmpty(value) ||
    // !valid.validateDateFormat(value)) {
    // msg.Print("> Valid Date Not Provided. Retry? [Y/N]", 'o');
    // System.out.print("> ");
    // String option = input.nextLine();
    // if (isStringNullOrEmpty(option) || option.charAt(0) == 'n'
    // || option.charAt(0) == 'N') {
    // value = GetCurrentLogInTime();
    // msg.Print("> Date replaced with Today's Date", 'o');
    // break;
    // } else {
    // msg.Print("> Enter a valid date (mm-dd-yyyy):", 'o');
    // System.out.print("\tdate: ");
    // value = input.nextLine();
    // }
    // }
    // }
    // values.add(value);
    // }
    // }
    // eventInfo.put(eventName, values);
    // insertEvent(values, replaceCurrentEvent);
    // }

    // private boolean eventAlreadyExists(String eventName) {
    // String sql = "SELECT * FROM events WHERE name = \"" + eventName + "\";"; //
    // Select COUNT(name)
    // DBAccess db = new DBAccess();
    // Map<Integer, List<Object>> results = db.FetchResults(sql, this.eventColumns);
    // if (results.size() > 0)
    // return true;
    // return false;
    // }

    // private void insertEvent(List<Object> values, boolean replaceCurrentEvent) {
    // String sql = "SELECT eventID FROM events WHERE name = \"" + values.get(0) +
    // "\";";
    // if (replaceCurrentEvent) {
    // Map<Integer, List<Object>> results = FetchResults(sql, "eventID-i");
    // int eventID = -1;
    // for (Object obj : results.keySet())
    // eventID = (Integer) obj;
    // sql = "UPDATE events SET ";
    // String edits = "";
    // for (int i = 1; i < super.cu.eventColumns.length - 1; i++) {
    // String eCol = super.cu.eventColumns[i];
    // char type = eCol.charAt(eCol.length() - 1);
    // String column = eCol.substring(0, eCol.indexOf('-'));
    // if (type == 'i')
    // edits += column + " = " + values.get(i) + ",";
    // else
    // edits += column + " = \"" + values.get(i) + "\",";
    // }
    // edits = edits.substring(0, edits.length() - 1);
    // sql += edits + " WHERE eventID = " + eventID;
    // super.ExecuteQuery(sql, 0);
    // } else {
    // sql = "INSERT INTO events VALUES (";
    // for (Object obj : values) {
    // if (obj instanceof String)
    // sql += "\"" + obj + "\",";
    // else
    // sql += obj + ",";
    // }

    // sql += "0," + this.jCalID + ");";
    // super.ExecuteQuery(sql, 0);
    // }
    // }
}
import java.util.*;

public class Add extends Validation {

    public Add(String command) {
        super(command);
    }

    public boolean execute() {
        CreatePrompt();
        return true;
    }

    /**
     * Checks to see if an event with that name already exists. Events should have
     * unique names
     * 
     * @param eventName name of event to check
     * @return true/false if the event with that name exists
     */
    private boolean eventAlreadyExists(String eventName) {
        DBAccess db = new DBAccess();
        String sql = "SELECT * FROM events WHERE name = \"" + eventName + "\";"; // Select COUNT(name)
        Map<Integer, List<Object>> results = db.FetchResults(sql, super.getEventColumns());
        if (results.size() > 0)
            return true;
        return false;
    }

    /**
     * Creates a prompt for the user to input details about the event. Warns user if
     * invalid input is entered.
     * 
     * ToDo: Add verification separate verification for each time (case where
     * startTime is'nt divisble by 15 will result in loop)
     * ToDo: Replace sysout lines with error/success/output messages
     * 
     * @param n/a
     * 
     * @return n/a
     * 
     */
    public void CreatePrompt() {
        Map<String, List<Object>> eventInfo = new HashMap<>();
        String eventName = "";
        List<Object> values = new ArrayList<>();
        boolean eventExists = false;
        for (String key : super.getEventColumns()) {
            char type = key.charAt(key.length() - 1);
            String columnName = key.substring(0, key.indexOf('-'));
            Scanner input = new Scanner(System.in);
            if (key.equals("eventID-i") || key.equals("jCal-i"))
                continue;
            if (key.equals("status-i"))
                columnName += " [1:Declined, 2:Attending, 3:Tentative, 4:Busy]";
            if (key.equals("priority-i"))
                columnName += " [1:Low, 2:Med, 3:High, 4:Very High]";
            System.out.print("\t" + columnName + ": ");
            Validation valid = new Validation(null);
            if (type == 'i') {
                boolean mismatch = false;
                while (!mismatch) {
                    try {
                        Integer value = input.nextInt();
                        input.nextLine();
                        if (key.equals("endTime-i")) {
                            while (!valid.isTimeValid((int) values.get(values.size() - 1), value)) {
                                output.Print(output.GetErrorMessage("lblInvalidTime", null), 'e');
                                System.out.print("  endTime: ");
                                value = input.nextInt();
                                input.nextLine();
                            }
                        }
                        values.add(value);
                        mismatch = true;
                    } catch (InputMismatchException e) {
                        super.output.Print(output.GetErrorMessage("lblNonNumerical", null), 'e');
                        System.out.print("\t" + columnName + ": ");
                        input.nextLine();
                    }
                }
            } else {
                String value = input.nextLine();
                if (key.equals("name-s")) {
                    if (eventAlreadyExists(value)) {
                        super.output.Print("> Event already exists. Use <event find -n \'" + value + "\' --edit>",
                                type);
                        eventExists = true;
                        break;
                    }
                    eventName = value;
                } else if (key.equals("date-s")) {
                    while (value == null || super.commandUtil.isStringNullOrEmpty(value) || !valid.isDateValid(value)) {
                        output.Print("> Valid Date Not Provided. Retry? [Y/N]", 'o');
                        System.out.print("> ");
                        String option = input.nextLine();
                        if (super.commandUtil.isStringNullOrEmpty(option) || option.charAt(0) == 'n'
                                || option.charAt(0) == 'N') {
                            value = super.commandUtil.GetCurrentLogInTime();
                            output.Print("> Date replaced with Today's Date", 'o');
                            break;
                        } else {
                            output.Print("> Enter a valid date (mm-dd-yyyy):", 'o');
                            System.out.print("\tdate: ");
                            value = input.nextLine();
                        }
                    }
                }
                values.add(value);
            }
        }
        eventInfo.put(eventName, values);
        if (!eventExists)
            insertEvent(values);
    }

    /**
     * Uses the inputs of the fields from the user to create a sql query. If the
     * user chose to replace an existing event, the event will be replaced with new
     * values
     * 
     * ToDo: Add proper crud function to execute method
     * 
     * @param values              event attributes
     * @param replaceCurrentEvent flag that determines to replace or insert event
     * @return n/a
     */
    private void insertEvent(List<Object> values) {
        DBAccess db = new DBAccess();
        String sql = "SELECT eventID FROM events WHERE name = \"" + values.get(0) + "\";";
        sql = "INSERT INTO events VALUES (";
        for (Object obj : values) {
            if (obj instanceof String)
                sql += "\"" + obj + "\",";
            else
                sql += obj + ",";
        }
        int jCalId = super.getLoggedInUser().getJCalID();
        sql += "0," + jCalId + ");";
        db.ExecuteQuery(sql, 0);
    }
}

import java.util.*;

public class Add extends DBAccess {
    private int jCalID;

    /**
     * Returns if adding an event was a successful operation. Checks for if user is
     * logged in to execute
     * 
     * @return if method was successful
     */
    public boolean execute() {
        this.jCalID = super.loggedInUser.getJCalID();
        if (jCalID == -1) {
            super.msg.Print(super.msg.GetErrorMessage("lblNotLoggedIn", null), 'e');
            return false;
        }
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
        String sql = "SELECT * FROM events WHERE name = \"" + eventName + "\";"; // Select COUNT(name)
        Map<Integer, List<Object>> results = super.FetchResults(sql, super.cu.eventColumns);
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
    private void CreatePrompt() {
        Map<String, List<Object>> eventInfo = new HashMap<>();
        String eventName = "";
        List<Object> values = new ArrayList<>();
        boolean replaceCurrentEvent = false;
        for (String key : super.cu.eventColumns) {
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
                            while (!valid.validateTime((int) values.get(values.size() - 1), value)) {
                                msg.Print(msg.GetErrorMessage("lblInvalidTime", null), 'e');
                                System.out.print("  endTime: ");
                                value = input.nextInt();
                                input.nextLine();
                            }
                        }
                        values.add(value);
                        mismatch = true;
                    } catch (InputMismatchException e) {
                        super.msg.Print(msg.GetErrorMessage("lblNonNumerical", null), 'e');
                        System.out.print("\t" + columnName + ": ");
                        input.nextLine();
                    }
                }
            } else {
                String value = input.nextLine();
                if (key.equals("name-s")) {
                    if (eventAlreadyExists(value)) {
                        String newValue = value;
                        while (newValue.equals(value)) {
                            msg.Print("> \'" + value
                                    + "\' already exists. Would you like to replace the pre-existing event with this information? [Y/N]",
                                    'o');
                            System.out.print("> ");
                            newValue = input.nextLine();
                            if (super.cu.isStringNullOrEmpty(newValue) || newValue.charAt(0) == 'n') {
                                msg.Print("> Ok. Please choose a different name.", 'o');
                                System.out.print("\tname: ");
                                newValue = input.nextLine();
                            } else {
                                replaceCurrentEvent = true;
                                break;
                            }
                        }
                    }
                    eventName = value;
                } else if (key.equals("date-s")) {
                    while (value == null || super.cu.isStringNullOrEmpty(value) || !valid.validateDateFormat(value)) {
                        msg.Print("> Valid Date Not Provided. Retry? [Y/N]", 'o');
                        System.out.print("> ");
                        String option = input.nextLine();
                        if (super.cu.isStringNullOrEmpty(option) || option.charAt(0) == 'n'
                                || option.charAt(0) == 'N') {
                            value = super.cu.GetCurrentLogInTime();
                            msg.Print("> Date replaced with Today's Date", 'o');
                            break;
                        } else {
                            msg.Print("> Enter a valid date (mm-dd-yyyy):", 'o');
                            System.out.print("\tdate: ");
                            value = input.nextLine();
                        }
                    }
                }
                values.add(value);
            }
        }
        eventInfo.put(eventName, values);
        insertEvent(values, replaceCurrentEvent);
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
    private void insertEvent(List<Object> values, boolean replaceCurrentEvent) {
        String sql = "SELECT eventID FROM events WHERE name = \"" + values.get(0) + "\";";
        if (replaceCurrentEvent) {
            Map<Integer, List<Object>> results = FetchResults(sql, "eventID-i");
            int eventID = -1;
            for (Object obj : results.keySet())
                eventID = (Integer) obj;
            sql = "UPDATE events SET ";
            String edits = "";
            for (int i = 1; i < super.cu.eventColumns.length - 1; i++) {
                String eCol = super.cu.eventColumns[i];
                char type = eCol.charAt(eCol.length() - 1);
                String column = eCol.substring(0, eCol.indexOf('-'));
                if (type == 'i')
                    edits += column + " = " + values.get(i) + ",";
                else
                    edits += column + " = \"" + values.get(i) + "\",";
            }
            edits = edits.substring(0, edits.length() - 1);
            sql += edits + " WHERE eventID = " + eventID;
            super.ExecuteQuery(sql, 0);
        } else {
            sql = "INSERT INTO events VALUES (";
            for (Object obj : values) {
                if (obj instanceof String)
                    sql += "\"" + obj + "\",";
                else
                    sql += obj + ",";
            }

            sql += "0," + this.jCalID + ");";
            super.ExecuteQuery(sql, 0);
        }
    }
}

import java.rmi.server.ObjID;
import java.util.*;

public class Add extends DBAccess {
    private int jCalID;

    public Add() {
    }

    public boolean execute() {
        this.jCalID = super.loggedInUser.getJCalID();
        if (jCalID == -1) {
            super.msg.Print(super.msg.GetErrorMessage("lblNotLoggedIn", null), 'e');
            return false;
        }
        CreatePrompt();
        return true;

    }

    /*
     * Creates a prompt for the user to input details about the event. Warns user if
     * invalid input is entered.
     * 
     * ToDo: validate fields like date (mm-dd-yyyy) and time (start <= end)
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

        for (String key : super.cu.eventColumns) {
            char type = key.charAt(key.length() - 1);
            String columnName = key.substring(0, key.indexOf('-'));
            Scanner input = new Scanner(System.in);

            if (key.equals("eventID-i"))
                continue;

            System.out.print("\t" + columnName + ": ");

            Validation valid = new Validation(null);
            if (type == 'i') {
                boolean mismatch = false;
                while (!mismatch) {
                    try {
                        // Verify start <= time and priority and status are 1 <= x <= 3
                        Integer value = input.nextInt();
                        input.nextLine();

                        if (key.equals("endTime-i")) {
                            while (!valid.validateTime((int) values.get(values.size() - 1), value)) {
                                msg.Print("> Invalid Time. End Time should be greater than Start Time.", 'e');
                                System.out.print("  endTime: ");
                                value = input.nextInt();
                                input.nextLine();
                            }
                        }

                        values.add(value);
                        mismatch = true;
                    } catch (InputMismatchException e) {
                        super.msg.Print(msg.GetErrorMessage("lblNonNumerical", null), 'e');
                        System.out.print("  " + columnName + ": ");
                        input.nextLine();
                    }
                }
            } else {
                String value = input.nextLine();
                if (key.equals("name-s"))
                    eventName = value;
                else if (key.equals("date-s")) {
                    while (value == null || super.cu.isStringNullOrEmpty(value) || !valid.validateDateFormat(value)) {
                        msg.Print("> Valid Date Not Provided. Retry? [Y/N]", 'o');
                        System.out.print("  > ");
                        String option = input.nextLine();
                        if (super.cu.isStringNullOrEmpty(option) || option.charAt(0) == 'n'
                                || option.charAt(0) == 'N') {
                            value = super.cu.GetCurrentLogInTime();
                            msg.Print("> Date replaced with Today's Date", 'o');
                            break;
                        } else {
                            msg.Print("> Enter a valid date (mm-dd-yyyy):", 'o');
                            System.out.print("  date: ");
                            value = input.nextLine();
                        }
                    }
                }
                values.add(value);
            }
        }

        eventInfo.put(eventName, values);

        insertEvent(values);
    }

    /*
     * Uses the inputs of the fields from the user to create a sql query. Executes
     * the query.
     * 
     * @param values - List of values of different types
     * 
     * @return n/a
     */
    private void insertEvent(List<Object> values) {
        String sql = "INSERT INTO events VALUES (";
        for (Object obj : values) {
            if (obj instanceof String)
                sql += "\'" + obj + "\',";
            else
                sql += obj + ",";
        }

        sql += "0," + this.jCalID + ");";
        super.ExecuteQuery(sql, 0);
    }

}

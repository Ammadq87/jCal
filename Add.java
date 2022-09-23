import java.rmi.server.ObjID;
import java.util.*;

public class Add extends DBAccess {
    public Add() {
        CreatePrompt();
    }

    public boolean execute() {
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

            System.out.print("  " + columnName + ": ");

            if (type == 'i') {
                boolean mismatch = false;
                while (!mismatch) {
                    try {
                        Integer value = input.nextInt();
                        input.nextLine();
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
        int jCalID = super.loggedInUser.getJCalID();
        sql += "0," + jCalID + ");";
        super.ExecuteQuery(sql, 0);
    }

}

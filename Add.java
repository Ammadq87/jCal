
import java.util.*;

public class Add extends Validation {

    public static boolean replaceCurrentEvent = false;
    public static String newName = "";
    public static String eventName = "";
    public static boolean bookingEvent = false;
    public static boolean findEvent = false;
    public static List<Object> oldValues = new ArrayList<>();

    public Add(String command) {
        super(command);
    }

    /**
     * Executes the Add command module. If it's a booking event, it will also book
     * the events
     * 
     * @return - if the excution was possible
     */
    public boolean execute() {
        if (CommandUtil.getCommandType().equals("book"))
            bookingEvent = true;
        return addEvent();
    }

    // ToDo: Booking Feature ↓
    private static boolean bookEvent() {
        return true;
    }

    private static boolean addEventsForOtherUsers(String users, List<Object> values) {
        String userList[] = users.split(" ");
        for (int i = 0; i < userList.length; i++)
            userList[i] = "\"" + userList[i].substring(1, userList[i].length()) + "\"";
        String sql = "SELECT Username, UserId FROM Users WHERE Username IN (" + String.join(", ", userList) + ");";
        System.out.println("\n" + sql);
        Map<Integer, List<Object>> hosts = DBAccess.FetchResults(sql, "Username-s", "UserId-i");
        sql = "";
        int cnt = 0;
        for (Integer hostId : hosts.keySet()) {
            String query = insertEventIntoDbQuery(values, hostId);
            if (cnt != 0) {
                sql += ", " + query.substring(25, query.length());
            } else {
                sql += query.substring(0, query.length() - 1);
            }
            cnt++;
        }
        int insert = DBAccess.ExecuteQuery(sql, 1000);

        int insertIntoBookings;
        if (insert != 1000)
            Messages.printMessage("Insert Operation did not occur", 'e');
        return insert == 1000;
    }

    private static String insertIntoBookings(Map<Integer, List<Object>> hosts) {
        String sql = "SELECT EventId FROM Events WHERE Name = \"" + eventName
                + "\" AND IsEventForBooking = 1 AND UserId != " + Validation.currentUser.getUID();
        Map<Integer, List<Object>> eventIds = DBAccess.FetchResults(sql, "EventId-i");
        if (eventIds.size() != hosts.size()) {
            // Something went wrong
        }
        Set<Integer> hostsIds = hosts.keySet();
        for (Integer hostId : hosts.keySet()) {
            // sql += "(\"" + eventName + "\", " + hostId + ", " +
            // super.currentUser.getUID() + ",0, "++")";
        }

        return sql;
    }

    // ToDo: Booking Feature ↑

    /**
     * Prompts user to enter field values for adding event to their calendar.
     * Initiates booking module if booking command is entered
     * 
     * @return - A mapping between Event name and Event details
     */
    public static Map<String, List<Object>> eventPrompt() {
        Map<String, List<Object>> eventInfo = new HashMap<>();
        List<Object> values = new ArrayList<>();
        Scanner input = new Scanner(System.in);
        int index = 0;
        for (String key : CommandUtil.getEventColumns()) {
            char type = key.charAt(key.length() - 1);
            String columnName = key.substring(0, key.indexOf('-'));

            if (key.equals("EventId-i") || key.equals("UserId-i") || key.equals("IsEventForBooking-i"))
                continue;
            if (key.equals("Status-i"))
                columnName += " [1:Declined, 2:Attending, 3:Tentative, 4:Busy]";
            if (key.equals("Priority-i"))
                columnName += " [1:Low, 2:Med, 3:High, 4:Very High]";
            System.out.print("\t" + columnName + (findEvent ? " (Prev: " + oldValues.get(index) + "): " : ": "));
            Validation valid = new Validation(null);
            if (type == 'i') {
                boolean mismatch = false;
                while (!mismatch) {
                    try {
                        Integer value = input.nextInt();
                        input.nextLine();
                        if (key.equals("EndTime-i")) {
                            while (!valid.isTimeValid((int) values.get(values.size() - 1), value)) {
                                Messages.printMessage(Messages.getErrorMessage("lblInvalidTime", null), 'e');
                                System.out.print("  endTime: ");
                                value = input.nextInt();
                                input.nextLine();
                            }
                        }
                        values.add(value);
                        mismatch = true;
                    } catch (InputMismatchException e) {
                        Messages.printMessage(Messages.getErrorMessage("lblNonNumerical", null), 'e');
                        System.out.print("\t" + columnName + ": ");
                        input.nextLine();
                    }
                }
            } else {
                String value = input.nextLine();
                if (key.equals("Name-s")) {
                    if (Validation.doesEventAlreadyExist(value)) {
                        String newValue = value;
                        while (newValue.equals(value)) {
                            Messages.printMessage("> \'" + value
                                    + "\' already exists. Would you like to replace the pre-existing event with this information? [Y/N]",
                                    'o');
                            System.out.print("> ");
                            newValue = input.nextLine();
                            if (CommandUtil.isNullOrEmpty(newValue) || newValue.charAt(0) == 'n') {
                                Messages.printMessage("> Please choose a different name.", 'o');
                                System.out.print("\tname: ");
                                newValue = input.nextLine();
                            } else {
                                replaceCurrentEvent = true;
                                newName = value;
                                break;
                            }
                        }
                    }
                    eventName = value;
                } else if (key.equals("Date-s")) {
                    while (value == null || CommandUtil.isNullOrEmpty(value) || !valid.isDateValid(value)) {
                        Messages.printMessage("> Valid Date Not Provided. Retry? [Y/N]", 'o');
                        System.out.print("> ");
                        String option = input.nextLine();
                        if (CommandUtil.isNullOrEmpty(option) || option.charAt(0) == 'n'
                                || option.charAt(0) == 'N') {
                            value = CommandUtil.getCurrentDate();
                            Messages.printMessage("> Date replaced with Today's Date", 'o');
                            break;
                        } else {
                            Messages.printMessage("> Enter a valid date (mm-dd-yyyy):", 'o');
                            System.out.print("\tdate: ");
                            value = input.nextLine();
                        }
                    }
                }
                values.add(value);
            }
            index++; // for FindEvent class
        }

        if (bookingEvent) {
            System.out.print("\tHost(s): ");
            /*
             * Example Input: @ammadq87 @umerq21
             */
            String hosts = input.nextLine();
            boolean x = addEventsForOtherUsers(hosts, values);
        }

        eventInfo.put(eventName, values);
        return eventInfo;
    }

    /**
     * Runs a Create or Update query
     * 
     * @return - if execution was successful
     */
    private static boolean addEvent() {
        Map<String, List<Object>> event = eventPrompt();
        if (replaceCurrentEvent) {
            return updateEvent(event.get(eventName), Validation.currentUser, eventName);
        }
        return insertEventIntoDB(event.get(eventName));
    }

    /**
     * Returns an insert query given the values to be inserted
     * 
     * @param values - List of values to be inserted
     * @return SQL Query
     */
    private static String insertEventIntoDbQuery(List<Object> values, int UserId) {
        String sql = "INSERT INTO Events VALUES(";
        for (Object v : values) {
            if (v instanceof String)
                sql += "\"" + v + "\",";
            else
                sql += v + ",";
        }
        sql += "0," + (bookingEvent ? 1 : 0) + "," + UserId + ");";
        return sql;
    }

    /**
     * Runs Create query
     * 
     * @param values - list of values to be added to event
     * @return - if the excution was possible
     */
    private static boolean insertEventIntoDB(List<Object> values) {
        String sql = insertEventIntoDbQuery(values, Validation.currentUser.getUID());
        int insert = DBAccess.ExecuteQuery(sql, 1000);
        if (insert == 1000)
            Messages.printMessage(Messages.getSuccessMessage("lblCreate", sql), 's');
        return insert == 1000;
    }

    /**
     * Runs an Update query to update an event
     * 
     * @param values      - list of new values of the new event
     * @param user        - the User
     * @param nameForEdit - name of old event that will be updated
     * @return - if the excution was possible
     */
    public static boolean updateEvent(List<Object> values, User user, String nameForEdit) {
        String tempSql = "SELECT EventId FROM Events WHERE Name =\"" + nameForEdit + "\";";
        Map<Integer, List<Object>> results = DBAccess.FetchResults(tempSql, "EventId-i");
        int eventID = -1;
        for (Object obj : results.keySet())
            eventID = (Integer) obj;

        String sql = "UPDATE Events SET ";
        String edits = "";

        int index = 0;
        for (String field : CommandUtil.getEventColumns()) {
            char type = field.charAt(field.length() - 1);
            String key = field.substring(0, field.indexOf('-'));
            if (field.equals("EventId-i") || field.equals("UserId-i") || field.equals("IsEventForBooking-i"))
                continue;
            else if (type == 'i')
                edits += key + " = " + values.get(index) + ",";
            else
                edits += key + " = \"" + values.get(index) + "\",";
            index++;
        }
        edits = edits.substring(0, edits.length() - 1);
        sql += edits + " WHERE EventId = " + eventID;
        int update = DBAccess.ExecuteQuery(sql, 0010);
        if (update == 0010)
            Messages.printMessage(Messages.getSuccessMessage("lblUpdate", null), 's');
        return update == 0010;
    }

}

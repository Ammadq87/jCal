
import java.util.*;

public class Validation {
    public static User currentUser = null;

    public Validation(String command) {
        CommandUtil.setCommand(command);
    }

    /**
     * Validates time interval
     * 
     * @param start - starting time of event
     * @param end   - ending time of event
     * @return if start and end times are valid intervals and in 15min increments
     */
    public boolean isTimeValid(int start, int end) {
        // end = -1 --> validate only start
        if (end == -1)
            return (0 <= start && start <= 2400 && (start % 100) % 15 == 0);
        return (start >= 0 && end >= 0) && (start <= 2400 && end <= 2400) && (start < end) &&
                (start % 100) % 15 == 0 && (end % 100) % 15 == 0;
    }

    /**
     * Verifies if date of event is in mm-dd-yyyy format and that date values
     * provided are within scope
     * 
     * @param date - event date taken from user input
     * @return if date is valid within scope
     */
    public boolean isDateValid(String date) {
        if (date == null || date.isEmpty() || date.isBlank())
            return false;
        int months[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        int days[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        String dateValues[] = date.split("-");
        try {
            if (Integer.parseInt(dateValues[0]) > months.length)
                return false;
            else if (Integer.parseInt(dateValues[0]) != months[Integer.parseInt(dateValues[0]) - 1])
                return false;
            else if (Integer.parseInt(dateValues[1]) > days[Integer.parseInt(dateValues[0]) - 1])
                return false;
            else if (Integer.parseInt(dateValues[2]) < 2022)
                return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public boolean RunValidation() {
        String commandType = CommandUtil.getCommandType();
        if (this.currentUser == null && !commandType.equals("login")) {
            Messages.printMessage(Messages.getErrorMessage("lblNotLoggedIn", null), 'e');
            return false;
        }
        if (commandType == null)
            return false;
        boolean found = false;
        for (String s : CommandUtil.getCommandTypes()) {
            if (s.equals(commandType)) {
                found = true;
                break;
            }
        }
        if (found) {
            CommandUtil.setCommandType(commandType);
            return ValidateCommand();
        }
        return found;
    }

    /**
     * Checks to see if an event with that name already exists. Events should have
     * unique names
     * 
     * @param eventName name of event to check
     * @return true/false if the event with that name exists
     */
    public static boolean doesEventAlreadyExist(String eventName) {
        String sql = "SELECT * FROM Events WHERE Name = \"" + eventName + "\";"; // Select COUNT(name)
        Map<Integer, List<Object>> results = DBAccess.FetchResults(sql, CommandUtil.getEventColumns());
        return results.size() > 0;
    }

    private boolean ValidateCommand() {
        if (CommandUtil.isNullOrEmpty(CommandUtil.getCommand()))
            return false;

        String text[] = CommandUtil.getCommand().split(" ");
        if (text.length <= 1)
            return false;
        if (!text[0].equals("event"))
            return false;

        switch (CommandUtil.getCommandType()) {

            case "add":
                Add obj = new Add(CommandUtil.getCommand());
                return obj.execute();
            case "book":
                Add book = new Add(CommandUtil.getCommand());
                return book.execute();
            /*
             * case "book":
             * Add obj1 = new Add(CommandUtil.getCommand(), true);
             * return obj1.execute();
             */
            case "find":
                Find find = new Find(CommandUtil.getCommand());
                return find.execute();
            case "login":
                Login login = new Login(CommandUtil.getCommand());
                return login.execute();
            case "ls":
        }

        // Regex validation
        // Based on commandType, run regex. It will be long process

        return true;
    }
}

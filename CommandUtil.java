
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommandUtil {
    private static String command;

    public static void setCommand(String c) {
        command = c;
    }

    public static String getCommand() {
        return command;
    }

    public static String getCommandType() {
        if (CommandUtil.isNullOrEmpty(CommandUtil.getCommand()))
            return null;

        String text[] = CommandUtil.getCommand().split(" ");
        if (text.length <= 1)
            return null;
        return text[1];
    }

    public static void setCommandType(String commandType) {
        CommandUtil.commandType = commandType;
    }

    public static String[] getCommandTypes() {
        return commandTypes;
    }

    public static void setCommandTypes(String[] commandTypes) {
        CommandUtil.commandTypes = commandTypes;
    }

    private static String commandType;
    private static String flags[] = { "-d", "-t", "-s", "-pr", "-delete", "-edit", "-new", "-u", "-p", "-n", "--all",
            "--auto" };
    private static String commandTypes[] = { "add", "book", "find", "ls", "login" };
    private static String userDataColumns[] = { "Name-s", "Username-s", "Password-s", "UserId-i", "LastLoggedIn-t" };
    private static String eventColumns[] = { "Name-s", "Date-s", "StartTime-i", "EndTime-i", "Status-i", "Priority-i",
            "EventId-i", "IsEventForBooking-i" };
    private static String tables[][] = { { "Users", "UserId" }, { "Events", "EventId" }, { "Bookings", "BookingId" } };

    public static String[][] getTables() {
        return tables;
    }

    public static void setTables(String[][] tables) {
        CommandUtil.tables = tables;
    }

    public static String[] getFlags() {
        return flags;
    }

    public static void setFlags(String[] flags) {
        CommandUtil.flags = flags;
    }

    public static String[] getCommands() {
        return commandTypes;
    }

    public static void setCommands(String[] commands) {
        CommandUtil.commandTypes = commands;
    }

    public static String[] getUserDataColumns() {
        return userDataColumns;
    }

    public static void setUserDataColumns(String[] userDataColumns) {
        CommandUtil.userDataColumns = userDataColumns;
    }

    public static String[] getEventColumns() {
        return eventColumns;
    }

    public static void setEventColumns(String[] eventColumns) {
        CommandUtil.eventColumns = eventColumns;
    }

    public CommandUtil() {
    }

    /**
     * Cleans up command by removing extra spaces
     * 
     * @param command input command from user
     * @return proper formatted command w/o extra spaces
     */
    public static String setFullCommand(String command) {
        if (isNullOrEmpty(command))
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
     * Determines whether input string is null or empty
     * 
     * @param s - input String
     * @return if string is null or empty
     */
    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.isBlank() || s.isEmpty() || s.equals(" ") || s.equals(""));
    }

    /**
     * Gets the argument from the required flag
     * 
     * @param command - input command from user
     * @param flag    - argument to be taken from flag
     * @return returns the argument
     */
    public static String getArgument(String command, String flag) {
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

    /**
     * Removes quotes from input command
     * 
     * @param command argument needed to be sanitized
     * @returns sanitized command
     */
    public static String sanitizedArgument(String command) {
        if (isNullOrEmpty(command))
            return null;
        return command.substring(0, command.length());
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    public static String getCurrentDate() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return myDateObj.format(myFormatObj);
    }

}
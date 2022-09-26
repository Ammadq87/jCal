import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandUtil {
    private String flags[] = { "-d", "-t", "-s", "-pr", "-delete", "-edit", "-new", "-u", "-p", "-n" };
    private String commands[] = { "add", "find", "ls", "login" };
    String userDataColumns[] = { "name-s", "username-s", "password-s", "uid-i", "lastLoggedIn-s", "JCal-i" };
    String eventColumns[] = { "name-s", "date-s", "startTime-i", "endTime-i", "status-i", "priority-i", "eventID-i",
            "jCal-i" };

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
}
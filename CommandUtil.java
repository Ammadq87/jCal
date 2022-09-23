import java.util.regex.*;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter;

public class CommandUtil {
    private String flags[] = { "-d", "-t", "-s", "-pr", "-delete", "-edit", "-new", "-u", "-p" };
    private String commands[] = { "add", "find", "ls", "login" };
    String userDataColumns[] = { "name-s", "username-s", "password-s", "uid-i", "lastLoggedIn-s", "JCal-i" };
    String eventColumns[] = { "name-s", "date-s", "startTime-i", "endTime-i", "status-i", "priority-i", "eventID-i" };

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

    public String SanitizeArgument(String command) {
        if (isStringNullOrEmpty(command))
            return null;
        return command.substring(0, command.length());
    }

    public String GetArgument(String command, String flag) {
        String text[] = command.split(" ");
        if (text == null || text.length == 0)
            return null;
        for (int i = 0; i < text.length; i++) {
            if (text[i].equals(flag)) {
                if (i != text.length - 1)
                    return SanitizeArgument(text[i + 1]);
                else
                    return null;
            }
        }
        return null;
    }

    private boolean isStringNullOrEmpty(String s) {
        return (s == null || s.isBlank() || s.isEmpty() || s.equals(" ") || s.equals(""));
    }

    public String GetCurrentLogInTime() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return myDateObj.format(myFormatObj);
    }
}
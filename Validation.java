public class Validation {
    private String command;
    private String commandType;
    private String commandTypes[] = { "add", "book", "find", "ls", "login" };
    private String flags[] = { "--delete", "--edit", "--new", "-u", "-p", "-n" };
    private String userDataColumns[] = { "name-s", "username-s", "password-s", "uid-i", "lastLoggedIn-s", "JCal-i" };
    private String eventColumns[] = { "name-s", "date-s", "startTime-i", "endTime-i", "status-i", "priority-i",
            "eventID-i",
            "jCal-i" };

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String[] getCommandTypes() {
        return commandTypes;
    }

    public void setCommandTypes(String[] commandTypes) {
        this.commandTypes = commandTypes;
    }

    public String[] getFlags() {
        return flags;
    }

    public void setFlags(String[] flags) {
        this.flags = flags;
    }

    public String[] getUserDataColumns() {
        return userDataColumns;
    }

    public void setUserDataColumns(String[] userDataColumns) {
        this.userDataColumns = userDataColumns;
    }

    public String[] getEventColumns() {
        return eventColumns;
    }

    public void setEventColumns(String[] eventColumns) {
        this.eventColumns = eventColumns;
    }

    public Messages getOutput() {
        return output;
    }

    public void setOutput(Messages output) {
        this.output = output;
    }

    public CommandUtil getCommandUtil() {
        return commandUtil;
    }

    public void setCommandUtil(CommandUtil commandUtil) {
        this.commandUtil = commandUtil;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        Validation.loggedInUser = loggedInUser;
    }

    protected Messages output = new Messages();
    protected CommandUtil commandUtil = new CommandUtil();
    public static User loggedInUser = new User();

    public Validation(String command) {
        this.command = commandUtil.SetCommand(command);
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
        if (Integer.parseInt(dateValues[0]) > months.length)
            return false;
        else if (Integer.parseInt(dateValues[0]) != months[Integer.parseInt(dateValues[0]) - 1])
            return false;
        else if (Integer.parseInt(dateValues[1]) > days[Integer.parseInt(dateValues[0]) - 1])
            return false;
        else if (Integer.parseInt(dateValues[2]) < 2022)
            return false;
        return true;
    }

    public boolean RunValidation() {
        if (!this.loggedInUser.getLoggedInStatus() && !getCommandType().equals("login")) {
            output.Print(output.GetErrorMessage("lblNotLoggedIn", null), 'e');
            return false;
        }
        String commandType = getCommandType();
        if (commandType == null)
            return false;
        boolean found = false;
        for (String s : this.commandTypes) {
            if (s.equals(commandType)) {
                found = true;
                break;
            }
        }
        if (found) {
            this.commandType = commandType;
            return ValidateCommand();
        }
        return found;
    }

    private boolean ValidateCommand() {
        if (this.command.isBlank() || this.command.isEmpty())
            return false;

        String text[] = this.command.split(" ");
        if (text.length <= 1)
            return false;
        if (!text[0].equals("event"))
            return false;

        switch (this.commandType) {
            case "add":
                Add obj = new Add(getCommand());
                return obj.execute();
            case "book":
            case "find":
                Find find = new Find(this.command);
                return find.execute();
            case "login":
                Login login = new Login(this.command);
                return login.Execute();
            case "ls":
        }

        // Regex validation
        // Based on commandType, run regex. It will be long process

        return true;
    }

    private String getCommandType() {
        if (this.command.isBlank() || this.command.isEmpty())
            return null;

        String text[] = this.command.split(" ");
        if (text.length <= 1)
            return null;
        return text[1];
    }
}

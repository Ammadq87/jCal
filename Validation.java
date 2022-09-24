public class Validation {

    private String command;
    private String commandTypes[] = { "add", "book", "find", "ls", "login" };
    private String commandType;
    CommandUtil cu = new CommandUtil();

    public Validation(String command) {
        this.command = cu.SetCommand(command);
    }

    public boolean validateTime(int start, int end) {
        boolean arr[] = { (start >= 0 && end >= 0) && (start <= 2400 && end <= 2400), (start <= end),
                (start % 100) % 15 == 0 && (end % 100) % 15 == 0 };

        // Find case where value is not true
        for (boolean value : arr) {
            if (!value)
                return false;
        }
        return true;
    }

    public boolean validateDateFormat(String date) {
        if (date == null || date.isEmpty() || date.isBlank()) {
            return false;
        }

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
                Add obj = new Add();
                return obj.execute();
            case "book":
            case "find":
            case "login":
                Login login = new Login(this.command);
                return true;
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

public class Validation {

    private String command;
    private String commandTypes[] = { "add", "book", "find", "ls", "login" };
    private String commandType;

    public Validation(String command) {
        this.command = command;
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

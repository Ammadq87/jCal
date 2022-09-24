public class Find extends DBAccess {
    private int jCalID;
    private String command;

    public Find(String command) {
        this.command = command;
    }

    public boolean execute() {
        this.jCalID = super.loggedInUser.getJCalID();
        if (jCalID == -1) {
            super.msg.Print(super.msg.GetErrorMessage("lblNotLoggedIn", null), 'e');
            return false;
        }
        // Validation ensures that there is only 1 tag
        String text[] = this.command.split(" ");
        String function = "null";
        for (String word : text) {
            if (word.equals("--delete"))
                function = word;
            else if (word.equals("--edit"))
                function = word;
        }
        switch (function) {
            case "--delete":
                deletePrompt();
                break;
            case "--edit":
                editPrompt();
            default:
                findPrompt();
        }
        return true;
    }

    /*
     * no tags:
     * collect events that are similar to input (regex may be used)
     * present events to user
     * 
     * --edit or --delete tags:
     * collect events that are similar to input (regex may be used)
     * present events to user
     * ask user to choose which event to edit
     * prompt to edit event
     * save new event
     * 
     * 
     */

    public void deletePrompt() {

    }

    public void editPrompt() {

    }

    public void findPrompt() {

    }

}

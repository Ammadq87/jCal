import java.util.*;

public class Find extends DBAccess {
    private int jCalID;
    private String command;

    /*
     * event find -n 'event'
     * event find -n 'event' --edit
     * event find -n 'event' --delete
     * 
     */

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
                break;
            default:
                System.out.println(findPrompt());
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
        Map<Integer, Event> results = getMappingOfEvents(findEventQuery());
        String options = showOptions(results);
        if (!options.equals("No Results Found")) {
            System.out.println(options);
            msg.Print("> Select an Event to delete.", 'o');
            System.out.print("> ");
            boolean error = false;
            while (!error) {
                try {

                    Scanner input = new Scanner(System.in);
                    int opt = input.nextInt();

                    Event toBeDeleted = results.get(opt);
                    if (toBeDeleted != null) {
                        String sql = "DELETE FROM events WHERE name =\"" + toBeDeleted.getName() + "\";";
                        super.ExecuteQuery(sql, 0);
                        error = true;
                    } else {
                        msg.Print("> Invalid Option: Could Not Delete", 'e');
                        break;
                    }
                } catch (InputMismatchException e) {
                    msg.Print(msg.GetErrorMessage("lblNonNumerical", null), 'e');
                }
            }

        } else {
            msg.Print("No Results Found", 'o');
        }
    }

    public void editPrompt() {

    }

    private Map<Integer, List<Object>> findEventQuery() {
        String eventName = super.cu.GetArgument(this.command, "-n");
        int jCalID = super.loggedInUser.getJCalID();
        String sql = "SELECT * FROM events WHERE name REGEXP " + eventName + " AND jCal = " + jCalID;
        return super.FetchResults(sql, super.cu.eventColumns);
    }

    public String findPrompt() {
        return showOptions(getMappingOfEvents(findEventQuery()));
    }

    private Map<Integer, Event> getMappingOfEvents(Map<Integer, List<Object>> results) {
        Map<Integer, Event> output = new HashMap<>();
        for (Integer key : results.keySet()) {
            output.put(key, new Event(results.get(key)));
        }
        return output;
    }

    private String showOptions(Map<Integer, Event> options) {
        String output = "";
        if (options == null || options.size() == 0)
            return "No Results Found";
        for (Integer key : options.keySet()) {
            output += "[" + key + "] " + options.get(key).toString() + "\n";
        }
        return output.substring(0, output.length() - 1);
    }

}

import java.util.*;

public class Find extends Validation {
    private int jCalID;
    private String command;

    /*
     * event find -n 'event'
     * event find -n 'event' --edit
     * event find -n 'event' --delete
     * 
     */

    public Find(String command) {
        super(command);
    }

    public boolean execute() {
        // Validation ensures that there is only 1 tag
        String text[] = super.getCommand().split(" ");
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
        DBAccess db = new DBAccess();
        Map<Integer, Event> results = getMappingOfEvents(findEventQuery());
        String options = showOptions(results);
        if (!options.equals("No Results Found")) {
            System.out.println(options);
            super.output.Print("> Select an Event to delete.", 'o');
            System.out.print("> ");
            boolean error = false;
            while (!error) {
                try {

                    Scanner input = new Scanner(System.in);
                    int opt = input.nextInt();

                    Event toBeDeleted = results.get(opt);
                    if (toBeDeleted != null) {
                        String sql = "DELETE FROM events WHERE name =\"" + toBeDeleted.getName() + "\";";
                        db.ExecuteQuery(sql, 0);
                        error = true;
                    } else {
                        super.output.Print("> Invalid Option: Could Not Delete", 'e');
                        break;
                    }
                } catch (InputMismatchException e) {
                    output.Print(output.GetErrorMessage("lblNonNumerical", null), 'e');
                }
            }

        } else {
            output.Print("No Results Found", 'o');
        }
    }

    public void editPrompt() {
        String options = findPrompt();
        if (options.equals("> No Results Found"))
            return;
        System.out.println(options);
        Map<Integer, List<Object>> results = findEventQuery();
        output.Print("Select an Event to edit:", 'o');
        System.out.print("> ");
        Scanner in = new Scanner(System.in);
        int input = -1;
        boolean validOption = false;
        try {
            input = in.nextInt();
            for (int i : results.keySet()) {
                if (i == input) {
                    validOption = true;
                    break;
                }
            }
            if (!validOption)
                output.Print("> Option Does Not Exist", 'e');
        } catch (InputMismatchException e) {
            output.Print("> Option Does Not Exist", 'e');
        }
        if (validOption || input != -1) {
            editScreen(results, input);
        }

    }

    // Try to use CreatePrompt from Add event to reduce code
    private void editScreen(Map<Integer, List<Object>> results, int option) {
    }

    private Map<Integer, List<Object>> findEventQuery() {
        DBAccess db = new DBAccess();
        String eventName = super.commandUtil.GetArgument(super.getCommand(), "-n");
        int jCalID = super.getLoggedInUser().getJCalID();
        String sql = "SELECT * FROM events WHERE name REGEXP " + eventName + " AND jCal = " + jCalID;
        return db.FetchResults(sql, super.getEventColumns());
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
            return "> No Results Found";
        super.output.Print("> Results:", 'o');
        for (Integer key : options.keySet()) {
            output += "[" + key + "] " + options.get(key).toString() + "\n";
        }
        return output.substring(0, output.length() - 1);
    }

}

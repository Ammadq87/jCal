
import java.util.*;

public class Find extends Validation {

    public Find(String command) {
        super(command);
    }

    /**
     * Executes the Add command module. If it's a booking event, it will also book
     * the events
     * 
     * @return - if the excution was possible
     */
    public boolean execute() {
        // Validation ensures that there is only 1 tag
        String text[] = CommandUtil.getCommand().split(" ");
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

    /**
     * Runs event delete
     * ToDo: If Event is a Booking event, the delete module has to delete for all
     * users
     */
    public void deletePrompt() {
        Map<Integer, Event> results = getMappingOfEvents(findEventQuery());
        String options = showOptions(results);
        if (!options.equals("> No Results Found")) {
            System.out.println(options);
            Messages.printMessage("> Select an Event to delete.", 'o');
            System.out.print("> ");
            boolean error = false;
            while (!error) {
                try {

                    Scanner input = new Scanner(System.in);
                    int opt = input.nextInt();

                    Event toBeDeleted = results.get(opt);
                    if (toBeDeleted != null) {
                        String sql = "DELETE FROM events WHERE name =\"" + toBeDeleted.getName() + "\";";
                        DBAccess.ExecuteQuery(sql, 1);
                        error = true;
                    } else {
                        Messages.printMessage("> Invalid Option: Could Not Delete", 'e');
                        break;
                    }
                } catch (InputMismatchException e) {
                    Messages.printMessage(Messages.getErrorMessage("lblNonNumerical", options), 'e');
                }
            }

        } else {
            Messages.printMessage(Messages.getErrorMessage("lblNoResults", options), 'o');
        }
    }

    /**
     * Runs event edit
     * ToDo: If Event is a Booking event, the edit module has to edit for all users
     * that are invited to the events
     */
    public void editPrompt() {
        String options = findPrompt();
        if (options.equals("> No Results Found"))
            return;
        System.out.println(options);
        Map<Integer, List<Object>> results = findEventQuery();
        Messages.printMessage("Select an Event to edit:", 'o');
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
                Messages.printMessage("> Option Does Not Exist", 'o');
        } catch (InputMismatchException e) {
            Messages.printMessage("> Option Does Not Exist", 'o');
        }
        if (validOption || input != -1) {
            Add.findEvent = true;
            Add.oldValues = results.get(input);
            String currName = (String) Add.oldValues.get(0);
            Map<String, List<Object>> x = Add.eventPrompt();
            System.out.println("New Edit Values" + x);
            Add.updateEvent(x.get(Add.eventName), currentUser, currName);
        }
    }

    /**
     * Creates a SELECT query to find the event
     * 
     * @return - a mapping between Event attributes(Converts to Event Class in
     *         getMappingOfEvents()) and ID's
     */
    private Map<Integer, List<Object>> findEventQuery() {
        String eventName = CommandUtil.getArgument(CommandUtil.getCommand(), "-n");
        if (CommandUtil.isNullOrEmpty(eventName))
            return null;
        String sql = "SELECT * FROM Events WHERE name REGEXP " + eventName + " AND UserId = "
                + Validation.currentUser.getUID();
        return DBAccess.FetchResults(sql, CommandUtil.getEventColumns());
    }

    /**
     * Starts the search for the event
     * 
     * @return - displays options if event is present
     */
    public String findPrompt() {
        return showOptions(getMappingOfEvents(findEventQuery()));
    }

    /**
     * Converts Event attributes to Event classes and maps the Event to its ID's
     * 
     * @param results - Mapping of ID and Lists from the db
     * @return - a mapping between Events and their ID's
     */
    private Map<Integer, Event> getMappingOfEvents(Map<Integer, List<Object>> results) {
        Map<Integer, Event> output = new HashMap<>();
        if (results == null)
            return output;
        for (Integer key : results.keySet()) {
            output.put(key, new Event(results.get(key)));
        }
        return output;
    }

    /**
     * Displays list of possible options the user can choose from
     * 
     * @param options - Mapping of possible Event options
     * @return - returns a display of results
     */
    private String showOptions(Map<Integer, Event> options) {
        String output = "";
        if (options == null || options.size() == 0)
            return "> No Results Found";
        Messages.printMessage("> Results:", 'o');
        for (Integer key : options.keySet()) {
            output += "[" + key + "] " + options.get(key).toString() + "\n";
        }
        return output.substring(0, output.length() - 1);
    }

}

import java.util.*;

public class App {
    public static void main(String args[]) {
        new App();
    }

    public App() {
        Messages.printMessage(Messages.getSuccessMessage("lblIntro", null), 'o');
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String command = input.nextLine();
        Validation validate = new Validation(command);
        while (!command.equals("-q")) {
            CommandUtil.setCommand(command);
            if (!validate.RunValidation()) {
                Messages.printMessage(Messages.getErrorMessage("lblCommandNotFound", command), 'e');
            }
            System.out.print("> ");
            command = input.nextLine();
        }
        input.close();
    }
}

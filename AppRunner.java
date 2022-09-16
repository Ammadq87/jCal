import java.util.*;

public class AppRunner {
    public static void main(String args[]) {
        new AppRunner();
    }

    Messages msg = new Messages();

    public AppRunner() {
        msg.Print(msg.GetSuccessMessage("lblIntro", null), 'o');
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String command = input.nextLine();
        while (!command.equals("-q")) {
            Validation validate = new Validation(command);
            if (!validate.RunValidation()) {
                msg.Print(msg.GetErrorMessage("lblCommandNotFound", command), 'e');
            }
            System.out.print("> ");
            command = input.nextLine();
        }
        input.close();
    }
}

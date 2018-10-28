package bill.exp.chat.client.console;

import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.PrintStream;
import java.util.Scanner;

@SuppressWarnings("unused")
@Component
public class DefaultChatClientConsole implements ChatClientConsole, Stoppable {

    private final Stoppable lifetimeManager;
    private final PrintStream out;
    private final Scanner in;
    private final String colorErrorPrefix;
    private final String colorHelpPrefix;
    private final String colorDefaultPrefix;

    @Autowired
    public DefaultChatClientConsole(
            @Qualifier("mainLifetimeManager") Stoppable lifetimeManager
    ) {
        this.lifetimeManager = lifetimeManager;
        out = System.out;
        in = new Scanner(System.in);
        colorErrorPrefix = (char)27 + "[33m";
        colorHelpPrefix = (char)27 + "[31m";
        colorDefaultPrefix = (char)27 + "[0m";
    }

    private void print(String output) {

        out.println(output);
    }

    private static String formatUser(ChatUser user) {

        return user != null && StringUtils.hasLength(user.getName()) ? user.getName() : "<Unknown>";
    }

    private void printError(ChatMessage message) {

        print(colorErrorPrefix + formatMessageText(message) + colorDefaultPrefix);
    }

    private void printHelp(ChatMessage message) {

        print(colorHelpPrefix + formatMessageText(message) + colorDefaultPrefix);
    }

    private void printUser(ChatUser user) {

        print(formatUser(user));
    }

    private void printText(ChatMessage message) {

        print(formatMessageText(message));
    }

    private static String formatMessageText(ChatMessage message) {

        return message.getContent();
    }

    @Override
    public void create() {

    }

    @Override
    public void printOutput(ChatMessage message) {

        if (ChatStandardRoute.Error.toString().equals(message.getRoute())) {

            printError(message);
        }
        else if (ChatStandardRoute.Message.toString().equals(message.getRoute())) {

            if (ChatStandardAction.Fetch.toString().equals(message.getAction())) {
                printUser(message.getAuthor());
                printText(message);
            }
        }
        else if (ChatStandardRoute.Auth.toString().equals(message.getRoute())) {

            if (ChatStandardAction.Login.toString().equals(message.getAction())) {

                if (ChatStandardStatus.Success.toString().equals(message.getStatus())) {
                    print("You are successfully logged in.");
                }
                else {
                    printError(message);
                }
            }
            else if (ChatStandardAction.Logout.toString().equals(message.getAction())) {

                print("Bye.");
                print("Press enter to exit the chat.");
                setStopping();
            }
        }
        else if (ChatStandardRoute.Help.toString().equals(message.getRoute())) {

            printHelp(message);
        }
    }

    @Override
    public ChatMessage readInput(boolean loginPrompt) {

        if (loginPrompt)
            print("Please enter your name to log in");

        final ChatMessage result = new ChatMessage();
        result.setContent(in.nextLine());

        if (isStopping())
            return null;

        return result;
    }

    @Override
    public boolean isStopping() {

        return lifetimeManager.isStopping();
    }

    @Override
    public void setStopping() {

        lifetimeManager.setStopping();
    }

    @Override
    public void setStopped() {

        lifetimeManager.setStopped();
    }

    @Override
    public boolean waitStopped(int timeout) {

        return lifetimeManager.waitStopped(timeout);
    }
}

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

        return user != null && StringUtils.hasLength(user.getName()) ? String.format("@%s:", user.getName()) : "<@Unknown>:";
    }

    private void printError(ChatMessage message) {

        print(colorErrorPrefix + formatMessageText(message) + colorDefaultPrefix);
    }

    private void printHelp(ChatMessage message) {

        print(colorHelpPrefix + formatMessageText(message) + colorDefaultPrefix);
    }

    private void printMessage(ChatMessage message) {

        print(String.format("%s\t%s", formatUser(message.getAuthor()), formatMessageText(message)));
    }

    private static String formatMessageText(ChatMessage message) {

        return message.getContent();
    }

    @Override
    public void create() {

    }

    @Override
    public void printOutput(ChatMessage message) {

        if (message.isStandardRoute(ChatStandardRoute.Error)) {

            printError(message);
        }
        else if (message.isStandardRoute(ChatStandardRoute.Message)) {

            if (message.isStandardAction(ChatStandardAction.Fetch)) {

                printMessage(message);
            }
        }
        else if (message.isStandardRoute(ChatStandardRoute.Auth)) {

            if (message.isStandardAction(ChatStandardAction.Login)) {

                if (message.isStandardStatus(ChatStandardStatus.Success)) {
                    print("You are successfully logged in.");
                }
                else {
                    printError(message);
                }
            }
            else if (message.isStandardAction(ChatStandardAction.Logout)) {

                print("Bye.");
                print("Press enter to exit the chat.");
                setStopping();
            }
        }
        else if (message.isStandardRoute(ChatStandardRoute.Help)) {

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

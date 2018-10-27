package bill.exp.chat.client.console;

import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.model.ChatStandardRoute;
import bill.exp.chat.model.ChatUser;
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
    private final String colorRedPrefix;

    @Autowired
    public DefaultChatClientConsole(
            @Qualifier("mainLifetimeManager") Stoppable lifetimeManager
    ) {
        this.lifetimeManager = lifetimeManager;
        out = System.out;
        in = new Scanner(System.in);
        colorRedPrefix = (char)27 + "[33m";
    }

    private void print(String output) {

        out.println(output);
    }

    private static String formatUser(ChatUser user) {

        return user != null && StringUtils.hasLength(user.getName()) ? user.getName() : "<Unknown>";
    }

    private void printError(ChatMessage message) {

        print(colorRedPrefix + formatMessageText(message));
    }

    private void printUser(ChatUser user) {

        print(formatUser(user));
    }

    private void printText(ChatMessage message) {

        print(formatMessageText(message));
    }

    private static String formatMessageText(ChatMessage message) {

        return String.format("%s%s", message.getRoute(), message.getContent());
    }

    @Override
    public void create() {

    }

    @Override
    public void printOutput(ChatMessage message) {

        if (ChatStandardRoute.Error.toString().equals(message.getRoute())) {

            printError(message);
        }
        else {

            printUser(message.getAuthor());
            printText(message);
        }
    }

    @Override
    public ChatMessage readInput() {

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

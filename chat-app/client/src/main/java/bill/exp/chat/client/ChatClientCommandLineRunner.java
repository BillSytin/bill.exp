package bill.exp.chat.client;

import bill.exp.chat.client.console.ChatClientConsole;
import bill.exp.chat.core.client.io.ClientChannel;
import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Component
@Profile({"client"})
public class ChatClientCommandLineRunner implements CommandLineRunner {

    private final ClientChannel clientChannel;
    private final Stoppable lifeTimeManager;
    private final ChatClientConsole console;

    @Autowired
    public ChatClientCommandLineRunner(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("tcpConnectChannel") ClientChannel clientChannel,
            ChatClientConsole console
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.clientChannel = clientChannel;
        this.console = console;
    }

    @Override
    public void run(String... args) throws Exception {

        final ChatMessage message = new ChatMessage();
        message.setContent(String.format("Connecting to %s...", clientChannel.toString()));
        console.printOutput(message);

        clientChannel.connect().get();
        if (console instanceof Stoppable) {

            ((Stoppable) console).waitStopped(-1);
            ((Stoppable) console).setStopping();
        }

        message.setContent("Session finished. Press enter to exit...");
        console.printOutput(message);

        lifeTimeManager.setStopping();
        if (clientChannel instanceof Stoppable) {

            ((Stoppable) clientChannel).setStopping();
        }

        if (console instanceof Stoppable) {

            if (!((Stoppable) console).waitStopped(1000)) {

                System.exit(-1);
            }
        }
    }
}

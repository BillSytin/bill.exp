package bill.exp.chat.client;

import bill.exp.chat.client.api.ChatClientRequestHandler;
import bill.exp.chat.client.api.ChatClientService;
import bill.exp.chat.client.api.ConsoleChatClientService;
import bill.exp.chat.client.console.ChatClientConsole;
import bill.exp.chat.core.api.RequestHandler;
import bill.exp.chat.core.client.io.ClientChannel;
import bill.exp.chat.core.data.MessageProcessor;
import bill.exp.chat.core.data.RequestMessageProcessor;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.model.ChatStandardRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Component
@Profile("!test")
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

    private static ChatClientService getSessionService(Session session) {

        ChatClientService service = null;
        for (final MessageProcessor processor : session.getProcessingManager().getProcessors()) {

            if (processor instanceof RequestMessageProcessor) {

                final RequestHandler requestHandler = ((RequestMessageProcessor) processor).getRequestHandler();
                if (requestHandler instanceof ChatClientRequestHandler) {

                    service = ((ChatClientRequestHandler) requestHandler).getService();
                }
                break;
            }
        }

        return service;
    }

    @Override
    public void run(String... args) throws Exception {

        final ChatMessage message = new ChatMessage();
        message.setStandardRoute(ChatStandardRoute.Help);
        message.setContent(String.format("Connecting to %s...", clientChannel.toString()));
        console.printOutput(message);

        final Session session = clientChannel.connect().get();
        final ChatClientService service = getSessionService(session);

        if (service instanceof Stoppable) {

            ((Stoppable) service).waitStopped(-1);
            ((Stoppable) service).setStopping();
        }

        lifeTimeManager.setStopping();

        if (console instanceof Stoppable) {

            if (!((Stoppable) console).waitStopped(1000)) {

                message.setContent("Server has closed connection. Press enter to exit.");
                console.printOutput(message);

                if (!((Stoppable) console).waitStopped(10000)) {

                    System.exit(-1);
                }
            }
        }

        if (clientChannel instanceof Stoppable) {

            ((Stoppable) clientChannel).setStopping();
        }
    }
}

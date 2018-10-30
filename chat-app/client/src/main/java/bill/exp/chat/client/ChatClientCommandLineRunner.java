package bill.exp.chat.client;

import bill.exp.chat.client.api.ChatClientRequestHandler;
import bill.exp.chat.client.api.ChatClientService;
import bill.exp.chat.client.console.ChatClientConsole;
import bill.exp.chat.client.util.Utils;
import bill.exp.chat.core.api.RequestHandler;
import bill.exp.chat.core.client.io.ClientChannel;
import bill.exp.chat.core.data.MessageProcessor;
import bill.exp.chat.core.data.RequestMessageProcessor;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.core.model.ChatMessage;
import bill.exp.chat.core.model.ChatStandardRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Component
@Profile("!test")
public class ChatClientCommandLineRunner implements CommandLineRunner {

    private final ClientChannel clientChannel;
    private final Stoppable lifeTimeManager;
    private final ChatClientConsole console;
    private final ResourceBundleMessageSource messageSource;

    @Autowired
    public ChatClientCommandLineRunner(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("tcpConnectChannel") ClientChannel clientChannel,
            @Qualifier("chatClientMessagesResource") ResourceBundleMessageSource messageSource,
            ChatClientConsole console
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.clientChannel = clientChannel;
        this.messageSource = messageSource;
        this.console = console;
    }

    private Locale getLocale() {

        return Utils.getCurrentLocale();
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
        message.setContent(messageSource.getMessage("connecting", new Object[] { clientChannel.toString() }, getLocale()));
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

                message.setContent(messageSource.getMessage("server.close", null, getLocale()));
                console.printOutput(message);

                message.setContent(messageSource.getMessage("logout.exit", null, getLocale()));
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

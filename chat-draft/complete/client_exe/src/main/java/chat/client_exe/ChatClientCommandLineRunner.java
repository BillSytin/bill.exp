package chat.client_exe;

import chat.client.api.ChatClientRequestHandler;
import chat.client.api.ChatClientService;
import chat.client.console.ChatClientConsole;
import chat.client.util.Utils;
import chat.core.api.RequestHandler;
import chat.core.client.io.ClientChannel;
import chat.core.data.MessageProcessor;
import chat.core.data.RequestMessageProcessor;
import chat.core.io.Session;
import chat.core.model.ChatMessage;
import chat.core.model.ChatStandardRoute;
import chat.core.util.Stoppable;
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
    public void run(String... args) {

        final ChatMessage message = new ChatMessage();
        message.setStandardRoute(ChatStandardRoute.Help);
        message.setContent(messageSource.getMessage("connecting", new Object[] { clientChannel.toString() }, getLocale()));
        console.printOutput(message);

        try {
            final Session session = clientChannel.connect().get();
            final ChatClientService service = getSessionService(session);

            if (service instanceof Stoppable) {

                ((Stoppable) service).waitStopped(-1);
                ((Stoppable) service).setStopping();
            }
        }
        catch (final Exception e) {

            console.printOutput(ChatMessage.createErrorMessage(e));
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

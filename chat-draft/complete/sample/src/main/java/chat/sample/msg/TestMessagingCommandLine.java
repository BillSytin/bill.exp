package chat.sample.msg;

import chat.client.api.*;
import chat.client.console.ChatClientConsole;
import chat.core.api.RequestHandler;
import chat.core.api.ResponseIntent;
import chat.core.client.io.ClientChannel;
import chat.core.client.io.ClientSession;
import chat.core.client.io.TcpClientConfig;
import chat.core.data.Message;
import chat.core.data.MessageProcessor;
import chat.core.data.RequestMessageProcessor;
import chat.core.data.ResponseIntentMessage;
import chat.core.io.Session;
import chat.core.io.SessionManager;
import chat.core.model.*;
import chat.core.util.Stoppable;
import chat.server.msg.ChatServerMessageNotificationsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "EmptyMethod"})
@Component
@Profile("messaging")
class TestMessagingCommandLine implements CommandLineRunner {

    private final Log logger = LogFactory.getLog(getClass());

    private Log getLogger() {

        return logger;
    }

    @Autowired
    private TcpClientConfig clientConfig;

    @Autowired
    private ObjectFactory<ChatClientConsole> consoleObjectFactory;

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable lifeTimeManager;

    @Autowired
    @Qualifier("inplaceExecutor")
    private TaskExecutor executor;

    @Autowired
    @Qualifier("mainWorker")
    private Runnable worker;

    @Autowired
    @Qualifier("tcpConnectChannel")
    private ClientChannel clientChannel;

    @Autowired
    @Qualifier("clientSessionManager")
    private SessionManager clientSessionManager;

    @Autowired
    @Qualifier("serverSessionManager")
    private SessionManager serverSessionManager;

    @Autowired
    private ChatServerMessageNotificationsService notificationsService;

    @Autowired
    private TestMessagingConfig config;

    @Override
    public void run(String... args) throws Exception {

        if (args != null && args.length > 0 && "-run".equals(args[0])) {

            integrationTest();
        }
    }

    private void integrationTest() throws Exception {

        final int TestClientCount = config.getClientCount();

        getLogger().info("Starting...");
        executor.execute(worker);

        getLogger().info("Connecting...");
        final ArrayList<Future<ClientSession>> clientFutures = new ArrayList<>();
        for (int i = 0; i < TestClientCount; i++) {

            Thread.sleep(50);
            clientFutures.add(clientChannel.connect());
        }

        final ArrayList<ClientSession> sessions = new ArrayList<>(clientFutures.size());
        for (final Future<ClientSession> future : clientFutures) {

            sessions.add(future.get(5, TimeUnit.MINUTES));
        }

        getLogger().info("Connected");
        getLogger().info("Messaging...");

        final ArrayList<ChatClientConsole> consoles = new ArrayList<>(sessions.size());
        final ArrayList<ChatClientService> services = new ArrayList<>(sessions.size());
        for (final ClientSession session : sessions) {

            ChatClientConsole console = null;
            ChatClientService service = null;
            for (final MessageProcessor processor : session.getProcessingManager().getProcessors()) {

                if (processor instanceof RequestMessageProcessor) {

                    final RequestHandler requestHandler = ((RequestMessageProcessor) processor).getRequestHandler();
                    if (requestHandler instanceof ChatClientRequestHandler) {

                        service = ((ChatClientRequestHandler) requestHandler).getService();
                        if (service instanceof ConsoleChatClientService) {

                            console = (((ConsoleChatClientService) service).getConsole());
                        }
                    }
                    break;
                }
            }

            services.add(service);
            consoles.add(console);
        }

        if (consoles.size() == 1) {

            final ChatMessage fakeMessage = new ChatMessage();
            fakeMessage.setStandardRoute(ChatStandardRoute.Message);
            fakeMessage.setStandardAction(ChatStandardAction.Fetch);
            fakeMessage.setContent("");
            consoles.get(0).printOutput(fakeMessage);
        }

        ArrayList<Integer> incomplete = new ArrayList<>(consoles.size());
        for (int i = 0; i < consoles.size(); i++) {

            incomplete.add(i);
        }

        for (int k = 0; k < 3; k++) {

            final ArrayList<Integer> timeouts = new ArrayList<>();

            for (final Integer index : incomplete) {

                final ChatClientConsole console = consoles.get(index);
                if (console instanceof TestMessagingConsole) {

                    try {

                        ((TestMessagingConsole) console).getDoneInputFuture().get(10, TimeUnit.SECONDS);
                    } catch (final Exception e) {

                        timeouts.add(index);
                    }
                }
            }
            incomplete = timeouts;

            getLogger().info(String.format("Incomplete %d", timeouts.size()));
            if (timeouts.isEmpty())
                break;

            Thread.sleep(timeouts.size() * 1000);
        }

        if (!incomplete.isEmpty()) {

            for (final Integer index : incomplete) {

                final Session session = sessions.get(index);
                final ChatClientService service = services.get(index);
                final ChatClientConsole console = consoles.get(index);

                final ChatMessage message = new ChatMessage();
                message.setStandardRoute(ChatStandardRoute.Auth);
                message.setStandardAction(ChatStandardAction.Logout);
                final ChatClientEnvelope content = new ChatClientEnvelope();
                content.setAuthToken(null);
                content.setMessages(new ChatMessageList());
                content.getMessages().add(message);
                final ResponseIntent responseIntent = new ChatClientResponseIntent(
                        ChatAction.Process,
                        new ChatClientEnvelope[]{content});
                final Message intentMessage = new ResponseIntentMessage(responseIntent);

                session.submit(intentMessage);
                service.process(session, new ChatClientRequestIntent(ChatAction.Process, new String[0]), null);
                console.printOutput(new ChatMessage());
            }
        }

        stop();
    }

    private void stop() {
        getLogger().info("Stopping...");
        gracefulStopSessions(clientSessionManager);
        lifeTimeManager.setStopping();

        if (clientChannel instanceof Stoppable) {

            ((Stoppable) clientChannel).setStopping();
        }

        if (worker instanceof Stoppable) {

            ((Stoppable) worker).setStopping();
        }

        if (notificationsService instanceof Stoppable) {

            ((Stoppable) notificationsService).setStopping();
        }

        lifeTimeManager.waitStopped(1000);
        System.exit(0);
    }

    private static void gracefulStopSessions(SessionManager manager) {

        manager.foreachSession(session -> ((Stoppable) session).setStopping());
        manager.foreachSession(session -> ((Stoppable) session).waitStopped(1000));
        manager.foreachSession(Session::close);
    }
}

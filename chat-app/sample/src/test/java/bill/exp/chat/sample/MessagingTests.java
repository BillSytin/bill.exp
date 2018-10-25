package bill.exp.chat.sample;

import bill.exp.chat.client.api.*;
import bill.exp.chat.client.console.ChatClientConsole;
import bill.exp.chat.core.api.RequestHandler;
import bill.exp.chat.core.api.ResponseIntent;
import bill.exp.chat.core.client.io.ClientChannel;
import bill.exp.chat.core.client.io.ClientSession;
import bill.exp.chat.core.client.io.TcpClientConfig;
import bill.exp.chat.core.data.*;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionManager;
import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "EmptyMethod", "ConstantConditions"})
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("messaging")
public class MessagingTests {
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

    @Test
    public void contextLoads() {

        Assert.assertTrue(consoleObjectFactory.getObject() instanceof TestMessagingConsole);
    }

    @Test
    public void loginTest() throws Exception {

        final int TestClientCount = 1000;

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

        ArrayList<Integer> incomplete = new ArrayList<>(consoles.size());
        for (int i = 0; i < consoles.size(); i++) {

            incomplete.add(i);
        }

        for (int k = 0; k < 2; k++) {

            final ArrayList<Integer> timeouts = new ArrayList<>();

            for (final Integer index : incomplete) {

                final ChatClientConsole console = consoles.get(index);
                if (console instanceof TestMessagingConsole) {

                    try {

                        ((TestMessagingConsole) console).getDoneInputFuture().get(1, TimeUnit.SECONDS);
                    } catch (final Exception e) {

                        timeouts.add(index);
                    }
                }
            }
            incomplete = timeouts;

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
                message.setRoute(ChatStandardRoute.Auth.toString());
                message.setAction(ChatStandardAction.Logout.toString());
                final ChatClientEnvelope content = new ChatClientEnvelope();
                content.setAuthToken(null);
                content.setMessages(new ChatMessageList());
                content.getMessages().add(message);
                final ResponseIntent responseIntent = new ChatClientResponseIntent(
                        ChatAction.Process,
                        new ChatClientEnvelope[] { content });
                final Message intentMessage = new ResponseIntentMessage(responseIntent);

                session.submit(intentMessage);
                service.process(session, new ChatClientRequestIntent(ChatAction.Process, new String[0]), null);
                console.printOutput(new ChatMessage());
            }
        }

        getLogger().info("Stopping...");
        gracefulStopSessions(clientSessionManager);
        lifeTimeManager.setStopping();
        lifeTimeManager.waitStopped(1000);
    }

    private static void gracefulStopSessions(SessionManager manager) {

        manager.foreachSession(session -> ((Stoppable) session).setStopping());
        manager.foreachSession(session -> ((Stoppable) session).waitStopped(1000));
        manager.foreachSession(Session::close);
    }
}

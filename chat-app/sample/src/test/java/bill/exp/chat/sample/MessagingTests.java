package bill.exp.chat.sample;

import bill.exp.chat.client.api.ChatClientRequestHandler;
import bill.exp.chat.client.api.ChatClientService;
import bill.exp.chat.client.api.DefaultChatClientService;
import bill.exp.chat.client.console.ChatClientConsole;
import bill.exp.chat.core.api.RequestHandler;
import bill.exp.chat.core.client.io.ClientChannel;
import bill.exp.chat.core.client.io.ClientSession;
import bill.exp.chat.core.client.io.TcpClientConfig;
import bill.exp.chat.core.data.BaseRequestMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionManager;
import bill.exp.chat.core.util.Stoppable;
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

        getLogger().info("Starting...");
        executor.execute(worker);

        getLogger().info("Connecting...");
        final ArrayList<Future<ClientSession>> clientFutures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {

            clientFutures.add(clientChannel.connect());
        }

        final ArrayList<ClientSession> sessions = new ArrayList<>(clientFutures.size());
        for (final Future<ClientSession> future : clientFutures) {

            sessions.add(future.get(5, TimeUnit.MINUTES));
        }

        getLogger().info("Connected");
        getLogger().info("Messaging...");

        final ArrayList<ChatClientConsole> consoles = new ArrayList<>(sessions.size());
        for (final ClientSession session : sessions) {

            for (final MessageProcessor processor : session.getProcessingManager().getProcessors()) {

                if (processor instanceof BaseRequestMessageProcessor) {

                    final RequestHandler requestHandler = ((BaseRequestMessageProcessor) processor).getRequestHandler();
                    if (requestHandler instanceof ChatClientRequestHandler) {

                        final ChatClientService service = ((ChatClientRequestHandler) requestHandler).getService();
                        if (service instanceof DefaultChatClientService) {

                            consoles.add(((DefaultChatClientService) service).getConsole());
                        }
                    }
                    break;
                }
            }
        }

        for (final ChatClientConsole console : consoles) {

            if (console instanceof TestMessagingConsole) {

                ((TestMessagingConsole) console).getDoneInputFuture().get(10, TimeUnit.MINUTES);
            }
        }

        //Thread.sleep(100000);

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

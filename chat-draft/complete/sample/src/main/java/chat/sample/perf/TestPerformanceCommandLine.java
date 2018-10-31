package chat.sample.perf;

import chat.client.api.ChatClientService;
import chat.core.client.io.ClientChannel;
import chat.core.client.io.TcpClientConfig;
import chat.core.io.Session;
import chat.core.io.SessionManager;
import chat.core.util.Stoppable;
import chat.server.api.ChatServerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Profile("performance")
public class TestPerformanceCommandLine implements CommandLineRunner {

    private final Log logger = LogFactory.getLog(getClass());

    private Log getLogger() {

        return logger;
    }

    @Autowired
    private TestPerformanceClientServer clientServer;

    @Autowired
    private ObjectFactory<ChatServerService> serverServiceObjectFactory;

    @Autowired
    private ObjectFactory<ChatClientService> clientServiceObjectFactory;

    @Autowired
    private TcpClientConfig clientConfig;

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

    @Override
    public void run(String... args) {

        if (args != null && args.length > 0 && "-run".equals(args[0])) {

            performanceTest();
        }
    }

    private void performanceTest() {

        executor.execute(worker);

        for (int i = 0; i < TestPerformanceClientServer.CLIENT_COUNT; i++) {
            try {
                Thread.sleep(10 + clientServer.getRandomSleepTime() / 10);
            }
            catch (final Exception ignored) {

            }
            clientChannel.connect();
        }

        boolean isCompleted = clientServer.waitCompleted();
        stopAfterTimeout(isCompleted ? 1 : TestPerformanceClientServer.TEST_TIME_SEC);

        clientServer.checkResults();
    }

    private static void gracefulStopSessions(SessionManager manager) {

        manager.foreachSession(session -> ((Stoppable) session).setStopping());
        manager.foreachSession(session -> ((Stoppable) session).waitStopped(1000));
        manager.foreachSession(Session::close);
    }

    private void stopAfterTimeout(int timeoutSec) {

        final Stoppable stopper = worker instanceof Stoppable ? (Stoppable)worker : lifeTimeManager;

        gracefulStopSessions(clientSessionManager);

        gracefulStopSessions(serverSessionManager);

        if (clientSessionManager instanceof Stoppable) {

            ((Stoppable) clientSessionManager).setStopping();
            ((Stoppable) clientSessionManager).waitStopped(timeoutSec * 1000);
        }

        stopper.setStopping();

        if (serverSessionManager instanceof Stoppable) {

            ((Stoppable) serverSessionManager).setStopping();
            ((Stoppable) serverSessionManager).waitStopped(timeoutSec * 1000);
        }

        stopper.waitStopped(timeoutSec * 1000);
    }
}

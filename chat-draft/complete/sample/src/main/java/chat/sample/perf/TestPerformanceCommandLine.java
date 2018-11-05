package chat.sample.perf;

import chat.client.api.ChatClientResponseIntent;
import chat.core.client.io.ClientChannel;
import chat.core.client.io.ClientSession;
import chat.core.data.ResponseIntentMessage;
import chat.core.io.Session;
import chat.core.io.SessionManager;
import chat.core.model.ChatAction;
import chat.core.model.ChatClientEnvelope;
import chat.core.util.Stoppable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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
    public void run(String... args) throws Exception {

        if (args != null && args.length > 0 && "-run".equals(args[0])) {

            performanceTest();
            System.exit(0);
        }
    }

    private void performanceTest() throws Exception {

        getLogger().info("Start server");
        executor.execute(worker);

        getLogger().info("Connecting clients...");
        List<Future<ClientSession>> clients = new ArrayList<>();
        for (int i = 0; i < clientServer.getClientCount(); i++) {
            Thread.sleep(10 + clientServer.getRandomSleepTime() / 10);

            clients.add(clientChannel.connect());
        }
        getLogger().info("Clients connected");

        List<ClientSession> sessions = new ArrayList<>();
        for (final Future<ClientSession> client : clients) {
            sessions.add(client.get());
        }

        final ChatClientEnvelope envelope = clientServer.generateOpenSessionResponse(clientServer.generateMessageString());
        final ChatClientResponseIntent intent = new ChatClientResponseIntent(ChatAction.Process, new ChatClientEnvelope[] { envelope });
        for (final Session session : sessions) {

            clientServer.incClientOutputCount();
            session.submit(new ResponseIntentMessage(intent));
        }


        final boolean isCompleted = clientServer.waitCompleted();
        stopAfterTimeout(isCompleted ? 1 : clientServer.getTestTimeSec());

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


        if (clientSessionManager instanceof Stoppable) {

            ((Stoppable) clientSessionManager).setStopping();
            ((Stoppable) clientSessionManager).waitStopped(timeoutSec * 1000);
        }

        stopper.setStopping();

        gracefulStopSessions(serverSessionManager);

        if (serverSessionManager instanceof Stoppable) {

            ((Stoppable) serverSessionManager).setStopping();
            ((Stoppable) serverSessionManager).waitStopped(timeoutSec * 1000);
        }

        stopper.waitStopped(timeoutSec * 1000);
    }
}

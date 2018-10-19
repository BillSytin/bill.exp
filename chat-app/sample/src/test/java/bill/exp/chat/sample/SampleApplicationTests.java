package bill.exp.chat.sample;

import bill.exp.chat.client.api.ChatClientService;
import bill.exp.chat.core.client.io.ClientChannel;
import bill.exp.chat.core.client.io.TcpClientConfig;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionManager;
import bill.exp.chat.core.util.Stoppable;
import bill.exp.chat.server.api.ChatServerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@SuppressWarnings({"unused", "EmptyMethod", "ConstantConditions", "PointlessArithmeticExpression"})
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("performance")
public class SampleApplicationTests {

    private final Log logger = LogFactory.getLog(getClass());

    private Log getLogger() {

        return logger;
    }

    @Autowired
    TestPerformanceClientServer clientServer;

    @Autowired
    ChatServerService server;

    @Autowired
    ChatClientService client;

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

    @Test
	public void contextLoads() {

        Assert.assertEquals(clientServer.getClient(), client);
        Assert.assertEquals(clientServer.getServer(), server);
	}

	@Test
	public void performanceTest() {

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

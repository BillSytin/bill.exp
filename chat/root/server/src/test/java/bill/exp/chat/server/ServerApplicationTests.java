package bill.exp.chat.server;

import bill.exp.chat.core.util.Stoppable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class ServerApplicationTests {

	@MockBean
	private Stoppable lifeTimeManager;

	@Autowired
	@Qualifier("inplaceExecutor")
	private TaskExecutor executor;

	@Autowired
	@Qualifier("mainWorker")
	private Runnable worker;

	@BeforeClass
	public static void init() {
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void serverStartsAndStops() {
		Mockito.when(lifeTimeManager.getIsStopping()).thenReturn(true);
		executor.execute(worker);
	}
}

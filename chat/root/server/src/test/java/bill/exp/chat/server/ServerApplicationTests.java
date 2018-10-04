package bill.exp.chat.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class ServerApplicationTests {

	/*
	@Autowired
	@Qualifier("mainLifetimeManager")
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
	public void serverStartsAndStops() {
		lifeTimeManager.setIsStopping();
		executor.execute(worker);
	}
	*/

	@Test
	public void contextLoads() {
	}
}

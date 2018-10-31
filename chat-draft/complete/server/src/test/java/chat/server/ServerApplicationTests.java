package chat.server;

import chat.core.util.Stoppable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings({"unused", "EmptyMethod"})
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test", "sample"})
public class ServerApplicationTests {

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable lifeTimeManager;

    @Autowired
    @Qualifier("inplaceExecutor")
    private TaskExecutor executor;

    @Autowired
    @Qualifier("mainWorker")
    private Runnable worker;

    @Test
    public void serverStartsAndStops() {

        lifeTimeManager.setStopping();
        executor.execute(worker);
    }

    @Test
    public void contextLoads() {
    }
}

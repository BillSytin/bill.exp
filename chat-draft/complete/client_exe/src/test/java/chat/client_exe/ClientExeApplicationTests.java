package chat.client_exe;

import chat.core.util.Stoppable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings({"unused", "EmptyMethod"})
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ClientExeApplicationTests {

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable mainLifetimeManager;

    @Test
    public void contextLoads() {

        Assert.assertNotNull(mainLifetimeManager);
    }
}

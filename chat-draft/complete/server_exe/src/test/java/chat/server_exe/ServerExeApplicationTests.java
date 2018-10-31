package chat.server_exe;

import chat.core.model.ChatClientEnvelope;
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
public class ServerExeApplicationTests {

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable mainLifetimeManager;

    @Test
    public void contextLoads() {

        Assert.assertNotNull(mainLifetimeManager);
    }

    @Test
    public void envelopeInstantiated() {

        final ChatClientEnvelope envelope = new ChatClientEnvelope();
        Assert.assertNotNull(envelope);
    }
}

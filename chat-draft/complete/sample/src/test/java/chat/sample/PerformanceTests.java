package chat.sample;

import chat.client.api.ChatClientService;
import chat.sample.perf.TestPerformanceChatClientService;
import chat.sample.perf.TestPerformanceChatServerService;
import chat.sample.perf.TestPerformanceClientServer;
import chat.server.api.ChatServerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test", "sample", "performance"})
public class PerformanceTests {

    @Autowired
    private TestPerformanceClientServer clientServer;

    @Autowired
    private ObjectFactory<ChatServerService> serverServiceObjectFactory;

    @Autowired
    private ObjectFactory<ChatClientService> clientServiceObjectFactory;

    @Test
	public void contextLoads() {

        final ChatServerService server = serverServiceObjectFactory.getObject();
        Assert.assertTrue(server instanceof TestPerformanceChatServerService);
        Assert.assertEquals(((TestPerformanceChatServerService) server).getClientServer(), this.clientServer);

        final ChatClientService client = clientServiceObjectFactory.getObject();
        Assert.assertTrue(client instanceof TestPerformanceChatClientService);
        Assert.assertEquals(((TestPerformanceChatClientService) client).getClientServer(), this.clientServer);
	}
}

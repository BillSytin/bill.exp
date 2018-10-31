package chat.sample;

import chat.client.console.ChatClientConsole;
import chat.sample.msg.TestMessagingConsole;
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
@ActiveProfiles({"test", "sample", "messaging"})
public class MessagingTests {

    @Autowired
    private ObjectFactory<ChatClientConsole> consoleObjectFactory;

    @Test
    public void contextLoads() {

        Assert.assertTrue(consoleObjectFactory.getObject() instanceof TestMessagingConsole);
    }
}

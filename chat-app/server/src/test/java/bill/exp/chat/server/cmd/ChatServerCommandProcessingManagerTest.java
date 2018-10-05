package bill.exp.chat.server.cmd;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings({"unused", "EmptyMethod"})
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class ChatServerCommandProcessingManagerTest {

    @Autowired
    private ChatServerCommandProcessingManager processingManager;

    @Test
    public void processGeneratesHelpMessages() {

        final Session session = Mockito.mock(Session.class);

        final ChatClientEnvelope input = new ChatClientEnvelope();
        input.setMessages(new ChatMessageList());

        final ChatMessage message = new ChatMessage();
        message.setType("help");
        message.setText("help");
        input.getMessages().add(message);

        final ChatServerCommandProcessingContext context = new ChatServerCommandProcessingContext(session, ChatBaseAction.Process, input);
        processingManager.process(context);

        final ChatServerEnvelope output = context.getOutput();
        Assert.assertFalse("Help output messages should be not empty", output.getMessages().isEmpty());
        for (ChatMessage helpMessage : output.getMessages()) {

            Assert.assertEquals("Help message type should be 'help'", helpMessage.getType(), "help");
        }

        processingManager.process(context);
    }
}
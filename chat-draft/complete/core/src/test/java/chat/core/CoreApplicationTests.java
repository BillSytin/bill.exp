package chat.core;

import chat.core.model.*;
import chat.core.util.Stoppable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings({"unused", "EmptyMethod"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreApplicationTests {

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable mainLifetimeManager;

	@Autowired
	@Qualifier("serverPoolExecutor")
	private TaskExecutor serverPool;

	@SuppressWarnings("unused")
    @SpringBootApplication
    public static class TestConfiguration {
	}

	@Test
	public void contextLoads() {

	    Assert.assertNotNull(mainLifetimeManager);
	}

	@Test
	public void waitStoppedCompletes() {

		for (int i = 0; i < 10; i++) {
			serverPool.execute(() -> Assert.assertTrue(mainLifetimeManager.waitStopped(10000)));
		}

        serverPool.execute(() -> {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException ignored) {
            }
            mainLifetimeManager.setStopped();
        });

		Assert.assertTrue(mainLifetimeManager.waitStopped(1000));

		for (int i = 0; i < 10; i++) {
			serverPool.execute(() -> Assert.assertTrue(mainLifetimeManager.waitStopped(10)));
		}
	}

	@Test
	public void serializationCompletes() throws Exception {

		final ChatServerEnvelope inputEnvelope = new ChatServerEnvelope();
		inputEnvelope.setAuthToken("token");
		inputEnvelope.setMessages(new ChatMessageList());
		final ChatMessage inputMessage = new ChatMessage();
		inputMessage.setStandardRoute(ChatStandardRoute.Auth);
		inputMessage.setStandardAction(ChatStandardAction.Login);
		inputMessage.setStandardStatus(ChatStandardStatus.Success);
		inputMessage.setContent("content");
		inputEnvelope.getMessages().add(inputMessage);

		final String inputContent = ModelConvert.serialize(inputEnvelope);

		final ChatServerEnvelope outputEnvelope = ModelConvert.deserialize(inputContent, ChatServerEnvelope.class);
		Assert.assertEquals(inputEnvelope.getAuthToken(), outputEnvelope.getAuthToken());
		Assert.assertEquals(inputEnvelope.getMessages().size(), outputEnvelope.getMessages().size());

		final ChatMessage outputMessage = outputEnvelope.getMessages().get(0);
		Assert.assertEquals(inputMessage.getRoute(), outputMessage.getRoute());
		Assert.assertEquals(inputMessage.getAction(), outputMessage.getAction());
		Assert.assertEquals(inputMessage.getStatus(), outputMessage.getStatus());
		Assert.assertEquals(inputMessage.getContent(), outputMessage.getContent());
	}
}

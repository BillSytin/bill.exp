package bill.exp.chat.core;

import bill.exp.chat.core.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("EmptyMethod")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreApplicationTests {

	@Test
	public void contextLoads() {
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

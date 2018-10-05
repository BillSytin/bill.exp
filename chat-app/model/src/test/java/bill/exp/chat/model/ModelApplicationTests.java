package bill.exp.chat.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

@SuppressWarnings("EmptyMethod")
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class ModelApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void clientMessageConverts() throws IOException {

		ChatClientEnvelope input = new ChatClientEnvelope();
		input.setTimestamp(new Date());

		ChatClientEnvelope output = ModelConvert.deserialize(ModelConvert.serialize(input), ChatClientEnvelope.class);

        Assert.assertEquals(input.getTimestamp(), output.getTimestamp());
	}
	
    @Test
    public void serverMessageConverts() throws IOException {

        ChatServerEnvelope input = new ChatServerEnvelope();
        input.setTimestamp(new Date());

        ChatServerEnvelope output = ModelConvert.deserialize(ModelConvert.serialize(input), ChatServerEnvelope.class);

        Assert.assertEquals(input.getTimestamp(), output.getTimestamp());
    }
}

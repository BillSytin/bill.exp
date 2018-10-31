package bill.exp.chat.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("EmptyMethod")
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
@ComponentScan(basePackages = "bill.exp.chat")
public class ClientApplicationTests {

	@Test
	public void contextLoads() {
	}

}

package bill.exp.chat.sample;

import bill.exp.chat.client.io.ClientChannel;
import bill.exp.chat.client.io.ClientSession;
import bill.exp.chat.core.util.Stoppable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleApplicationTests {

	@Autowired
    @Qualifier("mainLifetimeManager")
	private Stoppable lifeTimeManager;

	@Autowired
	@Qualifier("inplaceExecutor")
	private TaskExecutor executor;

	@Autowired
	@Qualifier("mainWorker")
	private Runnable worker;

    @Autowired
    @Qualifier("tcpConnectChannel")
    private ClientChannel client;

	@Test
	public void contextLoads() {
	}

	@Test
	public void clientServerInteracts() {
        final Stoppable stopper = worker instanceof Stoppable ? (Stoppable)worker : lifeTimeManager;

		executor.execute(worker);

		client.connect(null, new CompletionHandler<ClientSession, Void>() {
            @Override
            public void completed(ClientSession result, Void attachment) {
                ByteBuffer encoded = StandardCharsets.UTF_8.encode("Test string");
                encoded.rewind();
                ByteBuffer output = ByteBuffer.allocate(encoded.remaining() + 1);
                output.put(encoded);
                output.put((byte) 0);
                output.flip();

                result.write(output);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {

            }
        });

		if (!stopper.waitStopped(1 * 60 * 1000)) {
            stopper.setIsStopping();
            stopper.waitStopped(10 * 1000);
        }
	}
}

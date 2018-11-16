package chat.core;

import chat.core.model.*;
import chat.core.util.Stoppable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import java.util.concurrent.Semaphore;

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

	private static final int LONG_WAIT = 10000;

	@Test(timeout = LONG_WAIT / 2)
	public void shouldNotHang() throws InterruptedException {

		Manager manager = new Manager();
		Thread waiter = new Thread(() -> manager.waitStopped(LONG_WAIT));
		waiter.start();
		manager.setStopped();
		waiter.join();
	}

	static class Manager {

		private final Object completeEvent;
		private final Semaphore stopPointReachedOut = new Semaphore(0);
		private final Semaphore waitPointReachedOut = new Semaphore(0);
		private volatile boolean isStopping;
		private volatile boolean isStopped;

		Manager() {
			completeEvent = new Object();
			isStopping = false;
			isStopped = false;
		}

		void setStopped() throws InterruptedException {

			waitPointReachedOut.acquire(1);
			if (!isStopped) {

				synchronized (completeEvent) {
				    if (!isStopped) {
                        isStopped = true;
                        completeEvent.notifyAll();
                    }
				}

				stopPointReachedOut.release();
			}
		}

		boolean waitStopped(int timeout) {

			if (!isStopped) {

				waitPointReachedOut.release();
				try {
					stopPointReachedOut.acquire(1);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return false;
				}

				synchronized (completeEvent) {

				    if (!isStopped) {
                        try {
                            if (timeout < 0) {
                                completeEvent.wait();
                            } else {
                                completeEvent.wait(timeout);
                            }
                        } catch (final InterruptedException e) {
                            return false;
                        }
                    }
				}
			}
			return isStopped;
		}
	}
}

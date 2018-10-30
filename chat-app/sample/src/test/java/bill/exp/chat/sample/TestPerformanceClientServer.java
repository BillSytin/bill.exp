package bill.exp.chat.sample;

import bill.exp.chat.client.api.ChatClientService;
import bill.exp.chat.client.api.ChatClientRequestIntent;
import bill.exp.chat.client.api.ChatClientResponseIntent;
import bill.exp.chat.core.client.io.TcpClientConfig;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.model.*;
import bill.exp.chat.server.api.ChatServerRequestIntent;
import bill.exp.chat.server.api.ChatServerService;
import bill.exp.chat.server.api.ChatServerResponseIntent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "EmptyMethod", "ConstantConditions", "PointlessArithmeticExpression"})
@Component
@Profile("performance")
class TestPerformanceClientServer {

    private final Log logger = LogFactory.getLog(getClass());

    private Log getLogger() {

        return logger;
    }

    private final AtomicInteger clientConnectCount = new AtomicInteger();
    private final AtomicInteger serverAcceptCount = new AtomicInteger();

    private final AtomicInteger clientOutputCount = new AtomicInteger();
    private final AtomicInteger serverInputCount = new AtomicInteger();

    private final AtomicInteger clientInputCount = new AtomicInteger();
    private final AtomicInteger serverOutputCount = new AtomicInteger();

    private final AtomicInteger clientInputCloseCount = new AtomicInteger();
    private final AtomicInteger serverOutputCloseCount = new AtomicInteger();

    private final AtomicInteger clientOutputCloseCount = new AtomicInteger();
    private final AtomicInteger serverInputCloseCount = new AtomicInteger();

    private final AtomicInteger interactCount = new AtomicInteger();
    private final CompletableFuture<Boolean> interactCompleted = new CompletableFuture<>();

    public final static int CLIENT_COUNT = 10;
    private final static boolean USE_LARGE_MESSAGE = CLIENT_COUNT <= 10;
    private final static int CLIENT_SLEEP_TIME = 10;
    public final static int TEST_TIME_SEC = 30;
    private final static int TEST_INTERACT_COUNT = 5000;//3 * 100 * TEST_TIME_SEC * 1000 / (CLIENT_SLEEP_TIME + 10) / (USE_LARGE_MESSAGE ? 10 : 1);

    private final TcpClientConfig clientConfig;

    private final ChatServerService server;

    private final ChatClientService client;

    private final Random random;

    @Autowired
    public TestPerformanceClientServer(TcpClientConfig clientConfig) {

        this.clientConfig = clientConfig;

        random = new Random();
        server = new ServerService();
        client = new ClientService();
    }

    public ChatServerService getServer() {
        return server;
    }

    public ChatClientService getClient() {
        return client;
    }

    public boolean waitCompleted() {

        boolean isCompleted = false;
        try {
            if (interactCompleted.get(TEST_TIME_SEC, TimeUnit.SECONDS))
                isCompleted = true;
        } catch (final Exception e) {

            getLogger().error("Test is not completed at the expected time", e);
        }

        return isCompleted;
    }

    public void checkResults() {

        getLogger().info(String.format("client connect: %d%n", clientConnectCount.get()));
        getLogger().info(String.format("server accept: %d%n", serverAcceptCount.get()));

        getLogger().info(String.format("client output: %d%n", clientOutputCount.get()));
        getLogger().info(String.format("server input: %d%n", serverInputCount.get()));

        getLogger().info(String.format("server output: %d%n", serverOutputCount.get()));
        getLogger().info(String.format("client input: %d%n", clientInputCount.get()));

        getLogger().info(String.format("client close output: %d%n", clientOutputCloseCount.get()));
        getLogger().info(String.format("server close input: %d%n", serverInputCloseCount.get()));

        getLogger().info(String.format("server close output: %d%n", serverOutputCloseCount.get()));
        getLogger().info(String.format("client close input: %d%n", clientInputCloseCount.get()));

        Assert.assertEquals(clientConnectCount.get(), CLIENT_COUNT);
        Assert.assertEquals(serverAcceptCount.get(), CLIENT_COUNT);
    }

    private static ChatClientEnvelope generateOpenSessionResponse(String messageText) {

        final ChatMessage message = new ChatMessage();
        message.setStandardRoute(ChatStandardRoute.Help);
        message.setStandardAction(ChatStandardAction.Default);
        message.setStandardStatus(ChatStandardStatus.Success);
        message.setContent(messageText);

        final ChatClientEnvelope output = new ChatClientEnvelope();
        output.setMessages(new ChatMessageList());
        output.getMessages().add(message);

        return output;
    }


    private static boolean cloneInputFirstMessage(ChatBaseEnvelope model, ChatBaseEnvelope output) {

        if (model == null || model.getMessages() == null || model.getMessages().isEmpty()) {
            return false;
        }

        final ChatMessage input = model.getMessages().get(0);
        output.setMessages(new ChatMessageList());
        output.getMessages().add(input);

        return true;
    }

    private String generateMessageString(boolean large) {

        if (large) {
            StringBuilder sb = new StringBuilder(clientConfig.getReadBufferSize() * 2 + 100);
            while (sb.length() < clientConfig.getReadBufferSize() * 2 + 50)
                sb.append("Test string\n");
            return sb.toString();
        }

        return "Test string\n";
    }

    private boolean checkCompletion() {

        int count = interactCount.getAndIncrement();

        if ((count % (TEST_INTERACT_COUNT / 10)) == 0)
            getLogger().info(String.format("%d of %d%n", count, TEST_INTERACT_COUNT));

        if (count < TEST_INTERACT_COUNT)
            return true;

        if (count == TEST_INTERACT_COUNT)
            interactCompleted.complete(true);

        return false;
    }

    private class ServerService implements ChatServerService {

        @Override
        public ChatServerResponseIntent process(Session session, ChatServerRequestIntent intent, ChatClientEnvelope[] models) {

            ChatAction action = ChatAction.Process;
            ChatServerEnvelope output = null;
            final ChatClientEnvelope model = models != null && models.length > 0 ? models[models.length - 1] : new ChatClientEnvelope();
            switch (intent.getAction()) {

                case OpenSession:
                    serverAcceptCount.getAndIncrement();
                    break;

                case CloseSession:
                    serverOutputCloseCount.getAndIncrement();
                    output = new ChatServerEnvelope();
                    output.setAction(ChatAction.CloseSession);
                    break;

                case Process:

                    if (model.getAction() == ChatAction.CloseSession) {

                        serverInputCloseCount.getAndIncrement();
                        action = ChatAction.CloseSession;
                    }
                    else {

                        serverInputCount.getAndIncrement();
                        if (checkCompletion()) {

                            output = new ChatServerEnvelope();
                            if (cloneInputFirstMessage(model, output)) {
                                serverOutputCount.getAndIncrement();
                            } else {
                                output = null;
                            }
                        }
                    }
                    break;
            }

            return new ChatServerResponseIntent(action, output != null ? new ChatServerEnvelope[] { output } : null);
        }

        @Override
        public boolean isAsyncIntent(ChatServerRequestIntent intent) {

            return intent.getAction() == ChatAction.Process;
        }

        @Override
        public void dispose(Session session) {

        }
    }

    public int getRandomSleepTime() {

        return CLIENT_SLEEP_TIME / 2 + random.nextInt(1 + CLIENT_SLEEP_TIME / 2);
    }

    private class ClientService implements ChatClientService {

        @Override
        public ChatClientResponseIntent process(Session session, ChatClientRequestIntent intent, ChatServerEnvelope[] models) {

            ChatAction action = ChatAction.Process;
            ChatClientEnvelope output = null;
            final ChatServerEnvelope model = models != null && models.length > 0 ? models[models.length - 1] : new ChatServerEnvelope();
            switch (intent.getAction()) {

                case OpenSession:
                    clientConnectCount.getAndIncrement();
                    clientOutputCount.getAndIncrement();
                    output = generateOpenSessionResponse(generateMessageString(USE_LARGE_MESSAGE));
                    break;

                case CloseSession:
                    clientOutputCloseCount.getAndIncrement();
                    output = new ChatClientEnvelope();
                    output.setAction(ChatAction.CloseSession);
                    break;

                case Process:
                    if (model.getAction() == ChatAction.CloseSession) {

                        clientInputCloseCount.getAndIncrement();
                        action = ChatAction.CloseSession;
                    }
                    else {

                        clientInputCount.getAndIncrement();
                        if (checkCompletion()) {

                            output = new ChatClientEnvelope();
                            if (cloneInputFirstMessage(model, output)) {

                                clientOutputCount.getAndIncrement();
                                if (CLIENT_SLEEP_TIME > 0) {
                                    try {
                                        Thread.sleep(getRandomSleepTime());
                                    }
                                    catch (final InterruptedException ignored) {
                                    }
                                }
                            } else {
                                output = null;
                            }
                        }
                    }
                    break;
            }

            return new ChatClientResponseIntent(action, output != null ? new ChatClientEnvelope[] { output } : null);
        }

        @Override
        public boolean isAsyncIntent(ChatClientRequestIntent intent) {

            return false;
        }

        @Override
        public void dispose(Session session) {

        }
    }
}

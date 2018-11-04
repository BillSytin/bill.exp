package chat.sample.perf;

import chat.core.client.io.TcpClientConfig;
import chat.core.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class TestPerformanceClientServer {

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

    private final int clientCount;
    private final boolean useLargeMessage;
    private final int clientSleepTime;

    private final int testTimeSec;
    private final int testInteractCount;

    private final TcpClientConfig clientConfig;

    private final Random random;

    @Autowired
    public TestPerformanceClientServer(TcpClientConfig clientConfig, TestPerformanceConfig performanceConfig) {

        this.clientConfig = clientConfig;

        this.clientCount = performanceConfig.getClientCount();
        this.useLargeMessage = clientCount <= 10;
        this.clientSleepTime = performanceConfig.getClientSleepTime();
        this.testTimeSec = performanceConfig.getTestTimeSec();
        this.testInteractCount = 10 * 100 * testTimeSec * 1000 / (clientSleepTime + 100) / (useLargeMessage ? 10 : 1);

        random = new Random();
    }

    public int getClientCount() {
        return clientCount;
    }

    public int getTestTimeSec() {
        return testTimeSec;
    }

    public int incServerAcceptCount() {

        return serverAcceptCount.incrementAndGet();
    }

    public int incServerOutputCloseCount() {

        return serverOutputCloseCount.incrementAndGet();
    }

    public int incServerInputCloseCount() {

        return serverInputCloseCount.incrementAndGet();
    }

    public int incServerInputCount() {

        return serverInputCount.incrementAndGet();
    }

    public int incServerOutputCount() {

        return serverOutputCount.incrementAndGet();
    }

    public int incClientConnectCount() {

        return clientConnectCount.incrementAndGet();
    }

    public int incClientOutputCount() {

        return clientOutputCount.incrementAndGet();
    }

    public int incClientOutputCloseCount() {

        return clientOutputCloseCount.incrementAndGet();
    }

    public int incClientInputCloseCount() {

        return clientInputCloseCount.incrementAndGet();
    }

    public int incClientInputCount() {

        return clientInputCount.incrementAndGet();
    }

    public boolean waitCompleted() {

        boolean isCompleted = false;
        try {
            if (interactCompleted.get(testTimeSec, TimeUnit.SECONDS))
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
    }

    public ChatClientEnvelope generateOpenSessionResponse(String messageText) {

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


    public boolean cloneInputFirstMessage(ChatBaseEnvelope model, ChatBaseEnvelope output) {

        if (model == null || model.getMessages() == null || model.getMessages().isEmpty()) {
            return false;
        }

        final ChatMessage input = model.getMessages().get(0);
        output.setMessages(new ChatMessageList());
        output.getMessages().add(input);

        return true;
    }

    public String generateMessageString() {

        return generateMessageString(useLargeMessage);
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

    public boolean checkCompletion() {

        int count = interactCount.getAndIncrement();

        if ((count % (testInteractCount / 10)) == 0)
            getLogger().info(String.format("%d of %d%n", count, testInteractCount));

        if (count < testInteractCount)
            return true;

        if (count == testInteractCount)
            interactCompleted.complete(true);

        return false;
    }

    public int getClientSleepTime() {

        return clientSleepTime;
    }

    public int getRandomSleepTime() {

        return clientSleepTime / 2 + random.nextInt(1 + clientSleepTime / 2);
    }
}

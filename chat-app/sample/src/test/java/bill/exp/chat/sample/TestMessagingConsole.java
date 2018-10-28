package bill.exp.chat.sample;

import bill.exp.chat.client.console.ChatClientConsole;
import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.model.ChatStandardAction;
import bill.exp.chat.model.ChatStandardRoute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Component
@Profile("messaging")
@Scope("prototype")
@Primary
public class TestMessagingConsole implements ChatClientConsole {

    private static final AtomicInteger userSequence = new AtomicInteger();
    private final Log logger = LogFactory.getLog(getClass());

    private final CompletableFuture<Void> doneInputFuture;
    private final String[] inputs = {
            "Bill",
            "hi",
            "-help",
            "bye",
            "-logout"
    };
    private final CompletableFuture<Void> doneFetchFuture;

    private final int userId;
    private final AtomicInteger currentInputIndex;
    private final AtomicInteger inputDone;
    private final AtomicInteger fetchCount;

    private Log getLogger() {

        return logger;
    }

    public TestMessagingConsole() {

        doneInputFuture = new CompletableFuture<>();
        doneFetchFuture = new CompletableFuture<>();
        currentInputIndex = new AtomicInteger(0);
        inputDone = new AtomicInteger(0);
        fetchCount = new AtomicInteger(0);
        userId = userSequence.getAndIncrement();
    }

    public CompletableFuture getDoneInputFuture() {
        return doneInputFuture;
    }

    private synchronized void printOutputSync(ChatMessage message) {

        logger.info(String.format("%d %s%n", userId, message.toString()));
    }

    private void incrementInputDone() {

        if (inputDone.incrementAndGet() == 3) {

            doneInputFuture.complete(null);
        }
    }

    @Override
    public void create() {

    }

    @Override
    public void printOutput(ChatMessage message) {

        printOutputSync(message);

        if (ChatStandardRoute.Auth.toString().equals(message.getRoute()) &&
                ChatStandardAction.Logout.toString().equals(message.getAction())) {

            incrementInputDone();
        }

        if (ChatStandardRoute.Message.toString().equals(message.getRoute()) &&
                ChatStandardAction.Fetch.toString().equals(message.getAction())) {

            if (fetchCount.incrementAndGet() == 1) {

                incrementInputDone();
                doneFetchFuture.complete(null);
            }
        }
    }

    @Override
    public ChatMessage readInput() {

        try {
            Thread.sleep(100);
        }
        catch (final InterruptedException ignored) {
        }

        final int index = currentInputIndex.getAndIncrement();
        if (index < inputs.length) {

            final ChatMessage message = new ChatMessage();
            message.setContent(inputs[index]);
            if (index == 0) {

                message.setContent(message.getContent() + userId);
            }
            if (index == inputs.length - 1) {

                try {
                    doneFetchFuture.get(10, TimeUnit.MINUTES);
                }
                catch (final Exception e) {

                    getLogger().error("Fetch is not done", e);
                }
            }
            return message;
        }

        if (index == inputs.length) {

            incrementInputDone();
        }

        return null;
    }
}

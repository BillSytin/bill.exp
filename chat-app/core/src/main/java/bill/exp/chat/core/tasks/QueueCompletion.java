package bill.exp.chat.core.tasks;

import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public interface QueueCompletion<V, A> {

    void submit(Consumer<CompletionHandler<V, A>> runFunction, CompletionHandler<V, A> onCompleted);
}

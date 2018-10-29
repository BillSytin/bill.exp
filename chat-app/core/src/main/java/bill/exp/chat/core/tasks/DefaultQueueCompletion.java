package bill.exp.chat.core.tasks;

import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class DefaultQueueCompletion<V, A> implements QueueCompletion<V, A> {

    private RunCompletionHandler last;

    public DefaultQueueCompletion() {
        last = null;
    }

    public void submit(Consumer<CompletionHandler<V, A>> runFunction, CompletionHandler<V, A> onCompleted) {

        submitNext(new RunCompletionHandler(runFunction, onCompleted));
    }

    private void submitNext(RunCompletionHandler next) {

        if (enqueue(next)) {

            next.start();
        }
    }

    private synchronized boolean enqueue(RunCompletionHandler next) {

        if (last == null) {
            last = next;
            return true;
        } else {
            last.setNext(next);
            last = next;
            return false;
        }
    }

    private synchronized RunCompletionHandler dequeue(RunCompletionHandler prev) {

        final RunCompletionHandler next = prev.getNext();
        if (next == null)
            last = null;
        return next;
    }

    private void runNext(RunCompletionHandler prev) {

        final RunCompletionHandler next = dequeue(prev);
        if (next != null)
            next.start();
    }

    private class RunCompletionHandler implements CompletionHandler<V, A> {

        private final Consumer<CompletionHandler<V, A>> runFunction;
        private final CompletionHandler<V, A> onCompleted;
        private boolean nextWasHandled;
        private RunCompletionHandler next;

        @SuppressWarnings("unused")
        public RunCompletionHandler(
                Consumer<CompletionHandler<V, A>> runFunction,
                CompletionHandler<V, A> onCompleted
        ) {

            this.runFunction = runFunction;
            this.onCompleted = onCompleted;
            this.nextWasHandled = false;
        }

        public void start() {

            runFunction.accept(this);
        }

        public RunCompletionHandler getNext() {

            return next;
        }

        public void setNext(RunCompletionHandler next) {

            this.next = next;
        }

        private void handleNext() {

            if (!nextWasHandled) {

                nextWasHandled = true;
                runNext(this);
            }
        }

        @Override
        public void completed(V result, A attachment) {

            onCompleted.completed(result, attachment);
            handleNext();
        }

        @Override
        public void failed(Throwable exc, A attachment) {

            onCompleted.failed(exc, attachment);
            handleNext();
        }
    }
}

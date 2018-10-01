package bill.exp.chat.core.io;

import bill.exp.chat.core.data.*;
import org.springframework.core.task.TaskExecutor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public abstract class BaseAsyncSession implements AsyncSession {
    private AsynchronousSocketChannel channel;
    private final ByteBuffer readBuffer;
    private final CompletionHandler<Integer, Session> readCompletionHandler;
    private final CompletionHandler<Integer, Session> writeCompletionHandler;
    private final MessageProcessingCompletionHandler processingCompletionHandler;
    private MessageProcessingState currentProcessingState;

    protected BaseAsyncSession(int bufferSize) {

        readBuffer = ByteBuffer.allocate(bufferSize);
        readCompletionHandler = new ReadCompletionHandler();
        writeCompletionHandler = new WriteCompletionHandler();
        processingCompletionHandler = new MessageProcessingCompletionHandler();
        currentProcessingState = null;
    }

    protected abstract TaskExecutor getQueueExecutor();

    protected abstract SessionManager getSessionManager();

    protected abstract MessageProcessingManager getProcessingManager();

    private void readNext() {

        channel.read(readBuffer, this, readCompletionHandler);
    }

    @Override
    public void open(AsynchronousSocketChannel channel) {
        this.channel = channel;
        getSessionManager().addSession(this);

        readNext();
    }

    private synchronized void readCompleted(Integer readCount) {

        if (readCount < 0)
            return;

        final ByteBuffer original = readBuffer;
        final boolean isIncomplete = !original.hasRemaining();

        original.flip();
        final ByteBuffer clone = ByteBuffer.allocate(original.limit());

        original.rewind();
        clone.put(original);
        clone.flip();
        original.clear();

        getQueueExecutor().execute(() -> processReadMessage(new ByteBufferMessage(clone, isIncomplete)));

        readNext();
    }

    private void processReadMessage(Message message) {

        MessageProcessingState processingState = currentProcessingState;
        if (processingState == null) {
            processingState = new MessageProcessingState(this, message, getProcessingManager().createProcessor());
        }
        else {
            currentProcessingState = null;
        }

        processingState.getProcessor().process(processingState, processingCompletionHandler);
    }

    private void writeNext(ByteBuffer output) {

        channel.write(output, this, writeCompletionHandler);
    }

    private synchronized void writeReplyMessage(MessageProcessingState state) {

        ByteBuffer output = state.getOutputBuffer();
        if (output != null) {
            getQueueExecutor().execute(() -> writeNext(output));
        }
    }

    private void writeCompleted(Integer writeCount) {

    }

    @Override
    public void close() {

        getSessionManager().removeSession(this);

        try {
            channel.close();
        }
        catch (final IOException e) {
        }
    }

    private class MessageProcessingCompletionHandler implements CompletionHandler<MessageProcessingAction, MessageProcessingState> {

        @Override
        public void completed(MessageProcessingAction result, MessageProcessingState attachment) {
            switch (result) {
                case RESET:
                    currentProcessingState = attachment;
                    break;
                case NEXT:
                    break;
                case REPLY:
                    writeReplyMessage(attachment);
                    break;
            }
        }

        @Override
        public void failed(Throwable exc, MessageProcessingState attachment) {

        }
    }

    private final class ReadCompletionHandler implements CompletionHandler<Integer, Session> {

        @Override
        public void completed(Integer result, Session attachment) {
            ((BaseAsyncSession)attachment).readCompleted(result);
        }

        @Override
        public void failed(Throwable exc, Session attachment) {

        }
    }

    private final class WriteCompletionHandler implements CompletionHandler<Integer, Session> {

        @Override
        public void completed(Integer result, Session attachment) {
            ((BaseAsyncSession)attachment).writeCompleted(result);
        }

        @Override
        public void failed(Throwable exc, Session attachment) {

        }
    }
}

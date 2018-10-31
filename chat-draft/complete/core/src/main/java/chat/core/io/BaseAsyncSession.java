package chat.core.io;

import chat.core.data.*;
import chat.core.tasks.DefaultQueueCompletion;
import chat.core.tasks.QueueCompletion;
import chat.core.util.Stoppable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public abstract class BaseAsyncSession implements AsyncSession, Session, Stoppable {

    private final Log logger = LogFactory.getLog(getClass());
    private final long id;
    private AsynchronousSocketChannel channel;
    private final SessionManager sessionManager;

    private final MessageProcessingManager processingManager;
    private final ByteBuffer readBuffer;
    private final CompletionHandler<Integer, Session> readCompletionHandler;
    private final CompletionHandler<Integer, Session> writeCompletionHandler;
    private final QueueCompletion<Integer, Session> writeQueueCompletion;
    private final CompletionHandler<MessageProcessingAction, MessageProcessingState> processingCompletionHandler;
    private final QueueCompletion<MessageProcessingAction, MessageProcessingState> processingQueueCompletion;
    private volatile boolean isClosing;
    private volatile boolean isClosed;
    private volatile Future<Boolean> futureClose;

    protected BaseAsyncSession(
            int bufferSize,
            SessionManager sessionManager,
            MessageProcessingManager processingManager
    ) {

        this.sessionManager = sessionManager;
        this.processingManager = processingManager;

        isClosing = true;
        isClosed = true;
        id = sessionManager.generateSessionId();
        readBuffer = ByteBuffer.allocate(bufferSize);
        readCompletionHandler = new ReadCompletionHandler();

        writeCompletionHandler = new WriteCompletionHandler();
        writeQueueCompletion = new DefaultQueueCompletion<>();

        processingCompletionHandler = new MessageProcessingCompletionHandler();
        processingQueueCompletion = new DefaultQueueCompletion<>();
    }

    @Override
    public MessageProcessingManager getProcessingManager() {
        return processingManager;
    }

    private void readNext() {

        if (!isClosing) {

            channel.read(readBuffer, this, readCompletionHandler);
        }
    }

    @Override
    public String toString() {

        return Long.toString(getId());
    }

    @Override
    public long getId() {

        return id;
    }

    @Override
    public void open(AsynchronousSocketChannel channel) {

        this.channel = channel;
        isClosing = false;
        isClosed = false;
        sessionManager.addSession(this);

        submitMessage(new SessionEventMessage(getId(), SessionEvent.Open));

        readNext();
    }

    @Override
    public void submit(Message message, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        if (completionHandler == null) {

            submitMessage(message);
        }
        else {

            submitMessage(message, new CompletionHandler<MessageProcessingAction, MessageProcessingState>() {

                @Override
                public void completed(MessageProcessingAction result, MessageProcessingState attachment) {

                    processingCompletionHandler.completed(result, attachment);
                    completionHandler.completed(result, attachment);
                }

                @Override
                public void failed(Throwable exc, MessageProcessingState attachment) {

                    processingCompletionHandler.failed(exc, attachment);
                    completionHandler.failed(exc, attachment);
                }
            });

        }
    }

    private void submitMessage(Message message) {

        submitMessage(message, processingCompletionHandler);
    }

    private synchronized void submitMessage(Message message, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        processingQueueCompletion.submit(handler -> processMessage(message, handler), completionHandler);
    }

    private synchronized void readCompleted(Integer readCount) {

        if (readCount < 0) {

            if (!isClosing) {

                logger.warn(String.format("Unexpected read count at session: %s, count: %d, closing session%n", this.toString(), readCount));
            }
            close();
            return;
        }

        final ByteBuffer original = readBuffer;

        original.flip();
        final ByteBuffer clone = ByteBuffer.allocate(original.limit());

        original.rewind();
        clone.put(original);
        clone.flip();
        original.clear();

        submitMessage(new ByteBufferMessage(clone));

        readNext();
    }

    private void processMessage(Message message, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        final MessageProcessingChain processingChain = processingManager.createProcessingChain();
        final MessageProcessingState processingState = new MessageProcessingState(this, completionHandler, message);
        processingChain.process(processingState);
    }

    private void writeNext(ByteBuffer output, CompletionHandler<Integer, Session> completionHandler) {

        if (output != null && !isClosed) {

            channel.write(output, this, completionHandler);
        }
        else {

            completionHandler.completed(output != null ? -1 : 0, this);
        }
    }

    private synchronized void handleOutputMessage(MessageProcessingState state) {

        Message output = state.getOutputMessage();
        if (output != null) {

            if (output instanceof ByteBufferMessage) {

                writeBuffers(((ByteBufferMessage) output).getBuffers());
            }
            else if (output instanceof SessionEventMessage) {

                if (((SessionEventMessage) output).getEvent() == SessionEvent.Close) {

                    close();
                }
            }
        }
    }

    private static void SleepBeforeClose() {

        try {

            // a chance to complete IO
            Thread.sleep(100);
        }
        catch (final Exception ignored) {
        }
    }

    private void writeBuffers(ByteBuffer[] output) {

        writeBuffers(output, writeCompletionHandler);
    }

    private synchronized void writeBuffers(ByteBuffer[] outputs, CompletionHandler<Integer, Session> completionHandler) {

        for (final ByteBuffer output : outputs) {

            writeQueueCompletion.submit(handler -> writeNext(output, handler), completionHandler);
        }
    }

    private void writeCompleted(Integer writeCount) {

        if (writeCount < 0) {

            if (!isClosing) {

                logger.warn(String.format("Unexpected write count at session: %s, count: %d, closing session%n", this.toString(), writeCount));
            }
            close();
        }
    }

    @Override
    public void close() {

        closeImpl();
    }

    private void closeProcessing() {

        final CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler = new CompletionHandler<MessageProcessingAction, MessageProcessingState>() {
            @Override
            public void completed(MessageProcessingAction result, MessageProcessingState attachment) {

            }

            @Override
            public void failed(Throwable exc, MessageProcessingState attachment) {

                logger.warn(String.format("Exception on close processing at session: %s%n", attachment.getSession().toString()), exc);
            }
        };

        final MessageProcessingState processingState = new MessageProcessingState(this,
                completionHandler,
                new SessionEventMessage(this.getId(), SessionEvent.Dispose));
        final MessageProcessingChain processingChain = processingManager.createProcessingChain();
        processingChain.process(processingState);
    }

    private synchronized void closeImpl() {

        if (isClosed)
            return;

        isClosing = true;
        sessionManager.removeSession(this);
        try {

            closeProcessing();
        }
        finally {

            if (channel != null) {

                try {
                    channel.close();
                } catch (final IOException e) {
                    logger.warn(String.format("Exception on close at session: %s%n", this.toString()), e);
                }
            }
            isClosed = true;
        }
    }

    @Override
    public boolean isStopping() {

        return isClosing;
    }

    @Override
    public void setStopping() {

        if (!isClosing) {

            isClosing = true;
            futureClose = submitCloseMessage();
        }
    }

    @Override
    public void setStopped() {

    }

    @Override
    public boolean waitStopped(int timeout) {

        if (isClosed)
            return true;

        if (!isClosing)
            return false;

        Future<Boolean> futureClose = this.futureClose;
        if (futureClose != null) {

            try {

                if (timeout < 0) {

                    if (futureClose.get())
                        return true;
                } else {

                    if (futureClose.get(timeout, TimeUnit.MILLISECONDS))
                        return true;
                }
            } catch (final Exception e) {

                logger.warn(String.format("Exception on waiting for close at session: %s%n", this.toString()), e);
            }
        }

        return isClosed;
    }

    private Future<Boolean> submitCloseMessage() {

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (isClosed) {

            future.complete(true);
        }
        else {

            submitMessage(new SessionEventMessage(getId(), SessionEvent.Close), new CompletionHandler<MessageProcessingAction, MessageProcessingState>() {

                @Override
                public void completed(MessageProcessingAction result, MessageProcessingState attachment) {

                    processingCompletionHandler.completed(result, attachment);
                    SleepBeforeClose();

                    writeBuffers(new ByteBuffer[] { null }, new CompletionHandler<Integer, Session>() {

                        @Override
                        public void completed(Integer result, Session attachment) {

                            writeCompletionHandler.completed(result, attachment);
                            future.complete(isClosed);
                        }

                        @Override
                        public void failed(Throwable exc, Session attachment) {

                            writeCompletionHandler.failed(exc, attachment);
                            future.complete(isClosed);
                        }
                    });
                }

                @Override
                public void failed(Throwable exc, MessageProcessingState attachment) {

                    processingCompletionHandler.failed(exc, attachment);
                    future.complete(isClosed);
                }
            });
        }

        return future;
    }

    private class MessageProcessingCompletionHandler implements CompletionHandler<MessageProcessingAction, MessageProcessingState> {

        @Override
        public void completed(MessageProcessingAction result, MessageProcessingState attachment) {

            switch (result) {
                case Done:
                    handleOutputMessage(attachment);
                    break;
            }
        }

        @Override
        public void failed(Throwable exc, MessageProcessingState attachment) {

            logger.error(String.format("Exception on processing at session: %s%n", this.toString()), exc);
        }
    }

    private static boolean IsDisconnectException(Throwable exc) {

        if (exc instanceof IOException) {

            final String errorMessage = exc.getMessage();
            if (errorMessage != null) {

                return errorMessage.startsWith("The specified network name is no longer available") ||
                        errorMessage.startsWith("An existing connection was forcibly closed by the remote host");
            }
        }

        return false;
    }


    private final class ReadCompletionHandler implements CompletionHandler<Integer, Session> {

        @Override
        public void completed(Integer result, Session attachment) {

            ((BaseAsyncSession)attachment).readCompleted(result);
        }

        @Override
        public void failed(Throwable exc, Session attachment) {

            if (!((BaseAsyncSession)attachment).isClosing) {

                if (!IsDisconnectException(exc)) {

                    logger.error(String.format("Exception on read at session: %s, closing session%n", attachment.toString()), exc);
                }
            }
            attachment.close();
        }
    }

    private final class WriteCompletionHandler implements CompletionHandler<Integer, Session> {

        @Override
        public void completed(Integer result, Session attachment) {

            ((BaseAsyncSession)attachment).writeCompleted(result);
        }

        @Override
        public void failed(Throwable exc, Session attachment) {

            logger.error(String.format("Exception on write at session: %s, closing session%n", attachment.toString()), exc);
            attachment.close();
        }
    }
}

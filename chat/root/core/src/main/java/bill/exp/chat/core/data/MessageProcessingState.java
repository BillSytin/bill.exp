package bill.exp.chat.core.data;

import bill.exp.chat.core.io.Session;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class MessageProcessingState {
    private final Session session;
    private final MessageProcessor processor;
    private Message incomingMessage;
    private CompletionHandler<MessageProcessingAction, MessageProcessingState> finalCompletionHandler;
    private Message processingMessage;
    private ByteBuffer outputBuffer;


    public MessageProcessingState(Session session, Message incomingMessage, MessageProcessor processor) {
        this.session = session;
        this.incomingMessage = incomingMessage;
        this.processingMessage = null;
        this.processor = processor;
        this.finalCompletionHandler = null;
        this.outputBuffer = null;
    }

    public final Session getSession() { return session; }
    public final MessageProcessor getProcessor() { return processor; }

    public final Message getIncomingMessage() { return incomingMessage; }
    public void setIncomingMessage(Message value) { incomingMessage = value; }

    public final Message getProcessingMessage() { return processingMessage; }
    public void setProcessingMessage(Message value) { processingMessage = value; }

    public final CompletionHandler<MessageProcessingAction, MessageProcessingState> getFinalCompletionHandler() { return finalCompletionHandler; }
    public void setFinalCompletionHandler(CompletionHandler<MessageProcessingAction, MessageProcessingState> value) { finalCompletionHandler = value; }

    public ByteBuffer getOutputBuffer() { return outputBuffer; }
    public void setOutputBuffer(ByteBuffer value) { outputBuffer = value; }
}

package bill.exp.chat.core.data;

import bill.exp.chat.core.io.Session;

import java.nio.channels.CompletionHandler;

public class MessageProcessingState {
    private final Session session;
    private final CompletionHandler<MessageProcessingAction, MessageProcessingState> finalCompletionHandler;
    private Message inputMessage;
    private Message outputMessage;

    public MessageProcessingState(Session session, CompletionHandler<MessageProcessingAction, MessageProcessingState> finalCompletionHandler, Message inputMessage) {
        this.session = session;
        this.finalCompletionHandler = finalCompletionHandler;
        this.inputMessage = inputMessage;
        this.outputMessage = null;
    }

    public Session getSession() { return session; }

    public CompletionHandler<MessageProcessingAction, MessageProcessingState> getFinalCompletionHandler() { return finalCompletionHandler; }

    public Message getInputMessage() { return inputMessage; }
    public void setInputMessage(Message value) { inputMessage = value; }

    public Message getOutputMessage() { return outputMessage; }
    public void setOutputMessage(Message value) { outputMessage = value; }
}

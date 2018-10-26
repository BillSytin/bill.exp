package bill.exp.chat.core.data;

public interface MessageProcessingChain {
    void process(MessageProcessingState state);
}

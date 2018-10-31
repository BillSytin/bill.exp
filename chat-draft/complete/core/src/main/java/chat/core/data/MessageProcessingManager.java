package chat.core.data;

public interface MessageProcessingManager {

    MessageProcessingChain createProcessingChain();
    Iterable<MessageProcessor> getProcessors();
}

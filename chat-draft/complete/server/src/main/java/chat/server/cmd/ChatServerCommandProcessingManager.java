package chat.server.cmd;

@SuppressWarnings("unused")
public interface ChatServerCommandProcessingManager {

    Iterable<ChatServerCommandProcessor> getProcessors();
    void process(ChatServerCommandProcessingContext context);
    void dispose(ChatServerCommandProcessingContext context);
}

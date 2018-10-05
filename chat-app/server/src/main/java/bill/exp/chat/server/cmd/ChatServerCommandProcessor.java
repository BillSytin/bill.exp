package bill.exp.chat.server.cmd;

@SuppressWarnings({"EmptyMethod", "unused"})
public interface ChatServerCommandProcessor {

    void process(ChatServerCommandProcessingPhase phase, ChatServerCommandProcessingContext context);
}

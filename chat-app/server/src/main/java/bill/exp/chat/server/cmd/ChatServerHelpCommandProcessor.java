package bill.exp.chat.server.cmd;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(0)
public class ChatServerHelpCommandProcessor extends BaseChatServerCommandProcessor {

    @Override
    protected String getCommandId() {

        return "help";
    }

    @Override
    public void process(ChatServerCommandProcessingContext context) {

        if (detectProcessingCommmand(context)) {

            for (ChatServerCommandProcessor processor : context.getProcessingManager().getProcessors()) {

                processor.process(ChatServerCommandProcessingPhase.Help, context);
            }
        }
    }
}

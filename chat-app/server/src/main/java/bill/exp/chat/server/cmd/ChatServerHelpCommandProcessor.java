package bill.exp.chat.server.cmd;

import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.model.ChatStandardAction;
import bill.exp.chat.model.ChatStandardRoute;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(0)
public class ChatServerHelpCommandProcessor extends BaseChatServerCommandProcessor {

    @Override
    protected String getCommandId() {

        return ChatStandardRoute.Help.toString();
    }

    @Override
    public void process(ChatServerCommandProcessingContext context) {

        if (detectProcessingAction(context, ChatStandardAction.Welcome.toString())) {

            final String welcomeText = getMessageResource(ChatStandardAction.Welcome.toString(), context);
            final ChatMessage message = new ChatMessage();
            message.setRoute(getCommandId());
            message.setAction(ChatStandardAction.Welcome.toString());
            message.setContent(welcomeText);
            context.getOutput().getMessages().add(message);
        }

        if (detectProcessingAction(context, ChatStandardAction.Help.toString())) {

            for (final ChatServerCommandProcessor processor : context.getProcessingManager().getProcessors()) {

                processor.process(ChatServerCommandProcessingPhase.Help, context);
            }
        }
    }
}

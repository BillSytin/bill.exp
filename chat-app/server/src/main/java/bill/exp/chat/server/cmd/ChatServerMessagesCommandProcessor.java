package bill.exp.chat.server.cmd;

import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.model.ChatStandardAction;
import bill.exp.chat.model.ChatStandardRoute;
import bill.exp.chat.model.ChatStandardStatus;
import bill.exp.chat.server.msg.ChatServerMessageRecord;
import bill.exp.chat.server.msg.ChatServerMessagesRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@SuppressWarnings("unused")
@Component
@Order(100)
public class ChatServerMessagesCommandProcessor extends BaseChatServerCommandProcessor {

    private final ObjectFactory<ChatServerMessagesRepository> messagesRepositoryObjectFactory;

    @Autowired
    public ChatServerMessagesCommandProcessor(ObjectFactory<ChatServerMessagesRepository> messagesRepositoryObjectFactory) {

        this.messagesRepositoryObjectFactory = messagesRepositoryObjectFactory;
    }

    private ChatServerMessagesRepository getMessagesRepository() {

        return messagesRepositoryObjectFactory.getObject();
    }

    @Override
    protected String getCommandId() {

        return ChatStandardRoute.Message.toString();
    }

    @Override
    protected void process(ChatServerCommandProcessingContext context) {

        if (detectProcessingAction(context, ChatStandardAction.None.toString()) ||
                (context.getProcessingMessage() != null &&
                        StringUtils.isEmpty(context.getProcessingMessage().getRoute()) &&
                        StringUtils.isEmpty(context.getProcessingMessage().getAction()) &&
                        StringUtils.hasLength(context.getProcessingMessage().getContent())
                )
        ) {

            if (context.getUser() != null && context.getUser().isAuthenticated()) {

                final ChatServerMessageRecord record = new ChatServerMessageRecord();
                record.setSessionId(context.getSession().getId());
                record.setMessage(context.getProcessingMessage());
                record.setUser(context.getUser().toModel());
                getMessagesRepository().put(record);

                final ChatMessage message = new ChatMessage();
                message.setRoute(getCommandId());
                message.setAction(ChatStandardAction.None.toString());
                message.setStatus(ChatStandardStatus.Success.toString());
                message.setContent(Long.toString(record.getStamp()));
                context.getOutput().getMessages().add(message);
            }
            else {

                final ChatMessage errorMessage = ChatMessage.createErrorMessage("Not logged in", ChatStandardAction.Login.toString());
                context.getOutput().getMessages().add(errorMessage);
                context.setCompleted();
            }

        }
    }
}

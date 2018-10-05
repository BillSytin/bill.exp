package bill.exp.chat.server.cmd;

import bill.exp.chat.model.ChatMessage;
import bill.exp.chat.server.msg.ChatServerMessageRecord;
import bill.exp.chat.server.msg.ChatServerMessagesRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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

        return "message";
    }

    @Override
    protected void process(ChatServerCommandProcessingContext context) {

        if (detectProcessingCommmand(context)) {

            if (context.getUser() != null && context.getUser().isAuthenticated()) {

                ChatServerMessageRecord record = new ChatServerMessageRecord();
                record.setSessionId(context.getSession().getId());
                record.setMessage(context.getProcessingMessage());
                record.setUser(context.getUser().toModel());
                getMessagesRepository().put(record);
            }
            else {

                final ChatMessage errorMessage = ChatMessage.createErrorMessage("Not logged in", "auth");
                context.getOutput().getMessages().add(errorMessage);
                context.setCompleted();
            }

        }
    }
}

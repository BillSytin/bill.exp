package chat.server.cmd;

import chat.core.model.ChatMessage;
import chat.core.model.ChatStandardAction;
import chat.core.model.ChatStandardRoute;
import chat.core.model.ChatStandardStatus;
import chat.server.msg.ChatServerMessageRecord;
import chat.server.msg.ChatServerMessagesRepository;
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

        return ChatStandardRoute.Message.toString();
    }

    @Override
    protected void process(ChatServerCommandProcessingContext context) {

        if (detectProcessingAction(context, ChatStandardAction.Notify.toString())) {

            if (context.getUser() != null && context.getUser().isLoggedIn()) {

                final ChatMessage message = new ChatMessage();
                message.setRoute(getCommandId());
                message.setStandardAction(ChatStandardAction.Notify);
                message.setContent(context.getProcessingMessage().getContent());
                context.getOutput().getMessages().add(message);
            }
        }
        else if (detectProcessingAction(context, ChatStandardAction.Fetch.toString())) {

            if (context.getUser() != null && context.getUser().isAuthenticated()) {

                long stamp;
                try {
                    stamp = Long.parseLong(context.getProcessingMessage().getContent());
                }
                catch (final NumberFormatException e) {
                    stamp = -1;
                }

                if (stamp >= 0) {

                    final long thisSessionId = context.getSession().getId();
                    for (final ChatServerMessageRecord record : getMessagesRepository().getAllSince(stamp)) {

                        if (record.getSessionId() != thisSessionId) {

                            final ChatMessage message = new ChatMessage();
                            message.setRoute(getCommandId());
                            message.setStandardAction(ChatStandardAction.Fetch);
                            message.setStatus(Long.toString(record.getStamp()));
                            message.setAuthor(record.getUser());
                            message.setContent(record.getMessage().getContent());
                            context.getOutput().getMessages().add(message);
                        }
                    }
                }
            }
        }
        else if (detectProcessingAction(context, ChatStandardAction.None.toString()) || detectDefaultAction(context)) {

            if (context.getUser() != null && context.getUser().isAuthenticated()) {

                final long thisSessionId = context.getSession().getId();
                final ChatServerMessageRecord record = new ChatServerMessageRecord();
                record.setSessionId(thisSessionId);
                record.setMessage(context.getProcessingMessage());
                record.setUser(context.getUser().toModel());
                getMessagesRepository().put(record);

                final ChatMessage message = new ChatMessage();
                message.setRoute(getCommandId());
                message.setStandardAction(ChatStandardAction.None);
                message.setStandardStatus(ChatStandardStatus.Success);
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

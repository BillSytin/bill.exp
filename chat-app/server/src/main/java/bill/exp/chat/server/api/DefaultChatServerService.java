package bill.exp.chat.server.api;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatClientEnvelope;
import bill.exp.chat.server.cmd.ChatServerCommandProcessingContext;
import bill.exp.chat.server.cmd.ChatServerCommandProcessingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@Service
@Scope("prototype")
public class DefaultChatServerService implements ChatServerService {

    private final ChatServerCommandProcessingManager commandProcessingManager;

    @Autowired
    public DefaultChatServerService(ChatServerCommandProcessingManager commandProcessingManager) {

        this.commandProcessingManager = commandProcessingManager;
    }

    @Override
    public ChatServerResponseIntent process(Session session, ChatServerRequestIntent intent, ChatClientEnvelope model) {

        ChatBaseAction action = ChatBaseAction.Process;
        switch (intent.getAction()) {

            case Process:
                if (model != null && model.getAction() == ChatBaseAction.CloseSession) {

                    action = ChatBaseAction.CloseSession;
                }
                break;
        }

        final ChatServerCommandProcessingContext context = new ChatServerCommandProcessingContext(session, intent.getAction(), model);
        commandProcessingManager.process(context);

        return new ChatServerResponseIntent(action, context.getOutput());
    }

    @Override
    public boolean isAsyncIntent(ChatServerRequestIntent intent) {

        return intent.getAction() == ChatBaseAction.Process;
    }

    @Override
    public void dispose(Session session) {

        final ChatServerCommandProcessingContext context = new ChatServerCommandProcessingContext(session, ChatBaseAction.Unknown, null);
        commandProcessingManager.dispose(context);
    }
}

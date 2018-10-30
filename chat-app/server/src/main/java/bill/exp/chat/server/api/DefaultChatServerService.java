package bill.exp.chat.server.api;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.model.ChatAction;
import bill.exp.chat.core.model.ChatClientEnvelope;
import bill.exp.chat.core.model.ChatServerEnvelope;
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
    public ChatServerResponseIntent process(Session session, ChatServerRequestIntent intent, ChatClientEnvelope[] model) {

        ChatAction action = ChatAction.Process;
        switch (intent.getAction()) {

            case Process:
                if (model != null && model.length > 0 && model[model.length - 1].getAction() == ChatAction.CloseSession) {

                    action = ChatAction.CloseSession;
                }
                break;
        }

        final ChatServerEnvelope[] outputs = new ChatServerEnvelope[model != null ? model.length : 1];

        for (int i = 0; i < outputs.length; i++) {

            final ChatServerCommandProcessingContext context = new ChatServerCommandProcessingContext(
                    session,
                    intent.getAction(),
                    model != null ? model[i] : null);

            commandProcessingManager.process(context);
            outputs[i] = context.getOutput();
        }

        return new ChatServerResponseIntent(action, outputs);
    }

    @Override
    public boolean isAsyncIntent(ChatServerRequestIntent intent) {

        return intent.getAction() == ChatAction.Process;
    }

    @Override
    public void dispose(Session session) {

        final ChatServerCommandProcessingContext context = new ChatServerCommandProcessingContext(session, ChatAction.Unknown, null);
        commandProcessingManager.dispose(context);
    }
}

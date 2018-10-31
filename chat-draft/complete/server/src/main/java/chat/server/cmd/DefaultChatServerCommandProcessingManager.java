package chat.server.cmd;

import chat.core.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@SuppressWarnings("unused")
@Component
@Scope("prototype")
public class DefaultChatServerCommandProcessingManager implements ChatServerCommandProcessingManager {

    private final ChatServerCommandProcessor[] processors;

    @Autowired
    public DefaultChatServerCommandProcessingManager(ChatServerCommandProcessor[] processors) {

        this.processors = processors;
    }

    @Override
    public Iterable<ChatServerCommandProcessor> getProcessors() {

        return Arrays.asList(processors);
    }

    @Override
    public void process(ChatServerCommandProcessingContext context) {

        context.setProcessingManager(this);

        for (ChatServerCommandProcessor processor : processors) {

            processor.process(ChatServerCommandProcessingPhase.Preprocess, context);
            if (context.isCompleted())
                return;
        }

        if (context.getInput() != null && context.getInput().getMessages() != null) {

            for (ChatMessage message : context.getInput().getMessages()) {

                context.setProcessingMessage(message);
                for (ChatServerCommandProcessor processor : processors) {

                    processor.process(ChatServerCommandProcessingPhase.Process, context);
                    if (context.isCompleted())
                        return;
                }
            }
        }
    }

    @Override
    public void dispose(ChatServerCommandProcessingContext context) {

        context.setProcessingManager(this);

        for (ChatServerCommandProcessor processor : processors) {

            processor.process(ChatServerCommandProcessingPhase.Dispose, context);
        }
    }
}

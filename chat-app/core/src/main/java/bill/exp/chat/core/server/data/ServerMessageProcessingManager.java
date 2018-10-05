package bill.exp.chat.core.server.data;

import bill.exp.chat.core.data.BaseMessageProcessingManager;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Qualifier("serverMessageProcessingManager")
@Scope("prototype")
public class ServerMessageProcessingManager extends BaseMessageProcessingManager {

    private final ServerMessageProcessor[] processors;

    @Autowired
    public ServerMessageProcessingManager(ServerMessageProcessor[] processors) {
        this.processors = processors;
    }

    @Override
    protected MessageProcessor[] getProcessors() {

        return processors;
    }
}

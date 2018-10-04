package bill.exp.chat.server.data;

import bill.exp.chat.core.data.BaseMessageProcessingManager;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("serverMessageProcessingManager")
public class ServerMessageProcessingManager extends BaseMessageProcessingManager {

    private final ServerMessageProcessorFactory[] processors;

    @Autowired
    public ServerMessageProcessingManager(ServerMessageProcessorFactory[] processors) {
        this.processors = processors;
    }

    @Override
    protected MessageProcessor[] getProcessors() {

        final MessageProcessor[] result = new MessageProcessor[processors.length];
        for (int i = 0; i < result.length; i++)
            result[i] = processors[i].getProcessor();

        return result;
    }
}

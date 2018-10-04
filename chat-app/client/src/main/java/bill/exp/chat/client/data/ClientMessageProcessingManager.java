package bill.exp.chat.client.data;

import bill.exp.chat.core.data.BaseMessageProcessingManager;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("clientMessageProcessingManager")
public class ClientMessageProcessingManager extends BaseMessageProcessingManager {

    private final ClientMessageProcessorFactory[] processors;

    @Autowired
    public ClientMessageProcessingManager(ClientMessageProcessorFactory[] processors) {

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

package bill.exp.chat.core.client.data;

import bill.exp.chat.core.data.BaseMessageProcessingManager;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Qualifier("clientMessageProcessingManager")
@Scope("prototype")
public class ClientMessageProcessingManager extends BaseMessageProcessingManager {

    private final ClientMessageProcessor[] processors;

    @Autowired
    public ClientMessageProcessingManager(ClientMessageProcessor[] processors) {

        this.processors = processors;
    }

    @Override
    protected MessageProcessor[] getProcessors() {

        return processors;
    }
}

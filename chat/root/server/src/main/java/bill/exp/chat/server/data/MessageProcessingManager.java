package bill.exp.chat.server.data;

import bill.exp.chat.core.data.BaseMessageProcessingManager;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessingManager extends BaseMessageProcessingManager {

    @Autowired
    private MessageProcessor[] processors;

    @Override
    protected MessageProcessor[] getProcessors() {

        return processors;
    }
}

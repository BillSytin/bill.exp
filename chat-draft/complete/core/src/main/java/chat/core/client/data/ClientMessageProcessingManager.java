package chat.core.client.data;

import chat.core.data.BaseMessageProcessingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Qualifier("clientMessageProcessingManager")
@Scope("prototype")
public class ClientMessageProcessingManager extends BaseMessageProcessingManager {

    @Autowired
    public ClientMessageProcessingManager(ClientMessageProcessor[] processors) {

        super(processors);
    }
}

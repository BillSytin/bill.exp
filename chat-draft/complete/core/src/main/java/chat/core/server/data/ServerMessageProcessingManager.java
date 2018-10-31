package chat.core.server.data;

import chat.core.data.BaseMessageProcessingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Qualifier("serverMessageProcessingManager")
@Scope("prototype")
public class ServerMessageProcessingManager extends BaseMessageProcessingManager {

    @Autowired
    public ServerMessageProcessingManager(ServerMessageProcessor[] processors) {

        super(processors);
    }
}

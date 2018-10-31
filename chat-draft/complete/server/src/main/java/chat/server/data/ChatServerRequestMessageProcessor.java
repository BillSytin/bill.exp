package chat.server.data;

import chat.core.api.RequestHandler;
import chat.core.data.BaseRequestMessageProcessor;
import chat.core.server.data.ServerMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseRequestMessageProcessor.Order)
@Scope("prototype")
public class ChatServerRequestMessageProcessor extends BaseRequestMessageProcessor implements ServerMessageProcessor {

    @Autowired
    public ChatServerRequestMessageProcessor(
            @Qualifier("chatServerRequestHandler") RequestHandler handler,
            @Qualifier("serverPoolHandlerExecutor") TaskExecutor requestTaskExecutor
    ) {

        super(handler, requestTaskExecutor);
    }
}

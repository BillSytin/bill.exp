package chat.client.data;

import chat.core.api.RequestHandler;
import chat.core.client.data.ClientMessageProcessor;
import chat.core.data.BaseRequestMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseRequestMessageProcessor.Order)
@Scope("prototype")
public class ChatClientRequestMessageProcessor extends BaseRequestMessageProcessor implements ClientMessageProcessor {

    @Autowired
    public ChatClientRequestMessageProcessor(@Qualifier("chatClientRequestHandler") RequestHandler handler) {

        super(handler);
    }
}

package bill.exp.chat.client.data;

import bill.exp.chat.core.client.data.ClientMessageProcessor;
import bill.exp.chat.core.data.BaseRequestMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import bill.exp.chat.core.api.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseRequestMessageProcessor.Order)
public class ChatClientRequestMessageProcessor extends BaseRequestMessageProcessor implements ClientMessageProcessor {

    @Autowired
    public ChatClientRequestMessageProcessor(@Qualifier("chatClientRequestHandler") RequestHandler handler) {

        super(handler);
    }
}

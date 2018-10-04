package bill.exp.chat.server.data;

import bill.exp.chat.core.data.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1000)
public class ServerEncodeOutputMessageProcessorFactory extends BaseEncodeOutputMessageProcessor implements ServerMessageProcessorFactory {

    @Override
    public MessageProcessor getProcessor() {
        return this;
    }
}

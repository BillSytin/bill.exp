package bill.exp.chat.client.data;

import bill.exp.chat.core.data.BaseJoinBuffersMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(-10)
public class ClientJoinBuffersMessageProcessorFactory extends BaseJoinBuffersMessageProcessor implements ClientMessageProcessorFactory {

    @Override
    public MessageProcessor getProcessor() {
        return this;
    }
}

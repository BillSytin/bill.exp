package bill.exp.chat.client.data;

import bill.exp.chat.core.data.BaseEncodeOutputMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1000)
public class ClientEncodeOutputMessageProcessorFactory extends BaseEncodeOutputMessageProcessor implements ClientMessageProcessorFactory {

    @Override
    public MessageProcessor getProcessor() {
        return this;
    }
}

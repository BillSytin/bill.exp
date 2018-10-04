package bill.exp.chat.client.data;

import bill.exp.chat.core.data.BaseDecodeInputMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class ClientDecodeInputMessageProcessorFactory extends BaseDecodeInputMessageProcessor implements ClientMessageProcessorFactory {

    @Override
    public MessageProcessor getProcessor() {
        return this;
    }
}

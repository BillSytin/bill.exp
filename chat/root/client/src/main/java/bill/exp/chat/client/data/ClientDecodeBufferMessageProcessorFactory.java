package bill.exp.chat.client.data;

import bill.exp.chat.core.data.BaseDecodeBufferMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class ClientDecodeBufferMessageProcessorFactory extends BaseDecodeBufferMessageProcessor implements ClientMessageProcessorFactory {

    @Override
    public MessageProcessor getProcessor() {
        return this;
    }
}

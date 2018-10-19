package bill.exp.chat.core.client.data;

import bill.exp.chat.core.data.BaseDecodeInputMessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseDecodeInputMessageProcessor.Order)
public class ClientDecodeInputMessageProcessor extends BaseDecodeInputMessageProcessor implements ClientMessageProcessor {

}

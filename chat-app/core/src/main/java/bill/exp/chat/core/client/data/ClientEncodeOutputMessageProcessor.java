package bill.exp.chat.core.client.data;

import bill.exp.chat.core.data.BaseEncodeOutputMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseEncodeOutputMessageProcessor.Order)
public class ClientEncodeOutputMessageProcessor extends BaseEncodeOutputMessageProcessor implements ClientMessageProcessor {

}

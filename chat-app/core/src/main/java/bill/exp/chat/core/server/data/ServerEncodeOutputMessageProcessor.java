package bill.exp.chat.core.server.data;

import bill.exp.chat.core.data.BaseEncodeOutputMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseEncodeOutputMessageProcessor.Order)
public class ServerEncodeOutputMessageProcessor extends BaseEncodeOutputMessageProcessor implements ServerMessageProcessor {

}

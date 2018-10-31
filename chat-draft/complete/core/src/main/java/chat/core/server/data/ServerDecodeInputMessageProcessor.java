package chat.core.server.data;

import chat.core.data.BaseDecodeInputMessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseDecodeInputMessageProcessor.Order)
public class ServerDecodeInputMessageProcessor extends BaseDecodeInputMessageProcessor implements ServerMessageProcessor {

}

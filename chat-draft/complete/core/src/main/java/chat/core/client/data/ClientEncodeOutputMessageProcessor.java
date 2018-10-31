package chat.core.client.data;

import chat.core.data.BaseEncodeOutputMessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseEncodeOutputMessageProcessor.Order)
public class ClientEncodeOutputMessageProcessor extends BaseEncodeOutputMessageProcessor implements ClientMessageProcessor {

}

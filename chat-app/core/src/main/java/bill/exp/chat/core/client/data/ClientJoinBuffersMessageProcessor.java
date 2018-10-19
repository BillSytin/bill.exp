package bill.exp.chat.core.client.data;

import bill.exp.chat.core.data.BaseJoinBuffersMessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseJoinBuffersMessageProcessor.Order)
public class ClientJoinBuffersMessageProcessor extends BaseJoinBuffersMessageProcessor implements ClientMessageProcessor {

}

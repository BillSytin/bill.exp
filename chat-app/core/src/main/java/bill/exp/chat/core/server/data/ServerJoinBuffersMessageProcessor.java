package bill.exp.chat.core.server.data;

import bill.exp.chat.core.data.BaseJoinBuffersMessageProcessor;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseJoinBuffersMessageProcessor.Order)
public class ServerJoinBuffersMessageProcessor extends BaseJoinBuffersMessageProcessor implements ServerMessageProcessor {

}

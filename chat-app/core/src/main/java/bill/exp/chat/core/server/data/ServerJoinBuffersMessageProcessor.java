package bill.exp.chat.core.server.data;

import bill.exp.chat.core.data.BaseJoinBuffersMessageProcessor;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseJoinBuffersMessageProcessor.Order)
@Scope("prototype")
public class ServerJoinBuffersMessageProcessor extends BaseJoinBuffersMessageProcessor implements ServerMessageProcessor {

}

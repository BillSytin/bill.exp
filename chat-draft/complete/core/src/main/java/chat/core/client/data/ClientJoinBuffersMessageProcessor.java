package chat.core.client.data;

import chat.core.data.BaseJoinBuffersMessageProcessor;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Order(BaseJoinBuffersMessageProcessor.Order)
@Scope("prototype")
public class ClientJoinBuffersMessageProcessor extends BaseJoinBuffersMessageProcessor implements ClientMessageProcessor {

}

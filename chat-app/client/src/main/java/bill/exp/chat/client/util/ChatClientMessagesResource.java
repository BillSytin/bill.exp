package bill.exp.chat.client.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Qualifier("chatClientMessagesResource")
public class ChatClientMessagesResource extends ResourceBundleMessageSource {

    public ChatClientMessagesResource() {

        setBasename("clientMessages");
    }
}

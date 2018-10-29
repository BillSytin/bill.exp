package bill.exp.chat.server.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@Qualifier("chatServerMessagesResource")
public class ChatServerMessagesResource extends ResourceBundleMessageSource {

    public ChatServerMessagesResource() {

        setBasename("serverMessages");
    }
}

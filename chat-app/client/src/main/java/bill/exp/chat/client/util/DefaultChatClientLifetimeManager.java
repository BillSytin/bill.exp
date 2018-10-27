package bill.exp.chat.client.util;

import bill.exp.chat.core.util.SimpleLifetimeManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("chatClientLifetimeManager")
@Scope("prototype")
public class DefaultChatClientLifetimeManager extends SimpleLifetimeManager {
}

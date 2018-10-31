package chat.client.util;

import chat.core.util.SimpleLifetimeManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("chatClientLifetimeManager")
@Scope("prototype")
public class DefaultChatClientLifetimeManager extends SimpleLifetimeManager {
}

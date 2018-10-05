package bill.exp.chat.client.api;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatServerEnvelope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@Service
public class DefaultChatClientService implements ChatClientService {

    private final Log logger = LogFactory.getLog(getClass());

    private Log getLogger() {

        return logger;
    }

    @Override
    public ChatClientResponseIntent process(Session session, ChatClientRequestIntent intent, ChatServerEnvelope model) {

        return null;
    }

    @Override
    public boolean isAsyncIntent(ChatClientRequestIntent intent) {

        return intent.getAction() == ChatBaseAction.Process;
    }

}

package bill.exp.chat.server.api;

import bill.exp.chat.core.api.*;
import bill.exp.chat.core.data.SessionEvent;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatClientEnvelope;
import bill.exp.chat.model.ModelConvert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@SuppressWarnings("unused")
@Component
public class DefaultChatServerModelConverter implements ChatServerModelConverter {

    private final Log logger = LogFactory.getLog(getClass());

    private Log getLogger() {

        return logger;
    }

    @Override
    public ChatServerRequestIntent convertRequestToIntent(Request request) {

        ChatBaseAction action = ChatBaseAction.Unknown;
        String content = null;

        if (request instanceof SessionEventRequest) {

            switch (((SessionEventRequest) request).getEvent()) {
                case Open:
                    action = ChatBaseAction.OpenSession;
                    break;
                case Close:
                    action = ChatBaseAction.CloseSession;
                    break;
            }
        }
        else {

            content = request.getContent();
            if (content != null && content.length() > 0) {

                action = ChatBaseAction.Process;
            }
        }

        return new ChatServerRequestIntent(action, content);
    }

    @Override
    public ChatClientEnvelope convertIntentToModel(ChatServerRequestIntent intent) {

        ChatClientEnvelope model = null;

        final String content = intent.getContent();
        if (content != null && content.length() > 0) {

            try {

                model = ModelConvert.deserialize(content, ChatClientEnvelope.class);
            } catch (final Exception e) {

                getLogger().error("Unexpected api deserialization error%n", e);
            }
        }
        else {

            model = new ChatClientEnvelope();
        }

        return model;
    }

    @Override
    public Response convertIntentToResponse(ChatServerResponseIntent intent) {

        if (intent.getContent() != null) {

            String responseString = null;
            try {

                responseString = ModelConvert.serialize(intent.getContent());

            } catch (final IOException e) {

                getLogger().error("Unexpected api serialization error%n", e);
            }

            if (responseString != null)
                return new SimpleResponse(responseString);
        }
        else {

            if (intent.getAction() == ChatBaseAction.CloseSession) {

                return new SessionEventResponse(0, SessionEvent.Close);
            }
        }

        return null;
    }
}

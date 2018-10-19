package bill.exp.chat.server.api;

import bill.exp.chat.core.api.*;
import bill.exp.chat.core.data.SessionEvent;
import bill.exp.chat.model.ChatAction;
import bill.exp.chat.model.ChatClientEnvelope;
import bill.exp.chat.model.ChatServerEnvelope;
import bill.exp.chat.model.ModelConvert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

        ChatAction action = ChatAction.Unknown;
        String[] content = null;

        if (request instanceof SessionEventRequest) {

            switch (((SessionEventRequest) request).getEvent()) {
                case Open:
                    action = ChatAction.OpenSession;
                    break;
                case Close:
                    action = ChatAction.CloseSession;
                    break;
            }
        }
        else {

            content = request.getContent();
            if (content != null && content.length > 0) {

                action = ChatAction.Process;
            }
        }

        return new ChatServerRequestIntent(action, content);
    }

    @Override
    public ChatClientEnvelope[] convertIntentToModels(ChatServerRequestIntent intent) {

        ChatClientEnvelope[] model = null;

        final String[] content = intent.getContent();
        if (content != null && content.length > 0) {

            int iter = 0;
            for (final String s : content) {
                if (StringUtils.hasLength(s))
                    iter++;
            }

            if (iter > 0) {

                model = new ChatClientEnvelope[iter];
                iter = 0;
                for (final String s : content) {

                    if (StringUtils.hasLength(s)) {

                        try {

                            model[iter] = ModelConvert.deserialize(s, ChatClientEnvelope.class);
                        } catch (final Exception e) {

                            getLogger().error("Unexpected api deserialization error%n", e);
                        }
                        iter++;
                    }
                }
            }
        }

        return model;
    }

    @Override
    public Response convertIntentToResponse(ChatServerResponseIntent intent) {

        final ChatServerEnvelope[] content = intent.getContent();
        if (content != null && content.length > 0) {

            final String[] responseStrings = new String[content.length];
            for (int i = 0; i < content.length; i++) {

                try {

                    responseStrings[i] = ModelConvert.serialize(content[i]);

                } catch (final IOException e) {

                    getLogger().error("Unexpected api serialization error%n", e);
                }
            }

            return new SimpleResponse(responseStrings);
        }
        else {

            if (intent.getAction() == ChatAction.CloseSession) {

                return new SessionEventResponse(0, SessionEvent.Close);
            }
        }

        return null;
    }
}

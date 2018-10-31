package chat.server.api;

import chat.core.api.*;
import chat.core.io.Session;
import chat.core.model.ChatAction;
import chat.core.model.ChatClientEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.channels.CompletionHandler;

@SuppressWarnings("unused")
@Component("chatServerRequestHandler")
@Scope("prototype")
public class ChatServerRequestHandler implements RequestHandler {

    private final ChatServerService service;
    private final ChatServerModelConverter converter;

    @Autowired
    public ChatServerRequestHandler(ChatServerService service, ChatServerModelConverter converter) {

        this.service = service;
        this.converter = converter;
    }

    @Override
    public RequestIntent detectRequestIntent(Request request) {

        return converter.convertRequestToIntent(request);
    }

    @Override
    public boolean isAsync(RequestIntent requestIntent) {

        return service.isAsyncIntent((ChatServerRequestIntent) requestIntent);
    }

    @Override
    public void handle(RequestHandlingContext context, Object attachment, CompletionHandler<RequestHandlingContext, Object> completionHandler) {

        final ChatServerRequestIntent intent = (ChatServerRequestIntent) (context.getRequestIntent());

        if (intent != null && intent.getAction() != ChatAction.Unknown) {

            final ChatClientEnvelope[] models = converter.convertIntentToModels(intent);
            final ChatServerResponseIntent responseIntent = service.process(context.getSession(), intent, models);
            if (responseIntent != null) {

                context.setResponseIntent(responseIntent);
            }
        }

        completionHandler.completed(context, attachment);
    }

    @Override
    public Response generateResponse(ResponseIntent responseIntent) {

        final ChatServerResponseIntent intent = (ChatServerResponseIntent) responseIntent;

        return converter.convertIntentToResponse(intent);
    }

    @Override
    public void dispose(Session session) {

        service.dispose(session);
    }
}

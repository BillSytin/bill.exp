package bill.exp.chat.client.api;

import bill.exp.chat.core.api.*;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatServerEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.CompletionHandler;

@SuppressWarnings("unused")
@Component("chatClientRequestHandler")
public class ChatClientRequestHandler implements RequestHandler {

    private final ChatClientService service;
    private final ChatClientModelConverter converter;

    @Autowired
    public ChatClientRequestHandler(ChatClientService service, ChatClientModelConverter converter) {

        this.service = service;
        this.converter = converter;
    }

    @Override
    public RequestIntent detectRequestIntent(Request request) {

        return converter.convertRequestToIntent(request);
    }

    @Override
    public boolean isAsync(RequestIntent requestIntent) {

        return service.isAsyncIntent((ChatClientRequestIntent) requestIntent);
    }

    @Override
    public void handle(RequestHandlingContext context, Object attachment, CompletionHandler<RequestHandlingContext, Object> completionHandler) {

        final ChatClientRequestIntent intent = (ChatClientRequestIntent)(context.getRequestIntent());

        if (intent != null && intent.getAction() != ChatBaseAction.Unknown) {

            final ChatServerEnvelope model = converter.convertIntentToModel(intent);
            final ChatClientResponseIntent responseIntent = service.process(context.getSession(), intent, model);
            if (responseIntent != null) {

                context.setResponseIntent(responseIntent);
            }
        }

        completionHandler.completed(context, attachment);
    }

    @Override
    public Response generateResponse(ResponseIntent responseIntent) {

        final ChatClientResponseIntent intent = (ChatClientResponseIntent)responseIntent;
        return converter.convertIntentToResponse(intent);
    }

    @Override
    public void dispose(Session session) {

    }
}

package chat.core.api;

import chat.core.io.Session;

import java.nio.channels.CompletionHandler;

public interface RequestHandler {

    RequestIntent detectRequestIntent(Request request);
    boolean isAsync(RequestIntent requestIntent);
    void handle(RequestHandlingContext context, Object attachment, CompletionHandler<RequestHandlingContext, Object> completionHandler);
    Response generateResponse(ResponseIntent responseIntent);
    void dispose(Session session);
}

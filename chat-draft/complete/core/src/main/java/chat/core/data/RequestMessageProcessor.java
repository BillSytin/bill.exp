package chat.core.data;

import chat.core.api.RequestHandler;

public interface RequestMessageProcessor extends MessageProcessor {

    RequestHandler getRequestHandler();
}

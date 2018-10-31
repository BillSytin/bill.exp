package chat.client.api;

import chat.core.api.RequestHandler;

public interface ChatClientRequestHandler extends RequestHandler {

    ChatClientService getService();
}

package bill.exp.chat.client.api;

import bill.exp.chat.core.api.RequestHandler;

public interface ChatClientRequestHandler extends RequestHandler {

    ChatClientService getService();
}

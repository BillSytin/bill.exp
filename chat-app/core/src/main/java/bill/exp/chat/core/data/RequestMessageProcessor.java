package bill.exp.chat.core.data;

import bill.exp.chat.core.api.RequestHandler;

public interface RequestMessageProcessor extends MessageProcessor {

    RequestHandler getRequestHandler();
}

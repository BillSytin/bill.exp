package bill.exp.chat.server.data;

import bill.exp.chat.core.data.MessageProcessor;

public interface ServerMessageProcessorFactory {
    MessageProcessor getProcessor();
}

package bill.exp.chat.client.data;

import bill.exp.chat.core.data.MessageProcessor;

public interface ClientMessageProcessorFactory {
    MessageProcessor getProcessor();
}

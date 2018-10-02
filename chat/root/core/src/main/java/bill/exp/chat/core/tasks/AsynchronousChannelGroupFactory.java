package bill.exp.chat.core.tasks;

import java.nio.channels.AsynchronousChannelGroup;

public interface AsynchronousChannelGroupFactory {
    AsynchronousChannelGroup getInstance();
}

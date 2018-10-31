package chat.core.io;

import java.nio.channels.AsynchronousSocketChannel;

public interface AsyncSession {

    void open(AsynchronousSocketChannel channel);
}

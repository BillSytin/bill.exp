package bill.exp.chat.core.io;

import java.nio.channels.AsynchronousSocketChannel;

public interface AsyncSession extends Session {
    void open(AsynchronousSocketChannel channel);
    void close();
}

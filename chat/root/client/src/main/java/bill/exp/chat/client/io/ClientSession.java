package bill.exp.chat.client.io;

import bill.exp.chat.core.io.AsyncSession;

import java.nio.ByteBuffer;

public interface ClientSession extends AsyncSession {
    void write(ByteBuffer output);
}

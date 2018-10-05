package bill.exp.chat.core.data;

import java.nio.ByteBuffer;

public class ByteBufferMessage implements Message {
    private final ByteBuffer content;
    private final boolean isIncomplete;

    public ByteBufferMessage(ByteBuffer content, boolean isIncomplete) {
        this.content = content;
        this.isIncomplete = isIncomplete;
    }

    public boolean isIncomplete() { return isIncomplete; }

    public ByteBuffer getBuffer() { return content; }
}

package bill.exp.chat.core.data;

import java.nio.ByteBuffer;

public class ByteBufferMessage implements Message {
    private final ByteBuffer[] content;

    public ByteBufferMessage(ByteBuffer content) {

        this(new ByteBuffer[] { content });
    }

    public ByteBufferMessage(ByteBuffer[] content) {
        this.content = content;
    }

    public ByteBuffer[] getBuffers() { return content; }
}

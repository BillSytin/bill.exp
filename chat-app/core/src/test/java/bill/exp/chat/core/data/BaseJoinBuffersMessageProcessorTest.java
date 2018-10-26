package bill.exp.chat.core.data;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.server.io.TcpServerConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseJoinBuffersMessageProcessorTest {

    @Autowired
    private TcpServerConfig serverConfig;

    private MessageProcessingAction completionAction;

    @Test
    public void processJoinsIncompleteBuffers() {

        final Session session = Mockito.mock(Session.class);

        final BaseJoinBuffersMessageProcessor processor = new BaseJoinBuffersMessageProcessor();

        final int bufferSize = serverConfig.getReadBufferSize();
        final MessageProcessingState state = new MessageProcessingState(session,null,null);

        final int BuffersCount = 3;
        int totalSize = 0;
        for (int bi = BuffersCount - 1; bi >= 0; bi--) {

            final StringBuilder sb = new StringBuilder();
            for (int ci = 0; ci < bufferSize / 2; ci++) {
                sb.append('б' + bi);
                sb.append('г' + bi);
            }

            final boolean isLast = bi == 0;
            final ByteBuffer[] buffers = new ByteBuffer[2];
            buffers[0] = StandardCharsets.UTF_8.encode(sb.toString());
            totalSize += buffers[0].limit();

            final ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
            final int endPosition = buffer.limit();
            totalSize += endPosition;
            if (isLast) {

                buffer.limit(endPosition + 1);
                buffer.position(endPosition);
                buffer.put((byte) 0);
            }
            buffers[1] = buffer;

            completionAction = MessageProcessingAction.Done;
            state.setInputMessage(new ByteBufferMessage(buffers));
            processor.process(state, new CompletionHandler<MessageProcessingAction, MessageProcessingState>() {
                @Override
                public void completed(MessageProcessingAction result, MessageProcessingState attachment) {

                    completionAction = result;
                }

                @Override
                public void failed(Throwable exc, MessageProcessingState attachment) {

                }
            });

            Assert.assertEquals(completionAction, isLast ? MessageProcessingAction.Next : MessageProcessingAction.Reset);
        }

        Assert.assertTrue(state.getInputMessage() instanceof ByteBufferMessage);

        final ByteBuffer[] resultBuffers = ((ByteBufferMessage)state.getInputMessage()).getBuffers();
        Assert.assertEquals(BuffersCount + 1, resultBuffers.length);

        int resultSize = 0;
        for (final ByteBuffer resultBuffer : resultBuffers) {

            resultSize += resultBuffer.limit();
        }
        Assert.assertEquals(totalSize, resultSize);
    }
}
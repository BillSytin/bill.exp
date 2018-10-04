package bill.exp.chat.client.io;

import java.nio.channels.CompletionHandler;

public interface ClientChannel {
    <A> void connect(A attachment, CompletionHandler<ClientSession, A> completionHandler);
}

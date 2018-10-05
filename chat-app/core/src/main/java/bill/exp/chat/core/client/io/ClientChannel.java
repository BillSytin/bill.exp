package bill.exp.chat.core.client.io;

import java.util.concurrent.Future;

@SuppressWarnings("UnusedReturnValue")
public interface ClientChannel {
    Future<ClientSession> connect();
}

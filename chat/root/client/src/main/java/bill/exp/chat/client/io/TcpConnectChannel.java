package bill.exp.chat.client.io;

import bill.exp.chat.core.io.AsyncSession;
import bill.exp.chat.core.io.Channel;
import bill.exp.chat.core.tasks.AsynchronousChannelGroupFactory;
import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

@Component("tcpConnectChannel")
public class TcpConnectChannel implements ClientChannel, Stoppable, DisposableBean {

    @Autowired
    private AsynchronousChannelGroupFactory groupFactory;

    @Autowired
    @Qualifier("mainLifetimeManager")
    private Stoppable lifeTimeManager;

    @Autowired
    @Qualifier("serverAddress")
    private InetSocketAddress address;

    @Autowired
    private ApplicationContext context;

    private AsynchronousChannelGroup group;
    private AsynchronousSocketChannel client;

    @Override
    public <A> void connect(A attachment, CompletionHandler<ClientSession, A> completionHandler) {

        client.connect(address, attachment, new CompletionHandler<Void, A>() {
            @Override
            public void completed(Void result, A attachment) {

                completionHandler.completed(openSession(), attachment);
            }

            @Override
            public void failed(Throwable exc, A attachment) {

                completionHandler.failed(exc, attachment);
            }
        });
    }

    @Override
    public void destroy() {

        stop();
    }

    private ClientSession openSession() {
        ClientSession session = context.getBean(ClientSession.class);
        session.open(client);
        return session;
    }

    @PostConstruct
    public void init() throws IOException {
        group = groupFactory.getInstance();
        client = AsynchronousSocketChannel.open(group);
    }

    private void stop() {

        lifeTimeManager.setIsStopping();

        if (client != null && client.isOpen()) {
            try {
                client.close();
            }
            catch (final IOException e) {
            }
        }

        if (group != null && !group.isShutdown()) {
            try {
                group.shutdownNow();

                try {
                    group.awaitTermination(10, TimeUnit.SECONDS);
                }
                catch (final InterruptedException e) {
                }
            }
            catch (final IOException e) {
            }
        }

    }

    @Override
    public boolean getIsStopping() {

        return lifeTimeManager.getIsStopping();
    }

    @Override
    public void setIsStopping() {

        stop();
    }

    @Override
    public void setIsStopped() {

        lifeTimeManager.setIsStopped();
    }

    @Override
    public boolean waitStopped(int timeout) {

        return lifeTimeManager.waitStopped(timeout);
    }
}

package bill.exp.chat.client.io;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionFactory;
import bill.exp.chat.core.tasks.AsynchronousChannelGroupFactory;
import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InvalidClassException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

@Component("tcpConnectChannel")
public class TcpConnectChannel implements ClientChannel, Stoppable, DisposableBean {

    private final Stoppable lifeTimeManager;
    private final InetSocketAddress address;
    private final SessionFactory sessionFactory;
    private final AsynchronousChannelGroup group;
    private final AsynchronousSocketChannel client;

    @Autowired
    public TcpConnectChannel(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            AsynchronousChannelGroupFactory groupFactory,
            @Qualifier("serverAddress")InetSocketAddress address,
            @Qualifier("clientSessionFactory") SessionFactory sessionFactory
    ) {
        this.lifeTimeManager = lifeTimeManager;
        this.address = address;
        this.sessionFactory = sessionFactory;
        this.group = groupFactory.getInstance();
        this.client = openClient();
    }

    private AsynchronousSocketChannel openClient() {

        try {
            return AsynchronousSocketChannel.open();
        }
        catch (final IOException e) {
            lifeTimeManager.setIsStopping();
        }
        return null;
    }

    @Override
    public <A> void connect(A attachment, CompletionHandler<ClientSession, A> completionHandler) {

        Session newSession = sessionFactory.createSession();
        if (newSession instanceof ClientSession) {

            ClientSession session = (ClientSession) newSession;
            client.connect(address, attachment, new CompletionHandler<Void, A>() {
                @Override
                public void completed(Void result, A attachment) {

                    session.open(client);
                    completionHandler.completed(session, attachment);
                }

                @Override
                public void failed(Throwable exc, A attachment) {

                    completionHandler.failed(exc, attachment);
                }
            });
        }
        else {
            completionHandler.failed(new InvalidClassException("ClientSession"), attachment);
        }
    }

    @Override
    public void destroy() {

        stop();
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

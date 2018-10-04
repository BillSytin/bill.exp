package bill.exp.chat.server.io;

import bill.exp.chat.core.io.AsyncSession;
import bill.exp.chat.core.io.Channel;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionFactory;
import bill.exp.chat.core.tasks.AsynchronousChannelGroupFactory;
import bill.exp.chat.core.util.Stoppable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InvalidClassException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

@Component("tcpAcceptChannel")
public class TcpAcceptChannel implements Channel, Stoppable, DisposableBean {

    private final Stoppable lifeTimeManager;
    private final InetSocketAddress address;
    private final SessionFactory sessionFactory;
    private final AsynchronousChannelGroup group;
    private final AsynchronousServerSocketChannel server;

    @Autowired
    public TcpAcceptChannel(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("serverPoolGroupFactory") AsynchronousChannelGroupFactory groupFactory,
            @Qualifier("serverAddress")InetSocketAddress address,
            @Qualifier("serverSessionFactory") SessionFactory sessionFactory
    ) {
        this.lifeTimeManager = lifeTimeManager;
        this.address = address;
        this.sessionFactory = sessionFactory;
        this.group = groupFactory.getInstance();
        this.server = bindServer();
    }

    @Override
    public void destroy() {

        stop();
    }

    private AsynchronousServerSocketChannel bindServer() {

        try {
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
            server.bind(address);
            return server;
        }
        catch (final IOException e) {
            lifeTimeManager.setIsStopping();
        }
        return null;
    }

    private void stop() {

        lifeTimeManager.setIsStopping();

        if (server != null && server.isOpen()) {
            try {
                server.close();
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

        lifeTimeManager.setIsStopped();
    }

    private CompletionHandler<AsynchronousSocketChannel, AsyncSession> createAcceptHandler() {

        return new CompletionHandler<AsynchronousSocketChannel, AsyncSession>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AsyncSession attachment) {

                acceptNext(this);
                attachment.open(result);
            }

            @Override
            public void failed(Throwable exc, AsyncSession attachment) {

                if (attachment != null) {
                    attachment.close();
                }
                stop();
            }
        };
    }

    private void acceptNext(CompletionHandler<AsynchronousSocketChannel, AsyncSession> handler) {

        if (lifeTimeManager.getIsStopping() || !server.isOpen()) {
            stop();
        } else {

            Session newSession = sessionFactory.createSession();
            if (newSession instanceof AsyncSession) {
                AsyncSession session = (AsyncSession) newSession;
                server.accept(session, handler);
            }
            else {
                handler.failed(new InvalidClassException("AsyncSession"), null);
            }
        }
    }

    @Override
    public void run() {

        acceptNext(createAcceptHandler());
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

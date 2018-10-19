package bill.exp.chat.core.server.io;

import bill.exp.chat.core.io.AsyncSession;
import bill.exp.chat.core.io.Channel;
import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.io.SessionFactory;
import bill.exp.chat.core.tasks.AsynchronousChannelGroupFactory;
import bill.exp.chat.core.util.Stoppable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InvalidClassException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Component("tcpAcceptChannel")
public class TcpAcceptChannel implements Channel, Stoppable, DisposableBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private final Stoppable lifeTimeManager;
    private final InetSocketAddress address;
    private final SessionFactory sessionFactory;
    private final AsynchronousChannelGroup group;
    private final AsynchronousServerSocketChannel server;

    @Autowired
    public TcpAcceptChannel(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("serverPoolGroupFactory") AsynchronousChannelGroupFactory groupFactory,
            @Qualifier("serverSessionFactory") SessionFactory sessionFactory,
            TcpAcceptConfig config
    ) {
        this.lifeTimeManager = lifeTimeManager;
        this.address = config.getAddress();
        this.sessionFactory = sessionFactory;
        this.group = groupFactory.getInstance();
        this.server = bindServer();
    }

    @Override
    public String toString() {

        return address.toString();
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

            logger.error(String.format("Error server bind to: %s%n", this.toString()), e);
            lifeTimeManager.setStopping();
        }

        return null;
    }

    private void stop() {

        lifeTimeManager.setStopping();

        if (server != null && server.isOpen()) {

            try {
                server.close();
            }
            catch (final IOException e) {
                logger.warn(String.format("Error server close: %s%n", this.toString()), e);
            }
        }

        if (group != null && !group.isShutdown()) {

            try {
                group.shutdownNow();

                try {
                    group.awaitTermination(10, TimeUnit.SECONDS);
                }
                catch (final InterruptedException e) {
                    logger.warn(String.format("Error server group wait termination: %s%n", this.toString()), e);
                }
            }
            catch (final IOException e) {
                logger.warn(String.format("Error server group shutdown: %s%n", this.toString()), e);
            }
        }

        lifeTimeManager.setStopped();
    }

    private CompletionHandler<AsynchronousSocketChannel, Session> createAcceptHandler() {

        return new CompletionHandler<AsynchronousSocketChannel, Session>() {

            @Override
            public void completed(AsynchronousSocketChannel result, Session attachment) {

                acceptNext(this);
                if (attachment instanceof AsyncSession) {
                    ((AsyncSession) attachment).open(result);
                }
            }

            @Override
            public void failed(Throwable exc, Session attachment) {

                if (attachment != null) {
                    attachment.close();
                }
                stop();
            }
        };
    }

    private void acceptNext(CompletionHandler<AsynchronousSocketChannel, Session> handler) {

        if (lifeTimeManager.isStopping() || !server.isOpen()) {

            stop();
        } else {

            final Session session = sessionFactory.createSession();
            if (session instanceof AsyncSession) {

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
    public boolean isStopping() {

        return lifeTimeManager.isStopping();
    }

    @Override
    public void setStopping() {

        stop();
    }

    @Override
    public void setStopped() {

        lifeTimeManager.setStopped();
    }

    @Override
    public boolean waitStopped(int timeout) {

        return lifeTimeManager.waitStopped(timeout);
    }
}

package chat.core.client.io;

import chat.core.io.AsyncSession;
import chat.core.io.Session;
import chat.core.io.SessionFactory;
import chat.core.tasks.AsynchronousChannelGroupFactory;
import chat.core.util.Stoppable;
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
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Component("tcpConnectChannel")
public class TcpConnectChannel implements ClientChannel, Stoppable, DisposableBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private final Stoppable lifeTimeManager;
    private final InetSocketAddress address;
    private final SessionFactory sessionFactory;
    private final AsynchronousChannelGroup group;

    @Autowired
    public TcpConnectChannel(
            @Qualifier("mainLifetimeManager") Stoppable lifeTimeManager,
            @Qualifier("clientPoolGroupFactory") AsynchronousChannelGroupFactory groupFactory,
            @Qualifier("clientSessionFactory") SessionFactory sessionFactory,
            TcpConnectConfig config
    ) {

        this.lifeTimeManager = lifeTimeManager;
        this.address = config.getAddress();
        this.sessionFactory = sessionFactory;
        this.group = groupFactory.getInstance();
    }

    @Override
    public String toString() {

        return address.toString();
    }

    private AsynchronousSocketChannel openClient() {

        try {

            return AsynchronousSocketChannel.open();
        }
        catch (final IOException e) {

            logger.error(String.format("Error client connect to: %s%n", this.toString()), e);
            lifeTimeManager.setStopping();
        }
        return null;
    }

    @Override
    public Future<ClientSession> connect() {

        final CompletableFuture<ClientSession> futureResult = new CompletableFuture<>();

        final Session newSession = sessionFactory.createSession();
        if (newSession instanceof ClientSession && newSession instanceof AsyncSession) {

            final AsynchronousSocketChannel client = openClient();
            if (client != null) {

                client.connect(address, (ClientSession) newSession, new CompletionHandler<Void, ClientSession>() {
                    @Override
                    public void completed(Void unused, ClientSession attachment) {

                        if (attachment instanceof AsyncSession) {

                            ((AsyncSession) attachment).open(client);
                        }
                        futureResult.complete(attachment);
                    }

                    @Override
                    public void failed(Throwable exc, ClientSession attachment) {

                        logger.error(String.format("Error connecting client: %s, session: %s%n", this.toString(), attachment), exc);
                        futureResult.completeExceptionally(exc);
                    }
                });
            }
            else {

                final String errorMessage = String.format("Error creating client: %s, session: %s%n", this.toString(), newSession);
                final Exception error = new IOException(errorMessage);

                logger.error(errorMessage, error);
                futureResult.completeExceptionally(error);
            }
        }
        else {

            final String errorMessage = String.format("Error creating session for client: %s, session: %s%n", this.toString(), newSession);
            final Exception error = new InvalidClassException(ClientSession.class.getName(), errorMessage);

            logger.error(errorMessage, error);
            futureResult.completeExceptionally(error);
        }

        return futureResult;
    }

    @Override
    public void destroy() {

        stop();
    }

    private void stop() {

        lifeTimeManager.setStopping();

        if (group != null && !group.isShutdown()) {

            try {
                group.shutdownNow();

                try {
                    group.awaitTermination(10, TimeUnit.SECONDS);
                }
                catch (final InterruptedException e) {
                    logger.warn(String.format("Error client group wait termination: %s%n", this.toString()), e);
                }
            }
            catch (final IOException e) {
                logger.warn(String.format("Error client group shutdown: %s%n", this.toString()), e);
            }
        }

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

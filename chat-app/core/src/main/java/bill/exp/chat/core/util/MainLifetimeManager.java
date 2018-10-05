package bill.exp.chat.core.util;

import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("mainLifetimeManager")
public class MainLifetimeManager implements Stoppable {

    private final Object completeEvent;
    private volatile boolean isStopping;
    private volatile boolean isStopped;

    public MainLifetimeManager() {
        completeEvent = new Object();
        isStopping = false;
        isStopped = false;
    }

    @Override
    public boolean isStopping() {
        return isStopping;
    }

    @Override
    public void setStopping() {
        isStopping = true;
    }

    @Override
    public void setStopped() {
        if (!isStopped) {
            isStopped = true;
            synchronized(completeEvent) {
                completeEvent.notifyAll();
            }
        }
    }

    @Override
    public boolean waitStopped(int timeout) {
        if (!isStopped) {
            synchronized(completeEvent) {
                try {
                    completeEvent.wait(timeout);
                }
                catch (final InterruptedException e) {
                    return false;
                }
            }
        }

        return isStopped;
    }
}

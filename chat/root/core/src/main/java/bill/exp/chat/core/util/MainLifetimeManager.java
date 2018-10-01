package bill.exp.chat.core.util;

import org.springframework.stereotype.Component;

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
    public boolean getIsStopping() {
        return isStopping;
    }

    @Override
    public void setIsStopping() {
        isStopping = true;
    }

    @Override
    public void setIsStopped() {
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

        return true;
    }
}

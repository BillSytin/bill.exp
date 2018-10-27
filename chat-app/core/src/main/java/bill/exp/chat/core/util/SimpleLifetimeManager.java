package bill.exp.chat.core.util;

public class SimpleLifetimeManager implements Stoppable {

    private final Object completeEvent;
    private volatile boolean isStopping;
    private volatile boolean isStopped;

    public SimpleLifetimeManager() {
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

                    if (timeout < 0)
                        completeEvent.wait();
                    else
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

package chat.core.util;

public interface Stoppable {
    boolean isStopping();
    void setStopping();
    void setStopped();
    boolean waitStopped(int timeout);
}

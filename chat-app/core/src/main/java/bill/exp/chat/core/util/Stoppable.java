package bill.exp.chat.core.util;

public interface Stoppable {
    boolean getIsStopping();
    void setIsStopping();
    void setIsStopped();
    boolean waitStopped(int timeout);
}

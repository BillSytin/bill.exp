package bill.exp.chat.core.io;

import bill.exp.chat.core.util.Stoppable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class BaseSessionManager implements SessionManager, Stoppable {

    private final AtomicLong idSequence;
    private final ConcurrentHashMap<Long, Session> sessionHashMap;

    public BaseSessionManager() {

        idSequence = new AtomicLong(1);
        sessionHashMap = new ConcurrentHashMap<>();
    }

    @Override
    public long generateSessionId() {

        return idSequence.getAndIncrement();
    }

    @Override
    public void addSession(Session session) {

        sessionHashMap.put(session.getId(), session);
    }

    @Override
    public void removeSession(Session session) {

        sessionHashMap.remove(session.getId());
    }

    @Override
    public void foreachSession(Consumer<Session> action) {

        for (Session session : sessionHashMap.values()) {

            action.accept(session);
        }
    }

    @Override
    public boolean isStopping() {

        return false;
    }

    @Override
    public void setStopping() {

        foreachSession(Session::close);
    }

    @Override
    public void setStopped() {

    }

    @Override
    public boolean waitStopped(int timeout) {

        return true;
    }
}

package bill.exp.chat.core.api;

import bill.exp.chat.core.data.SessionEvent;

@SuppressWarnings("unused")
public class SessionEventResponse implements Response {

    private final long id;
    private final SessionEvent event;

    public SessionEventResponse(long id, SessionEvent event) {

        this.id = id;
        this.event = event;
    }

    public long getId() {

        return id;
    }

    public SessionEvent getEvent() {

        return event;
    }

    @Override
    public String[] getContent() {

        return null;
    }
}

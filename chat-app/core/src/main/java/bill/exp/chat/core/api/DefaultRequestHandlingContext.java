package bill.exp.chat.core.api;

import bill.exp.chat.core.io.Session;

public class DefaultRequestHandlingContext implements RequestHandlingContext {

    private final Session session;
    private final RequestIntent requestIntent;
    private ResponseIntent responseIntent;

    public DefaultRequestHandlingContext(Session session, RequestIntent requestIntent) {

        this.session = session;
        this.requestIntent = requestIntent;
        this.responseIntent = null;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public RequestIntent getRequestIntent() {

        return requestIntent;
    }

    @Override
    public ResponseIntent getResponseIntent() {

        return responseIntent;
    }

    public void setResponseIntent(ResponseIntent value) {

        responseIntent = value;
    }
}

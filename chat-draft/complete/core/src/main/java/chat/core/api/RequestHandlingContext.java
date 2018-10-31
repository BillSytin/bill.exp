package chat.core.api;

import chat.core.io.Session;

@SuppressWarnings("unused")
public interface RequestHandlingContext {

    Session getSession();
    RequestIntent getRequestIntent();

    ResponseIntent getResponseIntent();
    void setResponseIntent(ResponseIntent value);
}

package bill.exp.chat.core.api;

import bill.exp.chat.core.io.Session;

@SuppressWarnings("unused")
public interface RequestHandlingContext {

    Session getSession();
    RequestIntent getRequestIntent();

    ResponseIntent getResponseIntent();
    void setResponseIntent(ResponseIntent value);
}

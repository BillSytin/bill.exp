package chat.core.data;

import chat.core.api.*;
import chat.core.tasks.DefaultQueueCompletion;
import chat.core.tasks.QueueCompletion;
import org.springframework.core.task.TaskExecutor;

import java.nio.channels.CompletionHandler;

@SuppressWarnings("unused")
public class BaseRequestMessageProcessor implements RequestMessageProcessor {

    public static final int Order = MessageProcessorCategory.Process + MessageProcessorBaseOrder.First + 1;

    private final RequestHandler requestHandler;
    private final TaskExecutor requestTaskExecutor;
    private final CompletionHandler<RequestHandlingContext, Object> completionHandler;
    private final QueueCompletion<RequestHandlingContext, Object> executeQueueCompletion;

    public BaseRequestMessageProcessor(RequestHandler handler) {

        this(handler, null);
    }

    public BaseRequestMessageProcessor(RequestHandler handler, TaskExecutor requestTaskExecutor) {

        this.requestHandler = handler;
        this.requestTaskExecutor = requestTaskExecutor;
        this.completionHandler = new RequestCompletionHandler();
        this.executeQueueCompletion = requestTaskExecutor == null ? null : new DefaultQueueCompletion<>();
    }

    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    private void runNext(RequestHandlingContext context, RequestHandlingAttachment attachment, CompletionHandler<RequestHandlingContext, Object> completionHandler) {

        requestHandler.handle(context, attachment, completionHandler);
    }

    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        boolean isHandled = false;
        if (requestHandler != null) {

            RequestIntent requestIntent = null;
            if (state.getInputMessage() instanceof RequestIntentMessage) {

                requestIntent = ((RequestIntentMessage) state.getInputMessage()).getIntent();
            }
            else {

                Request request = null;
                if (state.getInputMessage() instanceof StringMessage) {

                    final String[] requestStrings = ((StringMessage) state.getInputMessage()).getStrings();
                    request = new SimpleRequest(requestStrings);
                } else if (state.getInputMessage() instanceof SessionEventMessage) {

                    final SessionEvent sessionEvent = ((SessionEventMessage) state.getInputMessage()).getEvent();
                    if (sessionEvent == SessionEvent.Dispose) {

                        requestHandler.dispose(state.getSession());
                    } else {

                        final long sessionId = ((SessionEventMessage) state.getInputMessage()).getId();
                        request = new SessionEventRequest(sessionId, sessionEvent);
                    }
                }

                if (request != null) {

                    requestIntent = requestHandler.detectRequestIntent(request);
                }
            }

            if (requestIntent != null) {

                isHandled = true;

                if (requestHandler.isAsync(requestIntent)) {

                    completionHandler.completed(MessageProcessingAction.Async, state);
                }

                final RequestHandlingContext context = new DefaultRequestHandlingContext(state.getSession(), requestIntent);
                final RequestHandlingAttachment attachment = new RequestHandlingAttachment(state, completionHandler);

                if (this.requestTaskExecutor == null) {

                    runNext(context, attachment, this.completionHandler);
                } else {

                    executeQueueCompletion.submit(handler -> this.requestTaskExecutor.execute(() -> runNext(context, attachment, handler)), this.completionHandler);
                }
            }
            else {

                if (state.getInputMessage() instanceof ResponseIntentMessage) {

                    final ResponseIntent responseIntent = ((ResponseIntentMessage) state.getInputMessage()).getIntent();
                    processResponseIntent(responseIntent, state);
                }
            }
        }

        if (!isHandled) {

            completionHandler.completed(MessageProcessingAction.Next, state);
        }
    }

    private void processResponseIntent(ResponseIntent responseIntent, MessageProcessingState state) {

        if (responseIntent != null) {

            final Response response = requestHandler.generateResponse(responseIntent);
            if (response != null) {

                if (response instanceof SessionEventResponse) {

                    state.setOutputMessage(new SessionEventMessage(
                            ((SessionEventResponse) response).getId(),
                            ((SessionEventResponse) response).getEvent()));
                } else {

                    state.setOutputMessage(new StringMessage(response.getContent()));
                }
            }
        }
    }

    private final class RequestHandlingAttachment {

        private final MessageProcessingState state;
        private final CompletionHandler<MessageProcessingAction, MessageProcessingState> messageCompletionHandler;

        public RequestHandlingAttachment(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> messageCompletionHandler) {

            this.state = state;
            this.messageCompletionHandler = messageCompletionHandler;
        }
    }

    private class RequestCompletionHandler implements CompletionHandler<RequestHandlingContext, Object> {

        @Override
        public void completed(RequestHandlingContext context, Object attachment) {

            final RequestHandlingAttachment requestHandlingAttachment = (RequestHandlingAttachment) attachment;

            processResponseIntent(context.getResponseIntent(), requestHandlingAttachment.state);
            requestHandlingAttachment.messageCompletionHandler.completed(MessageProcessingAction.Next, requestHandlingAttachment.state);
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

            RequestHandlingAttachment requestHandlingAttachment = (RequestHandlingAttachment) attachment;
            requestHandlingAttachment.messageCompletionHandler.failed(exc, requestHandlingAttachment.state);
        }
    }
}

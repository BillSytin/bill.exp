package bill.exp.chat.core.data;

import bill.exp.chat.core.api.*;
import bill.exp.chat.core.tasks.DefaultQueueCompletion;
import bill.exp.chat.core.tasks.QueueCompletion;
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
            if (state.getProcessingMessage() instanceof RequestIntentMessage) {

                requestIntent = ((RequestIntentMessage) state.getProcessingMessage()).getIntent();
            }
            else {

                Request request = null;
                if (state.getProcessingMessage() instanceof StringMessage) {

                    final String[] requestStrings = ((StringMessage) state.getProcessingMessage()).getStrings();
                    request = new SimpleRequest(requestStrings);
                } else if (state.getIncomingMessage() instanceof SessionEventMessage) {

                    final SessionEvent sessionEvent = ((SessionEventMessage) state.getIncomingMessage()).getEvent();
                    if (sessionEvent == SessionEvent.Dispose) {

                        requestHandler.dispose(state.getSession());
                    } else {

                        final long sessionId = ((SessionEventMessage) state.getIncomingMessage()).getId();
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

                if (state.getIncomingMessage() instanceof ResponseIntentMessage) {

                    final ResponseIntent responseIntent = ((ResponseIntentMessage) state.getIncomingMessage()).getIntent();
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
package chat.server.cmd;

@SuppressWarnings("unused")
public enum ChatServerCommandProcessingPhase {
    Unknown,
    Preprocess,
    Help,
    Process,
    Dispose
}

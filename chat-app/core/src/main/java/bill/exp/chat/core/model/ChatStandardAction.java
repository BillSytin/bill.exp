package bill.exp.chat.core.model;

@SuppressWarnings("unused")
public enum ChatStandardAction {
    None(""),
    Default("default"),
    Open("open"),
    Close("close"),
    Login("login"),
    Logout("logout"),
    Welcome("welcome"),
    Help("help"),
    Fetch("fetch"),
    Notify("notify");

    private final String value;

    ChatStandardAction(String value) {

        this.value = value;
    }

    @Override
    public String toString() {

        return this.value;
    }
}

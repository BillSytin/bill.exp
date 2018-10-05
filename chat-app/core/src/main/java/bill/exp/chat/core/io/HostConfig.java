package bill.exp.chat.core.io;

import java.net.InetSocketAddress;

@SuppressWarnings("unused")
public class HostConfig {
    private String host;
    private int port;

    public HostConfig() {

        host = "localhost";
        port = 1974;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public InetSocketAddress getAddress() {

        return new InetSocketAddress(getHost(), getPort());
    }
}

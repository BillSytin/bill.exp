package chat.core.server.io;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "server")
public class TcpServerConfig {

    @Min(2 * 1024)
    @Max(1024 * 1024)
    private int readBufferSize;

    public TcpServerConfig() {

        readBufferSize = 8192;
    }

    public int getReadBufferSize() {

        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {

        this.readBufferSize = readBufferSize;
    }
}

package chat.core.server.io;

import chat.core.io.HostConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "server-accept")
public class TcpAcceptConfig extends HostConfig {
}

package bill.exp.chat.core.client.io;

import bill.exp.chat.core.io.HostConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "client-connect")
public class TcpConnectConfig extends HostConfig {
}

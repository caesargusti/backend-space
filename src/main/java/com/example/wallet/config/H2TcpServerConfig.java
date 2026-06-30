package com.example.wallet.config;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
@ConditionalOnProperty(name = "wallet.h2.tcp.enabled", havingValue = "true", matchIfMissing = true)
public class H2TcpServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer(
            @Value("${wallet.h2.tcp.port:9092}") String tcpPort,
            @Value("${wallet.h2.tcp.allow-others:true}") boolean allowOthers
    ) throws SQLException {
        if (allowOthers) {
            return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", tcpPort);
        }
        return Server.createTcpServer("-tcp", "-tcpPort", tcpPort);
    }
}

package com.thalossphere.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("thalossphere.server.heartbeat")
public class HeartbeatProperties {

    private long timeoutThreshold = 60;

    private long delay = 30;

    private int pageSize = 30;

}

package com.thalossphere.server.heartbeat;

import com.thalossphere.server.config.HeartbeatProperties;
import com.thalossphere.server.service.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HeartbeatManager implements InitializingBean {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    @Autowired
    private HeartbeatReceiver heartbeatReceiver;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private HeartbeatProperties heartbeatProperties;

    public void receive(String applicationName, String ip, int port) {
        heartbeatReceiver.receive(applicationName, ip, port);
    }

    public int countOnline(String applicationName) {
        return redissonClient.getScoredSortedSet(applicationName).size();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SCHEDULER.scheduleWithFixedDelay(new HeartbeatCheck(redissonClient, heartbeatProperties.getTimeoutThreshold(), providerService, heartbeatProperties),
                heartbeatProperties.getDelay(), heartbeatProperties.getDelay(), TimeUnit.SECONDS);
    }

}

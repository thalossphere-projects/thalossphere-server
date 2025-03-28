package com.thalossphere.server.controller;

import com.thalossphere.server.entity.Cmd;
import com.thalossphere.server.entity.Consumer;
import com.thalossphere.server.entity.ProviderInstance;
import com.thalossphere.server.enums.PrividerInstanceStatusEnum;
import com.thalossphere.server.heartbeat.HeartbeatManager;
import com.thalossphere.server.notify.NotificationListener;
import com.thalossphere.server.notify.ReleaseMessage;
import com.thalossphere.server.repository.ConsumerRepository;
import com.thalossphere.server.repository.ProviderInstanceRepository;
import com.thalossphere.server.request.NotificationRequest;
import com.thalossphere.server.response.NotificationResponse;
import com.thalossphere.server.service.CmdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController implements NotificationListener {

    private static final ResponseEntity<NotificationResponse>
            NOT_MODIFIED_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    private final Map<String, DeferredResult> deferredResultConcurrentHashMap = new ConcurrentHashMap<>();

    @Value("${thalossphere.server.notification.timeout:3000}")
    private long notificationTimeout;

    @Autowired
    private HeartbeatManager heartbeatManager;

    @Autowired
    private CmdService cmdService;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ProviderInstanceRepository providerInstanceRepository;

    @PostMapping("/pollNotification")
    public DeferredResult<ResponseEntity<ReleaseMessage>> pollNotification(@RequestBody NotificationRequest request) {
        DeferredResult deferredResult = new DeferredResult(notificationTimeout, NOT_MODIFIED_RESPONSE);
        heartbeatManager.receive(request.getApplicationName(), request.getIp(), request.getPort());
        String key = request.getApplicationName() + request.getIp() + request.getPort();
        deferredResultConcurrentHashMap.put(key, deferredResult);
        deferredResult.onCompletion(() -> {
            deferredResultConcurrentHashMap.remove(key);
        });
        deferredResult.onTimeout(() -> {
            deferredResult.setResult(ResponseEntity.ok(new ReleaseMessage()));
        });
        return deferredResult;
    }


    @Override
    public void handler(List<Cmd> cmdList) {
        for (Cmd cmd : cmdList) {
            //查找消费者
            List<Consumer> consumserList = consumerRepository.findByProviderName(cmd.getApplicationName());
            List<String> providerNameList = consumserList.stream().map(Consumer::getName).collect(Collectors.toList());
            List<ProviderInstance> providerInstanceList = providerInstanceRepository.findByProviderNameInAndStatus(providerNameList, PrividerInstanceStatusEnum.NORMAL.getStatus());
            for (ProviderInstance providerInstance : providerInstanceList) {
                DeferredResult deferredResult = deferredResultConcurrentHashMap.get(providerInstance.getProviderName() + providerInstance.getIp() + providerInstance.getPort());
                if (Objects.nonNull(deferredResult)) {
                    deferredResult.setResult(ResponseEntity.ok(new ReleaseMessage()
                            .setCmd(cmd.getCmd())
                            .setExtendData(cmd.getExtendData())));
                    cmdService.updateComplete(cmd);
                }
            }
        }
    }
}

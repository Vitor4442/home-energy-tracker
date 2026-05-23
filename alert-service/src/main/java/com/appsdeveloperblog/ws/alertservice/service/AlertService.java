package com.appsdeveloperblog.ws.alertservice.service;

import com.appsdeveloperblog.ws.kafka.event.AlertingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertService {
    @KafkaListener(topics = "energy-alerts", groupId = "alert-service")
    public void energyUsageAlertEvent(AlertingEvent alertingEvent){
        log.info("Received alert event: {}", alertingEvent);
    }
}

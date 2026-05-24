package com.appsdeveloperblog.ws.alertservice.service;

import com.appsdeveloperblog.ws.kafka.event.AlertingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final Emailservice emailservice;

    @KafkaListener(topics = "energy-alerts", groupId = "alert-service")
    public void energyUsageAlertEvent(AlertingEvent alertingEvent){
        log.info("Received alert event: {}", alertingEvent);

        // send email alert
        final String subject = "Energy usage Alert for User " +alertingEvent.getUserId();
        final String message = "Alert: " + alertingEvent.getMessage() + "\nThreshhold: " + alertingEvent.getThreshold() +
                "\nEnergy Consumed: " + alertingEvent.getEnergyConsumed();
        emailservice.sendEmail(alertingEvent.getEmail(), subject, message, alertingEvent.getUserId());
    }
}

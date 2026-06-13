package com.appsdeveloperblog.ws.usageservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic energyUsageTopic() {
        return TopicBuilder.name("energy-usage")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic energyAlertsTopic() {
        return TopicBuilder.name("energy-alerts")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

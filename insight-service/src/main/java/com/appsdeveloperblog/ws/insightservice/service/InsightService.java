package com.appsdeveloperblog.ws.insightservice.service;

import com.appsdeveloperblog.ws.insightservice.client.UsageClient;
import com.appsdeveloperblog.ws.insightservice.dto.InsightDto;
import com.appsdeveloperblog.ws.insightservice.dto.UsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InsightService {

    private UsageClient usageClient;
    private OllamaChatModel ollamaChatModel;

    public InsightService(UsageClient usageClient){
        this.usageClient = usageClient;
    }

    public InsightDto getSavingsTips (Long userId){
        // fetch data from usage services
        final UsageDto usageDto = usageClient.getXDaysUsageForUser(userId, 7);

        double totalUsage = usageDto.devices().stream()
                .mapToDouble(device -> device.energyConsumed())
                .sum();

        log.info("Calling Ollama for userId {} with total usage {}", userId, totalUsage);

        String prompt = new StringBuilder()
                .append("Analyse the following energy usage data and provide a " +
                        "concise overview with actionable insights.")
                .append("This data is the aggregate data for the past 3 days.")
                .append("Usage Data: \n")
                .append(usageDto.devices())
                .toString();

        ChatResponse response = ollamaChatModel.call(
                Prompt.builder()
                        .content(prompt)
                        .build());

        return InsightDto.builder()
                .userId(userId)
                .tips(response.getResult().getOutput().getText())
                .energyUsage(totalUsage)
                .build();
    }

    public InsightDto getOverview (Long userId){
        // fetch data from usage services
        final UsageDto usageDto = usageClient.getXDaysUsageForUser(userId, 7);

        double totalUsage = usageDto.devices().stream()
                .mapToDouble(device -> device.energyConsumed())
                .sum();

        log.info("Calling Ollama for userId {} with total usage {}", userId, totalUsage);

        String prompt = new StringBuilder()
                .append("Analyse the following energy usage data and provide a " +
                        "concise overview with actionable insights.")
                .append("This data is the aggregate data for the past 3 days.")
                .append("Usage Data: \n")
                .append(usageDto.devices())
                .toString();

        ChatResponse response = ollamaChatModel.call(
                Prompt.builder()
                        .content(prompt)
                        .build());

        return InsightDto.builder()
                .userId(userId)
                .tips(response.getResult().getOutput().getText())
                .energyUsage(totalUsage)
                .build();
    }
}

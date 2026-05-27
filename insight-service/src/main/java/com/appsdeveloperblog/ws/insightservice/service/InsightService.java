package com.appsdeveloperblog.ws.insightservice.service;

import com.appsdeveloperblog.ws.insightservice.client.UsageClient;
import com.appsdeveloperblog.ws.insightservice.dto.InsightDto;
import com.appsdeveloperblog.ws.insightservice.dto.UsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InsightService {

    private UsageClient usageClient;

    public InsightService(UsageClient usageClient){
        this.usageClient = usageClient;
    }

    public InsightDto getOverview (Long userId){
        // fetch data from usage services
        final UsageDto usageDto = usageClient.getXDaysUsageForUser(userId, 7);
    }
}

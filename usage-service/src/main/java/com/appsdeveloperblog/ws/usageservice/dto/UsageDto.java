package com.appsdeveloperblog.ws.usageservice.dto;

import lombok.Builder;

@Builder
public record UsageDto(
        Long id,
        String name,
        String type,
        String location,
        Long userId,
        Double energyConsumed
) {
}

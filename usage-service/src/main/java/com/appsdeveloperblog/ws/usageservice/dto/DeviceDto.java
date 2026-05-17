package com.appsdeveloperblog.ws.usageservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
public record DeviceDto(Long id,
                        String name,
                        String type,
                        String location,
                        Long userId,
                        Double energyConsumed) {
}

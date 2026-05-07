package com.appsdeveloperblog.ws.deviceservice.mapper;

import com.appsdeveloperblog.ws.deviceservice.dto.DeviceDto;
import com.appsdeveloperblog.ws.deviceservice.entity.Device;

public class DeviceMapper {
    public static DeviceDto toDto (Device input){
        return DeviceDto.builder()
                .id(input.getId())
                .name(input.getName())
                .location(input.getLocation())
                .userId(input.getUserId())
                .type(input.getType())
                .build();
    }
    public static Device toEntity (DeviceDto deviceDto){
        return Device.builder()
                .id(deviceDto.getId())
                .name(deviceDto.getName())
                .location(deviceDto.getLocation())
                .userId(deviceDto.getUserId())
                .type(deviceDto.getType())
                .build();
    }
    public static void extracted(DeviceDto deviceDto, Device device) {
        device.setId(deviceDto.getId());
        device.setName(deviceDto.getName());
        device.setType(deviceDto.getType());
        device.setLocation(deviceDto.getLocation());
        device.setUserId(deviceDto.getUserId());
    }
}

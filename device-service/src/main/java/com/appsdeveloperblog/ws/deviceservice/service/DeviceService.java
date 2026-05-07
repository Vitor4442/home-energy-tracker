package com.appsdeveloperblog.ws.deviceservice.service;

import com.appsdeveloperblog.ws.deviceservice.dto.DeviceDto;
import com.appsdeveloperblog.ws.deviceservice.entity.Device;
import com.appsdeveloperblog.ws.deviceservice.exception.DeviceNotFoundException;
import com.appsdeveloperblog.ws.deviceservice.mapper.DeviceMapper;
import com.appsdeveloperblog.ws.deviceservice.repository.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceDto getDeviceById(Long id){
        Device device = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException("Device not found with id " + id));
        return DeviceMapper.toDto(device);
    }

    public DeviceDto createDevice(DeviceDto deviceDto){
        Device device = deviceRepository.save(DeviceMapper.toEntity(deviceDto));
        return DeviceMapper.toDto(device);
    }

    public DeviceDto updateDevice(Long id, DeviceDto deviceDto) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException("Device not found with id " + id));
        DeviceMapper.extracted(deviceDto, device);
        return DeviceMapper.toDto(device);
    }

    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new DeviceNotFoundException("Device not found with id " + id));
        deviceRepository.delete(device);
    }
}

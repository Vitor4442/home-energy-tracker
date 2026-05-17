package com.appsdeveloperblog.ws.usageservice.service;

import com.appsdeveloperblog.ws.kafka.event.AlertingEvent;
import com.appsdeveloperblog.ws.kafka.event.EnergyUsageEvent;
import com.appsdeveloperblog.ws.usageservice.client.DeviceClient;
import com.appsdeveloperblog.ws.usageservice.client.UserClient;
import com.appsdeveloperblog.ws.usageservice.dto.DeviceDto;
import com.appsdeveloperblog.ws.usageservice.dto.UserDto;
import com.appsdeveloperblog.ws.usageservice.model.DeviceEnergy;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsageService {

    private InfluxDBClient influxDBClient;
    private DeviceClient deviceClient;
    private UserClient userClient;

    @Value("${influx.bucket}")
    private String influxBucket;

    @Value("${influx.org}")
    private String influxOrg;

    private final KafkaTemplate<String, AlertingEvent> kafkaTemplate;

    @KafkaListener(topics = "energy-usage", groupId = "usage-service")
    public void energyUsageEvent(EnergyUsageEvent energyUsageEvent){
        log.info("Received energy usage event: {}", energyUsageEvent);
        Point point = Point.measurement("energy_usage")
                .addTag("deviceId", String.valueOf(energyUsageEvent.deviceId()))
                .addField("energyConsumed", energyUsageEvent.energyConsumed())
                .time(energyUsageEvent.timestamp(), WritePrecision.MS);
        influxDBClient.getWriteApiBlocking().writePoint(influxBucket, influxOrg, point);
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void aggregateDeviceEnergyUsage(){
        final Instant now = Instant.now();
        final Instant oneHourAgo = now.minusSeconds(3600);

        String fluxQuery = String.format("""
        from(bucket: "%s")
          |> range(start: time(v: "%s"), stop: time(v: "%s"))
          |> filter(fn: (r) => r["_measurement"] == "energy_usage")
          |> filter(fn: (r) => r["_field"] == "energyConsumed")
          |> group(columns: ["deviceId"])
          |> sum(column: "_value")
        """, influxBucket, oneHourAgo.toString(), now);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery, influxOrg);

        List<DeviceEnergy> deviceEnergies = new ArrayList<>();

        for (FluxTable table : tables){
            for (FluxRecord record : table.getRecords()){
                String deviceIdStr = (String) record.getValueByKey("deviceId");
                Double energyConsumed = record.getValueByKey("_value") instanceof Number ?
                        ((Number) record.getValueByKey("_value")).doubleValue() : 0.0;

                deviceEnergies.add(
                        DeviceEnergy.builder()
                                .deviceId(Long.valueOf(deviceIdStr))
                                .energyConsumed(energyConsumed)
                                .build()
                );
            }
        }
        log.info("Aggregated device energies over the past hour: {}", deviceEnergies);

        for (DeviceEnergy deviceEnergy : deviceEnergies){
            final DeviceDto deviceResponse = deviceClient.getDeviceById(deviceEnergy.getDeviceId());

            if(deviceResponse == null || deviceResponse.id() == null){
                log.warn("Device not found for ID: {}", deviceEnergy.getDeviceId());
                continue;
            }
            deviceEnergy.setUserId(deviceResponse.userId());
        }

        // remove devices with null userId
        deviceEnergies.removeIf(de -> de.getUserId() == null);

        //get user-device mapping and aggregate per user
        Map<Long, List<DeviceEnergy>> userDeviceEnergyMap =
                deviceEnergies.stream()
                        .collect(Collectors.groupingBy(DeviceEnergy::getUserId));

        log.info("User-Device Energy Map: {}", userDeviceEnergyMap);

        //get users energy consuming thresholds
        List<Long> userIds = new ArrayList<>(userDeviceEnergyMap.keySet());
        final Map<Long, Double> userThresholdMap = new HashMap<>();
        final Map<Long, String> userEmailMap = new HashMap<>();

        for (final Long userId : userIds){
            try {
                UserDto user = userClient.getUserById(userId);
                if(user == null || user.id() == null || !user.alerting()){
                    log.warn("User not found or alerting disable for ID: {}", userId);
                    continue;
                }
                userThresholdMap.put(userId, user.energyAlertingThreshold());
                userEmailMap.put(userId, user.email());
            } catch (Exception e){
                log.warn("Failed to fetch user for ID: {}", userId);
            }
        }
        log.info("User Threshold Map: {}", userThresholdMap);

        //Check thresholds against aggregated usage
        final List<Long> alertedUsers = new ArrayList<>(userThresholdMap.keySet());
        for (final Long userId : alertedUsers){
            final Double threshold = userThresholdMap.get(userId);
            final List<DeviceEnergy> devices = userDeviceEnergyMap.get(userId);

            final Double totalConsumption = devices.stream()
                    .mapToDouble(DeviceEnergy::getEnergyConsumed)
                    .sum();

            if (totalConsumption > threshold){
                log.info("ALERT: User ID {} has exceeded the energy threshold! " +
                        "Total Consumption: {}, Threshold: {}", userId, totalConsumption, threshold);
                //put message on kafka alert-topic
                final AlertingEvent alertingEvent = AlertingEvent.builder()
                        .userId(userId)
                        .message("energy consumed threshold exceeded")
                        .threshold(threshold)
                        .energyConsumed(totalConsumption)
                        .email(userEmailMap.get(userId))
                        .build();
                kafkaTemplate.send("energy-alerts", alertingEvent);
            } else {
                log.info("User ID {} is within the nergy threshold. " +
                        "Total Consumption: {}, Threshold: {}",
                        userId, totalConsumption, threshold);
            }

        }
    }


}

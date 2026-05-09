package com.appsdeveloperblog.ws.ingestionservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingestion")
public class ingestionController {

    private final IngestionService ingestionService;

    public ingestionController(IngestionService ingestionService){
        this.ingestionService = ingestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void ingestData(@RequestBody EnergyUsageDto usageDto){
        ingestionService.ingestEnergyUsage(usageDto);
    }
}

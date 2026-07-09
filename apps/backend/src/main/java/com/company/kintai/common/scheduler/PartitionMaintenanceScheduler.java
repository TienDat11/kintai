package com.company.kintai.common.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PartitionMaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(PartitionMaintenanceScheduler.class);

    private final PartitionService partitionService;

    public PartitionMaintenanceScheduler(PartitionService partitionService) {
        this.partitionService = partitionService;
    }

    @Scheduled(cron = "0 0 3 1 * *")
    public void scheduledMaintenance() {
        log.info("Scheduled partition maintenance triggered at 3 AM on day 1 of month.");
        partitionService.runPartitionMaintenance();
    }
}

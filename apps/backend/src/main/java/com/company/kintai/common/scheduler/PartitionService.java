package com.company.kintai.common.scheduler;

import com.company.kintai.common.scheduler.mapper.PartitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PartitionService {

    private static final Logger log = LoggerFactory.getLogger(PartitionService.class);

    private final PartitionMapper partitionMapper;

    public PartitionService(PartitionMapper partitionMapper) {
        this.partitionMapper = partitionMapper;
    }

    public void runPartitionMaintenance() {
        log.info("Partition maintenance started.");
        try {
            partitionMapper.ensureFuturePartitions(3);
            log.info("ensure_future_partitions(3) completed.");
        } catch (Exception e) {
            log.error("ensure_future_partitions(3) failed: {}", e.getMessage(), e);
        }
        try {
            Integer detached = partitionMapper.detachOldPartitions(24);
            log.info("detach_old_partitions(24) completed: {} partition(s) detached.",
                    detached != null ? detached : 0);
        } catch (Exception e) {
            log.error("detach_old_partitions(24) failed: {}", e.getMessage(), e);
        }
        log.info("Partition maintenance finished.");
    }
}

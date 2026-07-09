package com.company.kintai.common.admin;

import com.company.kintai.common.dto.ApiResponse;
import com.company.kintai.common.scheduler.PartitionService;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final PartitionService partitionService;

    public AdminController(PartitionService partitionService) {
        this.partitionService = partitionService;
    }

    @PostMapping("/partition-maintenance/run")
    @PreAuthorize("hasAuthority('admin:all')")
    public ApiResponse<Map<String, String>> runPartitionMaintenance() {
        partitionService.runPartitionMaintenance();
        return ApiResponse.ok(Map.of("message", "Partition maintenance completed"));
    }
}

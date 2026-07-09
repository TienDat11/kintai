package com.company.kintai.leave.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tự động cấp leave_balance đầu năm tài chính.
 * TODO: implement logic cấp grant theo năm làm việc (労基法 Điều 39):
 *   - 6 tháng đầu: 10 ngày PAID
 *   - Sau 1.5 năm: +1 ngày/năm (tối đa 20 ngày)
 *   - Mỗi năm vào 1/4: chạy batch insert grant cho từng employee
 * Chạy 1 lần/năm (cron = "0 0 2 4 * ?" = 2h sáng 1/4 hàng năm).
 * KHÔNG block gate — để TODO.
 */
@Component
public class LeaveGrantScheduler {

    // TODO: implement annual grant logic
    @Scheduled(cron = "0 0 2 4 * ?")
    public void grantAnnualLeave() {
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
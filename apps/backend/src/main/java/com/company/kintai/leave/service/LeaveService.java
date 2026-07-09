package com.company.kintai.leave.service;

import com.company.kintai.leave.dto.ApprovalRequest;
import com.company.kintai.leave.dto.LeaveRequest;
import com.company.kintai.leave.dto.LeaveResponse;
import java.util.List;

public interface LeaveService {

    LeaveResponse apply(LeaveRequest request);

    LeaveResponse approve(Long id, ApprovalRequest request);

    LeaveResponse reject(Long id, ApprovalRequest request);

    LeaveResponse cancel(Long id);

    List<LeaveResponse> getByEmployee(Long employeeId);

    List<LeaveResponse> getPending();
}
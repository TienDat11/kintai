package com.company.kintai.leave.service;

import com.company.kintai.common.constant.ErrorCode;
import com.company.kintai.common.exception.BusinessException;
import com.company.kintai.common.exception.ResourceNotFoundException;
import com.company.kintai.leave.dto.ApprovalRequest;
import com.company.kintai.leave.dto.LeaveRequest;
import com.company.kintai.leave.dto.LeaveResponse;
import com.company.kintai.leave.entity.LeaveBalanceEntity;
import com.company.kintai.leave.entity.LeaveRequestEntity;
import com.company.kintai.leave.entity.LeaveTypeEntity;
import com.company.kintai.leave.mapper.LeaveBalanceMapper;
import com.company.kintai.leave.mapper.LeaveRequestMapper;
import com.company.kintai.leave.mapper.LeaveTypeMapper;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final LeaveTypeMapper leaveTypeMapper;

    public LeaveServiceImpl(
            LeaveRequestMapper leaveRequestMapper,
            LeaveBalanceMapper leaveBalanceMapper,
            LeaveTypeMapper leaveTypeMapper) {
        this.leaveRequestMapper = leaveRequestMapper;
        this.leaveBalanceMapper = leaveBalanceMapper;
        this.leaveTypeMapper = leaveTypeMapper;
    }

    @Override
    @Transactional
    public LeaveResponse apply(LeaveRequest request) {
        var entity = new LeaveRequestEntity();
        entity.setEmployeeId(request.employeeId());
        entity.setLeaveTypeId(request.leaveTypeId());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setDays(request.days());
        entity.setReason(request.reason());
        entity.setCreatedBy("system");
        entity.setUpdatedBy("system");
        leaveRequestMapper.insert(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public LeaveResponse approve(Long id, ApprovalRequest request) {
        var leaveReq = leaveRequestMapper
                .selectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));

        if (!"PENDING".equals(leaveReq.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION,
                    "Leave request is not in PENDING status: " + leaveReq.getStatus());
        }

        var lb = leaveBalanceMapper
                .selectByEmployeeAndYear(leaveReq.getEmployeeId(), null, leaveReq.getLeaveTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION,
                        "Leave balance not found for employee + type + year"));

        // remaining = granted - used (generated column from DB)
        // verify remaining >= days
        BigDecimal remaining = lb.getGrantedDays().subtract(lb.getUsedDays());
        if (remaining.compareTo(leaveReq.getDays()) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_LEAVE_BALANCE,
                    "Insufficient leave balance: remaining=" + remaining + ", requested=" + leaveReq.getDays());
        }

        // update leave_balance
        BigDecimal newUsed = lb.getUsedDays().add(leaveReq.getDays());
        leaveBalanceMapper.updateUsedDays(lb.getId(), newUsed, lb.getVersion(), "system");

        // update leave_request status
        leaveRequestMapper.updateStatus(id, "APPROVED", request.approverId(), "system");

        leaveReq.setStatus("APPROVED");
        return toResponse(leaveReq);
    }

    @Override
    @Transactional
    public LeaveResponse reject(Long id, ApprovalRequest request) {
        var leaveReq = leaveRequestMapper
                .selectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));

        if (!"PENDING".equals(leaveReq.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION,
                    "Only PENDING leave requests can be rejected");
        }

        leaveRequestMapper.updateStatus(id, "REJECTED", request.approverId(), "system");
        leaveReq.setStatus("REJECTED");
        return toResponse(leaveReq);
    }

    @Override
    @Transactional
    public LeaveResponse cancel(Long id) {
        var leaveReq = leaveRequestMapper
                .selectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));

        String currentStatus = leaveReq.getStatus();

        if ("APPROVED".equals(currentStatus)) {
            // Must rollback leave_balance.used
            var lb = leaveBalanceMapper
                    .selectByEmployeeAndYear(leaveReq.getEmployeeId(), null, leaveReq.getLeaveTypeId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION,
                            "Leave balance not found for rollback"));

            BigDecimal newUsed = lb.getUsedDays().subtract(leaveReq.getDays());
            if (newUsed.compareTo(BigDecimal.ZERO) < 0) {
                newUsed = BigDecimal.ZERO;
            }
            leaveBalanceMapper.updateUsedDays(lb.getId(), newUsed, lb.getVersion(), "system");
        }

        // CANCELLED for any status
        leaveRequestMapper.updateStatus(id, "CANCELLED", null, "system");
        leaveReq.setStatus("CANCELLED");
        return toResponse(leaveReq);
    }

    @Override
    public List<LeaveResponse> getByEmployee(Long employeeId) {
        return leaveRequestMapper.selectByEmployeeId(employeeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<LeaveResponse> getPending() {
        return leaveRequestMapper.selectByStatus("PENDING").stream()
                .map(this::toResponse)
                .toList();
    }

    private LeaveResponse toResponse(LeaveRequestEntity entity) {
        LeaveTypeEntity lt = leaveTypeMapper.selectById(entity.getLeaveTypeId());
        return new LeaveResponse(
                entity.getId(),
                entity.getEmployeeId(),
                null,
                entity.getLeaveTypeId(),
                lt != null ? lt.getCode() : null,
                lt != null ? lt.getName() : null,
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getReason(),
                entity.getStatus());
    }
}
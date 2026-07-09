package com.company.kintai.employee.controller;

import com.company.kintai.common.constant.ApiPath;
import com.company.kintai.common.dto.ApiResponse;
import com.company.kintai.common.dto.PageResult;
import com.company.kintai.employee.dto.EmployeeRequest;
import com.company.kintai.employee.dto.EmployeeResponse;
import com.company.kintai.employee.dto.EmployeeSearchForm;
import com.company.kintai.employee.service.EmployeeRelationService;
import com.company.kintai.employee.service.EmployeeService;
import com.company.kintai.employee.domain.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPath.EMPLOYEES)
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeRelationService employeeRelationService;

    public EmployeeController(EmployeeService employeeService,
                               EmployeeRelationService employeeRelationService) {
        this.employeeService = employeeService;
        this.employeeRelationService = employeeRelationService;
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(employeeService.findById(id));
    }

    @GetMapping
    public ApiResponse<PageResult<EmployeeResponse>> search(@Valid EmployeeSearchForm form) {
        return ApiResponse.ok(employeeService.search(form));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.ok(employeeService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> update(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }

    // === Sub-resources: Contract ===

    @GetMapping("/{id}/contracts")
    public ApiResponse<List<Contract>> getContracts(@PathVariable Long id) {
        return ApiResponse.ok(employeeRelationService.findContractsByEmployeeId(id));
    }

    // === Sub-resources: BankAccount ===

    @GetMapping("/{id}/bank-accounts")
    public ApiResponse<List<BankAccount>> getBankAccounts(@PathVariable Long id) {
        return ApiResponse.ok(employeeRelationService.findBankAccountsByEmployeeId(id));
    }

    // === Sub-resources: DependentFamily ===

    @GetMapping("/{id}/dependents")
    public ApiResponse<List<DependentFamily>> getDependents(@PathVariable Long id) {
        return ApiResponse.ok(employeeRelationService.findDependentsByEmployeeId(id));
    }

    // === Sub-resources: CommuteRoute ===

    @GetMapping("/{id}/commute-routes")
    public ApiResponse<List<CommuteRoute>> getCommuteRoutes(@PathVariable Long id) {
        return ApiResponse.ok(employeeRelationService.findCommuteRoutesByEmployeeId(id));
    }

    // === Sub-resources: EmergencyContact ===

    @GetMapping("/{id}/emergency-contacts")
    public ApiResponse<List<EmergencyContact>> getEmergencyContacts(@PathVariable Long id) {
        return ApiResponse.ok(employeeRelationService.findEmergencyContactsByEmployeeId(id));
    }
}

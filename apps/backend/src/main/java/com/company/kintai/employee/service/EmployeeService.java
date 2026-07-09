package com.company.kintai.employee.service;

import com.company.kintai.common.dto.PageResult;
import com.company.kintai.employee.dto.EmployeeRequest;
import com.company.kintai.employee.dto.EmployeeResponse;
import com.company.kintai.employee.dto.EmployeeSearchForm;

public interface EmployeeService {

    EmployeeResponse findById(Long id);

    PageResult<EmployeeResponse> search(EmployeeSearchForm form);

    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse update(Long id, EmployeeRequest request);

    void delete(Long id);
}

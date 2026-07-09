package com.company.kintai.employee.service;

import com.company.kintai.common.constant.ErrorCode;
import com.company.kintai.common.dto.PageResult;
import com.company.kintai.common.exception.BusinessException;
import com.company.kintai.common.exception.ResourceNotFoundException;
import com.company.kintai.employee.converter.EmployeeConverter;
import com.company.kintai.employee.dto.EmployeeRequest;
import com.company.kintai.employee.dto.EmployeeResponse;
import com.company.kintai.employee.dto.EmployeeSearchForm;
import com.company.kintai.employee.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final EmployeeConverter employeeConverter;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper, EmployeeConverter employeeConverter) {
        this.employeeMapper = employeeMapper;
        this.employeeConverter = employeeConverter;
    }

    @Override
    public EmployeeResponse findById(Long id) {
        var entity =
                employeeMapper
                        .selectById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        return employeeConverter.toResponse(entity);
    }

    @Override
    public PageResult<EmployeeResponse> search(EmployeeSearchForm form) {
        long total = employeeMapper.countBySearch(form);
        var rows =
                employeeMapper.selectBySearch(
                        form, form.sizeOrDefault(), form.pageOrDefault() * form.sizeOrDefault());
        var content = rows.stream().map(employeeConverter::toResponse).toList();
        return PageResult.of(content, form.pageOrDefault(), form.sizeOrDefault(), total);
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeMapper.existsByCode(request.code())) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Code exists: " + request.code());
        }
        var entity = employeeConverter.toEntity(request);
        employeeMapper.insert(entity);
        return employeeConverter.toResponse(entity);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        var entity =
                employeeMapper
                        .selectById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));

        if (employeeMapper.existsByCode(request.code())) {
            var existing =
                    employeeMapper
                            .selectById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
            if (!existing.getCode().equals(request.code())) {
                throw new BusinessException(ErrorCode.DUPLICATE, "Code exists: " + request.code());
            }
        }

        // TODO: update fields properly via converter once domain/entity fields are aligned
        employeeMapper.update(entity);
        return employeeConverter.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var entity =
                employeeMapper
                        .selectById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        employeeMapper.deleteById(id);
    }
}
package com.company.kintai.employee.converter;

import com.company.kintai.employee.dto.EmployeeRequest;
import com.company.kintai.employee.dto.EmployeeResponse;
import com.company.kintai.employee.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeConverter {

    EmployeeResponse toResponse(EmployeeEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "departmentName", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "mynumberEncrypted", ignore = true)
    @Mapping(target = "mynumberHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    EmployeeEntity toEntity(EmployeeRequest request);
}
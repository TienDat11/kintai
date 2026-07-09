package com.company.kintai.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.company.kintai.common.exception.ResourceNotFoundException;
import com.company.kintai.employee.converter.EmployeeConverter;
import com.company.kintai.employee.dto.EmployeeRequest;
import com.company.kintai.employee.dto.EmployeeResponse;
import com.company.kintai.employee.entity.EmployeeEntity;
import com.company.kintai.employee.mapper.EmployeeMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock EmployeeMapper employeeMapper;
    @Mock EmployeeConverter employeeConverter;
    @InjectMocks EmployeeServiceImpl service;

    @Test
    void should_throw_not_found_when_id_absent() {
        when(employeeMapper.selectById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_return_employee_when_id_exists() {
        var entity = new EmployeeEntity();
        entity.setId(1L);
        entity.setCode("EMP001");
        entity.setFullName("田中太郎");
        entity.setStatus("ACTIVE");

        var response =
                new EmployeeResponse(1L, "EMP001", "田中太郎", null, "開発部", null, null, "ACTIVE");

        when(employeeMapper.selectById(1L)).thenReturn(Optional.of(entity));
        when(employeeConverter.toResponse(entity)).thenReturn(response);

        var result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("EMP001");
        assertThat(result.fullName()).isEqualTo("田中太郎");
    }

    @Test
    void should_create_employee_successfully() {
        var request =
                new EmployeeRequest("EMP002", "佐藤花子", "サトウハナコ", 1L, "sato@example.com", "03-1234-5678", "100-0001");

        var entity = new EmployeeEntity();
        entity.setId(2L);
        entity.setCode("EMP002");
        entity.setFullName("佐藤花子");
        entity.setStatus("ACTIVE");

        var response =
                new EmployeeResponse(2L, "EMP002", "佐藤花子", "サトウハナコ", "開発部", "sato@example.com", "03-1234-5678", "ACTIVE");

        when(employeeMapper.existsByCode("EMP002")).thenReturn(false);
        when(employeeConverter.toEntity(request)).thenReturn(entity);
        when(employeeMapper.insert(any(EmployeeEntity.class))).thenReturn(1);
        when(employeeConverter.toResponse(entity)).thenReturn(response);

        var result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("EMP002");
    }

    @Test
    void should_throw_duplicate_when_code_exists() {
        var request = new EmployeeRequest("EMP001", "田中太郎", null, 1L, null, null, null);

        when(employeeMapper.existsByCode("EMP001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .hasMessageContaining("Code exists");
    }
}

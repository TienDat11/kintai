package com.company.kintai.employee.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.kintai.employee.dto.EmployeeSearchForm;
import com.company.kintai.employee.entity.EmployeeEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev")
class EmployeeMapperIT {

    @Autowired EmployeeMapper employeeMapper;

    @Test
    void should_insert_and_select_employee() {
        var entity = new EmployeeEntity();
        entity.setCode("TEST001");
        entity.setFullName("テスト太郎");
        entity.setFullNameKana("テストタロウ");
        entity.setDepartmentId(1L);
        entity.setEmail("test@example.com");
        entity.setStatus("ACTIVE");

        employeeMapper.insert(entity);
        assertThat(entity.getId()).isNotNull();

        var found = employeeMapper.selectById(entity.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("TEST001");
        assertThat(found.get().getFullName()).isEqualTo("テスト太郎");
    }

    @Test
    void should_search_by_keyword() {
        var form = new EmployeeSearchForm("テスト", null, null, 0, 20);
        List<EmployeeEntity> results = employeeMapper.selectBySearch(form, 20, 0);
        long count = employeeMapper.countBySearch(form);

        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    void should_check_code_exists() {
        assertThat(employeeMapper.existsByCode("EMP001")).isFalse();
    }
}

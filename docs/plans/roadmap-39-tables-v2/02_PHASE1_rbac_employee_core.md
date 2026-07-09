# PHASE 1 — RBAC động + Employee Core (nền tảng mọi phase sau)

## Business context
HRM Nhật thật không dùng `role` string cứng như code demo hiện tại (`app_user.role`). Họ cần **RBAC
động**: một `role` có nhiều `permission`, một `employee` có thể có nhiều `role` (ví dụ vừa là
MANAGER của phòng mình vừa là PAYROLL_OFFICER kiêm nhiệm). Đây là lý do có 3 bảng
`role`/`permission`/`role_permission` + bảng nối `employee_role` — mô hình RBAC chuẩn (không phải
đơn giản 1 user = 1 role). `@PreAuthorize` sẽ check permission (`employee:read`), không check role
trực tiếp — permission mới là đơn vị phân quyền thật, role chỉ là "gói" permission để gán nhanh.

`app.employee` là bảng trung tâm — mọi bảng khác (contract, attendance, leave, payroll...) đều FK
về nó. Khác biệt lớn nhất so với code demo: có `mynumber_encrypted`/`mynumber_hash` (マイナンバー —
số định danh cá nhân Nhật, PHẢI mã hoá theo luật bảo vệ số cá nhân, KHÔNG lưu plaintext bao giờ),
`position_id` (chức vụ, tách khỏi department), `hire_date`, `birth_date`.

## Scope — bảng (từ V1, V2 employee-part, V3)
`app.role`, `app.permission`, `app.role_permission`, `app.employee_role`,
`app.department`, `app.position`, `app.employee`

## Việc cụ thể

### 1. Dọn code cũ (bắt buộc trước khi viết mới)
- Xoá `auth/entity/AppUserEntity.java`, `auth/entity/UserEntity.java`, `auth/mapper/UserMapper.java`
  + `.xml` (viết cho bảng `app_user` không còn tồn tại).
- Xoá `employee/entity/EmployeeEntity.java`, `employee/mapper/EmployeeMapper.java` + `.xml` (sai cột).
- **Giữ nguyên** `employee/domain/Employee.java` làm khung tham khảo nhưng sẽ viết lại field cho khớp
  `app.employee` — domain record là nơi duy nhất KHÔNG bị dọn trắng, chỉ mở rộng field.

### 2. RBAC (mới hoàn toàn)
- `common/security/entity/{RoleEntity, PermissionEntity, RolePermissionEntity, EmployeeRoleEntity}.java`
- `common/security/mapper/{RoleMapper, PermissionMapper, EmployeeRoleMapper}.xml` + interface —
  quan trọng nhất: `EmployeeRoleMapper.selectPermissionsByEmployeeId(Long employeeId)` — JOIN 3 bảng
  `employee_role → role_permission → permission`, trả về `List<String>` code permission (vd
  `"employee:read"`) để nạp vào JWT claim hoặc `UserDetails.getAuthorities()`.
- Sửa `JwtTokenProvider`/`JwtAuthFilter`: claim JWT đổi từ 1 role string → **list permission code**,
  `SimpleGrantedAuthority` build từ permission code (không phải `ROLE_xxx` nữa vì đây không phải role
  Spring Security cổ điển — dùng `@PreAuthorize("hasAuthority('employee:read')")`).
- `AuthServiceImpl.login()`: sau khi verify password, gọi `EmployeeRoleMapper` lấy permission list,
  nhúng vào JWT — KHÔNG query lại DB mỗi request (JWT tự chứa permission, đây là lý do JWT hợp lý cho
  case này, tránh N+1 query).

### 3. Employee Core (viết lại từ đầu)
- `employee/entity/{DepartmentEntity, PositionEntity, EmployeeEntity}.java` — field khớp đúng cột
  V2 (`code`, `full_name`, `full_name_kana`, `email`, `phone`, `postal_code`, `birth_date`,
  `hire_date`, `department_id`, `position_id`, `status`, `mynumber_encrypted BYTEA`,
  `mynumber_hash BYTEA`, `version`, `deleted_at`).
- `common/security/CryptoService.java` — AES-GCM encrypt/decrypt cho `mynumber_encrypted`, dùng key
  từ env (`APP_ENCRYPTION_KEY`, KHÔNG hardcode — theo `PLAN_00_RULES.md` R7). `mynumber_hash` = SHA-256
  của mynumber gốc, dùng để **tra cứu** (không thể decrypt để so sánh, phải hash để index UNIQUE).
- `employee/mapper/EmployeeMapper.xml`: mọi SELECT có `WHERE deleted_at IS NULL`; UPDATE có
  `WHERE id = #{id} AND version = #{version}` rồi `SET version = version + 1` (optimistic lock —
  nguyên tắc #3 trong ROADMAP).
- `employee/domain/Employee.java`: record mở rộng field mới, **KHÔNG chứa `mynumberEncrypted` thô**
  (chỉ chứa `mynumberMasked` dạng `"***-**-6789"` — domain/DTO ra ngoài không bao giờ lộ số gốc).

## File cụ thể
| File | Hành động |
|---|---|
| `auth/entity/*`, `auth/mapper/UserMapper.*` | Xoá |
| `employee/entity/EmployeeEntity.java`, `employee/mapper/EmployeeMapper.*` | Xoá, viết lại |
| `common/security/entity/{Role,Permission,RolePermission,EmployeeRole}Entity.java` | Tạo mới |
| `common/security/mapper/*Mapper.java` + `.xml` | Tạo mới |
| `common/security/CryptoService.java` | Tạo mới |
| `common/security/JwtTokenProvider.java`, `JwtAuthFilter.java` | Sửa (claim → permission list) |
| `employee/domain/Employee.java`, `EmployeeConverter.java` (MapStruct) | Sửa |

## Test / Gate
- Unit test `CryptoService`: encrypt rồi decrypt ra đúng giá trị gốc; hash 2 lần cùng input ra cùng hash.
- Integration test (Testcontainers Postgres) `EmployeeRoleMapper`: seed 1 employee + 1 role có 2
  permission → `selectPermissionsByEmployeeId` trả đúng 2 code.
- `AuthServiceImplTest`: login trả JWT có đúng permission claim (parse JWT ra kiểm tra, không mock).
- `mvn clean verify` xanh.

## Lesson placeholder
```
Phase 1 — <PASS/FAIL> — ngày <...>
Điều đúng: ...
Điều vướng: ...
```

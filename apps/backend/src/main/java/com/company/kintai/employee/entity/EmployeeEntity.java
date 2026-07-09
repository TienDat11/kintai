package com.company.kintai.employee.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class EmployeeEntity {
    private Long id;
    private String code;
    private String fullName;
    private String fullNameKana;
    private String email;
    private String phone;
    private String postalCode;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private Long departmentId;
    private String departmentName;
    private Long positionId;
    private String status;
    private byte[] mynumberEncrypted;
    private byte[] mynumberHash;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
    private OffsetDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFullNameKana() { return fullNameKana; }
    public void setFullNameKana(String fullNameKana) { this.fullNameKana = fullNameKana; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public byte[] getMynumberEncrypted() { return mynumberEncrypted; }
    public void setMynumberEncrypted(byte[] mynumberEncrypted) { this.mynumberEncrypted = mynumberEncrypted; }
    public byte[] getMynumberHash() { return mynumberHash; }
    public void setMynumberHash(byte[] mynumberHash) { this.mynumberHash = mynumberHash; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }
}
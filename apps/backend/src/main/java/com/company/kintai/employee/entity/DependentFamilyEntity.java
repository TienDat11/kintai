package com.company.kintai.employee.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class DependentFamilyEntity {
    private Long id;
    private Long employeeId;
    private String fullName;
    private String fullNameKana;
    private String relationship;
    private LocalDate birthDate;
    private byte[] mynumberEncrypted;
    private String socialInsuranceStatus;
    private BigDecimal income;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
    private OffsetDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFullNameKana() { return fullNameKana; }
    public void setFullNameKana(String fullNameKana) { this.fullNameKana = fullNameKana; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public byte[] getMynumberEncrypted() { return mynumberEncrypted; }
    public void setMynumberEncrypted(byte[] mynumberEncrypted) { this.mynumberEncrypted = mynumberEncrypted; }
    public String getSocialInsuranceStatus() { return socialInsuranceStatus; }
    public void setSocialInsuranceStatus(String socialInsuranceStatus) { this.socialInsuranceStatus = socialInsuranceStatus; }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
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

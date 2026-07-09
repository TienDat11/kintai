package com.company.kintai.employee.entity;

import java.time.OffsetDateTime;

public class BankAccountEntity {
    private Long id;
    private Long employeeId;
    private String bankCode;
    private String branchCode;
    private String accountType;
    private byte[] accountNumberEncrypted;
    private byte[] accountNumberHash;
    private String accountHolder;
    private String accountHolderKana;
    private Boolean isPrimary;
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
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public byte[] getAccountNumberEncrypted() { return accountNumberEncrypted; }
    public void setAccountNumberEncrypted(byte[] accountNumberEncrypted) { this.accountNumberEncrypted = accountNumberEncrypted; }
    public byte[] getAccountNumberHash() { return accountNumberHash; }
    public void setAccountNumberHash(byte[] accountNumberHash) { this.accountNumberHash = accountNumberHash; }
    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }
    public String getAccountHolderKana() { return accountHolderKana; }
    public void setAccountHolderKana(String accountHolderKana) { this.accountHolderKana = accountHolderKana; }
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
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

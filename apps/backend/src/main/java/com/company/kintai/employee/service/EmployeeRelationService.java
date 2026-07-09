package com.company.kintai.employee.service;

import com.company.kintai.common.dto.PageResult;
import com.company.kintai.employee.domain.*;
import java.util.List;

public interface EmployeeRelationService {

    // Contract
    Contract findContractById(Long id);
    List<Contract> findContractsByEmployeeId(Long employeeId);

    // BankAccount
    BankAccount findBankAccountById(Long id);
    List<BankAccount> findBankAccountsByEmployeeId(Long employeeId);
    BankAccount createBankAccount(Long employeeId, String bankCode, String branchCode,
                                  String accountType, byte[] accountNumberEncrypted,
                                  byte[] accountNumberHash, String accountHolder,
                                  String accountHolderKana, Boolean isPrimary);

    // DependentFamily
    DependentFamily findDependentFamilyById(Long id);
    List<DependentFamily> findDependentsByEmployeeId(Long employeeId);

    // CommuteRoute
    CommuteRoute findCommuteRouteById(Long id);
    List<CommuteRoute> findCommuteRoutesByEmployeeId(Long employeeId);

    // EmergencyContact
    EmergencyContact findEmergencyContactById(Long id);
    List<EmergencyContact> findEmergencyContactsByEmployeeId(Long employeeId);
}
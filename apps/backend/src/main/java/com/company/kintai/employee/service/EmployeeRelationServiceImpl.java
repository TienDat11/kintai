package com.company.kintai.employee.service;

import com.company.kintai.common.constant.ErrorCode;
import com.company.kintai.common.exception.BusinessException;
import com.company.kintai.common.exception.ResourceNotFoundException;
import com.company.kintai.employee.domain.*;
import com.company.kintai.employee.entity.*;
import com.company.kintai.employee.mapper.*;
import com.company.kintai.common.security.CryptoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeRelationServiceImpl implements EmployeeRelationService {

    private final ContractMapper contractMapper;
    private final BankAccountMapper bankAccountMapper;
    private final DependentFamilyMapper dependentFamilyMapper;
    private final CommuteRouteMapper commuteRouteMapper;
    private final EmergencyContactMapper emergencyContactMapper;
    private final CryptoService cryptoService;

    public EmployeeRelationServiceImpl(
            ContractMapper contractMapper,
            BankAccountMapper bankAccountMapper,
            DependentFamilyMapper dependentFamilyMapper,
            CommuteRouteMapper commuteRouteMapper,
            EmergencyContactMapper emergencyContactMapper,
            CryptoService cryptoService) {
        this.contractMapper = contractMapper;
        this.bankAccountMapper = bankAccountMapper;
        this.dependentFamilyMapper = dependentFamilyMapper;
        this.commuteRouteMapper = commuteRouteMapper;
        this.emergencyContactMapper = emergencyContactMapper;
        this.cryptoService = cryptoService;
    }

    // === Contract ===

    @Override
    public Contract findContractById(Long id) {
        var entity = contractMapper.selectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found: " + id));
        return toContract(entity);
    }

    @Override
    public List<Contract> findContractsByEmployeeId(Long employeeId) {
        return contractMapper.selectByEmployeeId(employeeId).stream().map(this::toContract).toList();
    }

    private Contract toContract(ContractEntity e) {
        return new Contract(e.getId(), e.getEmployeeId(), e.getContractType(),
                e.getStartDate(), e.getEndDate(), e.getBaseSalary(), e.getPaymentType());
    }

    // === BankAccount ===

    @Override
    public BankAccount findBankAccountById(Long id) {
        var entity = bankAccountMapper.selectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount not found: " + id));
        return toBankAccount(entity);
    }

    @Override
    public List<BankAccount> findBankAccountsByEmployeeId(Long employeeId) {
        return bankAccountMapper.selectByEmployeeId(employeeId).stream().map(this::toBankAccount).toList();
    }

    @Override
    @Transactional
    public BankAccount createBankAccount(Long employeeId, String bankCode, String branchCode,
                                           String accountType, byte[] accountNumberEncrypted,
                                           byte[] accountNumberHash, String accountHolder,
                                           String accountHolderKana, Boolean isPrimary) {
        if (Boolean.TRUE.equals(isPrimary)) {
            // In cùng 1 transaction: set all existing primary = false before inserting new primary
            bankAccountMapper.updateSetNonPrimaryByEmployeeId(employeeId);
        }
        var entity = new BankAccountEntity();
        entity.setEmployeeId(employeeId);
        entity.setBankCode(bankCode);
        entity.setBranchCode(branchCode);
        entity.setAccountType(accountType);
        entity.setAccountNumberEncrypted(accountNumberEncrypted);
        entity.setAccountNumberHash(accountNumberHash);
        entity.setAccountHolder(accountHolder);
        entity.setAccountHolderKana(accountHolderKana);
        entity.setIsPrimary(isPrimary);
        entity.setCreatedBy("system");
        entity.setUpdatedBy("system");
        bankAccountMapper.insert(entity);
        return toBankAccount(entity);
    }

    private BankAccount toBankAccount(BankAccountEntity e) {
        String masked = e.getAccountNumberEncrypted() != null
                ? CryptoService.mask(cryptoService.decrypt(
                        java.util.Base64.getEncoder().encodeToString(e.getAccountNumberEncrypted())))
                : null;
        return new BankAccount(e.getId(), e.getEmployeeId(), e.getBankCode(),
                e.getBranchCode(), e.getAccountType(), masked, e.getAccountHolder(),
                e.getAccountHolderKana(), e.getIsPrimary());
    }

    // === DependentFamily ===

    @Override
    public DependentFamily findDependentFamilyById(Long id) {
        return dependentFamilyMapper.selectById(id)
                .map(this::toDependentFamily)
                .orElseThrow(() -> new ResourceNotFoundException("DependentFamily not found: " + id));
    }

    @Override
    public List<DependentFamily> findDependentsByEmployeeId(Long employeeId) {
        return dependentFamilyMapper.selectByEmployeeId(employeeId)
                .stream().map(this::toDependentFamily).toList();
    }

    private DependentFamily toDependentFamily(DependentFamilyEntity e) {
        return new DependentFamily(e.getId(), e.getEmployeeId(), e.getFullName(),
                e.getFullNameKana(), e.getRelationship(), e.getBirthDate(),
                e.getSocialInsuranceStatus(), e.getIncome());
    }

    // === CommuteRoute ===

    @Override
    public CommuteRoute findCommuteRouteById(Long id) {
        return commuteRouteMapper.selectById(id)
                .map(this::toCommuteRoute)
                .orElseThrow(() -> new ResourceNotFoundException("CommuteRoute not found: " + id));
    }

    @Override
    public List<CommuteRoute> findCommuteRoutesByEmployeeId(Long employeeId) {
        return commuteRouteMapper.selectByEmployeeId(employeeId)
                .stream().map(this::toCommuteRoute).toList();
    }

    private CommuteRoute toCommuteRoute(CommuteRouteEntity e) {
        return new CommuteRoute(e.getId(), e.getEmployeeId(), e.getFromStation(),
                e.getToStation(), e.getDistanceKm(), e.getMonthlyPassCost(),
                e.getValidFrom(), e.getValidTo());
    }

    // === EmergencyContact ===

    @Override
    public EmergencyContact findEmergencyContactById(Long id) {
        return emergencyContactMapper.selectById(id)
                .map(this::toEmergencyContact)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyContact not found: " + id));
    }

    @Override
    public List<EmergencyContact> findEmergencyContactsByEmployeeId(Long employeeId) {
        return emergencyContactMapper.selectByEmployeeId(employeeId)
                .stream().map(this::toEmergencyContact).toList();
    }

    private EmergencyContact toEmergencyContact(EmergencyContactEntity e) {
        return new EmergencyContact(e.getId(), e.getEmployeeId(), e.getName(),
                e.getRelationship(), e.getPhone(), e.getAddress(), e.getPriority());
    }
}
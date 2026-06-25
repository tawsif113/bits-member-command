package com.bits.member.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.application.handler.CommandHandler;
import com.bits.ddd.application.service.MessageProcessor;
import com.bits.ddd.application.service.SourceDataContext;
import com.bits.ddd.application.service.SourceDataProvider;
import com.bits.ddd.infra.persistence.service.DomainPersistenceService;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.DeduplicationResult;
import com.bits.member.application.dto.MemberSourceData;
import com.bits.member.application.mapper.MemberDataMapper;
import com.bits.member.application.service.DeduplicationService;
import com.bits.member.application.service.MemberConcurrencyLockService;
import com.bits.member.application.service.MemberNumberGenerator;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.enums.MemberErrorCode;
import com.bits.member.domain.param.MemberCreationData;
import com.bits.member.infrastructure.persistence.document.*;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
@RegisterCommandHandler
public class CreateMemberCommandHandler implements CommandHandler<CreateMemberCommand> {

    @PersistDomain
    private final DomainPersistenceService<Member, String> persistenceService;
    private final SourceDataProvider<CreateMemberCommand> sourceDataProvider;
    private final MessageProcessor messageProcessor;
    private final MemberConcurrencyLockService lockService;
    private final DeduplicationService deduplicationService;
    private final MemberNumberGenerator memberNumberGenerator;

    public CreateMemberCommandHandler(
            DomainPersistenceService<Member, String> persistenceService,
            SourceDataProvider<CreateMemberCommand> sourceDataProvider,
            MessageProcessor messageProcessor,
            MemberConcurrencyLockService lockService,
            DeduplicationService deduplicationService,
            MemberNumberGenerator memberNumberGenerator) {
        this.persistenceService = persistenceService;
        this.sourceDataProvider = sourceDataProvider;
        this.messageProcessor = messageProcessor;
        this.lockService = lockService;
        this.deduplicationService = deduplicationService;
        this.memberNumberGenerator = memberNumberGenerator;
    }

    @Override
    public void handle(CreateMemberCommand command) {
        String identityLockKey = lockService.identityLockKey(command);
        String branchProjectGroupLockKey = lockService.branchProjectGroupLockKey(command);
        boolean identityLocked = false;
        boolean branchProjectGroupLocked = false;
        try {
            identityLocked = lockService.acquire(identityLockKey);
            branchProjectGroupLocked = lockService.acquire(branchProjectGroupLockKey);

            if (!identityLocked || !branchProjectGroupLocked) {
                throw new DomainValidationException(
                        MemberErrorCode.MEMBER_CREATION_LOCKED.getCode(),
                        "Member Creation process is on going. Please wait and check after few minutes."
                );
            }

            DeduplicationResult deduplicationResult = deduplicationService.checkForCreate(command);
            SourceDataContext context = sourceDataProvider.provide(command);
            MemberSourceData sourceData = mapToMemberSourceData(command, context);

            String memberNo = memberNumberGenerator.nextMemberNo(command, sourceData);
            LocalDate businessDate = sourceData.getPhysicalOfficeInfo() == null
                    ? null
                    : sourceData.getPhysicalOfficeInfo().businessDate();

            MemberCreationData creationData = MemberDataMapper.toCreationData(
                    command, sourceData, deduplicationResult, businessDate, memberNo);

            Member member = Member.create(creationData);
            persistenceService.persist(member);
            messageProcessor.publish(member.getEvents());
            member.clearEvents();
        } finally {
            if (branchProjectGroupLocked) {
                lockService.release(branchProjectGroupLockKey);
            }
            if (identityLocked) {
                lockService.release(identityLockKey);
            }
        }
    }

    private MemberSourceData mapToMemberSourceData(CreateMemberCommand command, SourceDataContext context) {
        MemberSourceData sourceData = new MemberSourceData();

        PhysicalOfficeInfoDocument officeDoc = context.get("physicalOfficeInfo", PhysicalOfficeInfoDocument.class);
        sourceData.setPhysicalOfficeInfo(mapPhysicalOfficeInfo(officeDoc));

        ProjectInfoDocument projectDoc = context.get("projectInfo", ProjectInfoDocument.class);
        sourceData.setProjectInfo(mapProjectInfo(projectDoc));

        GroupInfoDocument groupDoc = context.get("groupInfo", GroupInfoDocument.class);
        sourceData.setGroupInfo(mapGroupInfo(groupDoc));

        EmployeeCoreInfoDocument poDoc = context.get("employeeCoreInfo", EmployeeCoreInfoDocument.class);
        sourceData.setEmployeeCoreInfo(mapEmployeeCoreInfo(poDoc));

        MemberClassificationDocument classDoc = context.get("memberClassification", MemberClassificationDocument.class);
        sourceData.setMemberClassification(mapMemberClassification(classDoc));

        SavingsProductDocument productDoc = context.get("savingsProduct", SavingsProductDocument.class);
        sourceData.setSavingsProduct(mapSavingsProduct(productDoc));

        if (command.getOccupationId() != null) {
            OccupationDocument occDoc = context.get("occupation", OccupationDocument.class);
            sourceData.setOccupation(mapOccupation(occDoc));
        }

        java.util.List<com.bits.member.application.dto.sourcedata.Thana> thanaList = new java.util.ArrayList<>();
        if (command.getPresentThanaId() != null) {
            ThanaDocument thanaDoc = context.get("presentThana", ThanaDocument.class);
            thanaList.add(mapThana(thanaDoc));
        }
        if (command.getPermanentThanaId() != null) {
            ThanaDocument thanaDoc = context.get("permanentThana", ThanaDocument.class);
            thanaList.add(mapThana(thanaDoc));
        }
        sourceData.setThanas(thanaList);

        return sourceData;
    }

    private com.bits.member.application.dto.sourcedata.Thana mapThana(ThanaDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.Thana dto = new com.bits.member.application.dto.sourcedata.Thana();
        dto.setId(doc.id());
        dto.setDistrictId(doc.getDistrictId());
        dto.setThanaName(doc.getThanaName());
        dto.setThanaCode(doc.getThanaCode());
        dto.setHrThanaId(doc.getHrThanaId());
        dto.setCountryId(doc.getCountryId());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.PhysicalOfficeInfo mapPhysicalOfficeInfo(PhysicalOfficeInfoDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.PhysicalOfficeInfo dto = new com.bits.member.application.dto.sourcedata.PhysicalOfficeInfo();
        dto.setId(doc.id());
        dto.setOfficeCode(doc.getOfficeCode());
        dto.setOfficeRefCode(doc.getOfficeRefCode());
        dto.setOfficeName(doc.getOfficeName());
        dto.setOfficeTypeId(doc.getOfficeTypeId());
        dto.setAreaTypeId(doc.getAreaTypeId());
        dto.setSetupDate(doc.getSetupDate());
        dto.setEffectiveDate(doc.getEffectiveDate());
        dto.setRegisteredAddressId(doc.getRegisteredAddressId());
        dto.setBusinessAddressId(doc.getBusinessAddressId());
        dto.setOfficeStatusId(doc.getOfficeStatusId());
        dto.setOfficeCountryId(doc.getOfficeCountryId());
        dto.setParentOfficeId(doc.getParentOfficeId());
        dto.setMobileNo(doc.getMobileNo());
        dto.setIsHrOffice(doc.getIsHrOffice());
        dto.setIsMfOffice(doc.getIsMfOffice());
        dto.setIsDevOffice(doc.getIsDevOffice());
        dto.setIsUpazilaAccountsOffice(doc.getIsUpazilaAccountsOffice());
        dto.setIsIndp(doc.getIsIndp());
        dto.setVerifyTB(doc.getVerifyTB());
        dto.setVerifyPortfolio(doc.getVerifyPortfolio());
        dto.setVerifySavings(doc.getVerifySavings());
        dto.setOfficeHierarchyId(doc.getOfficeHierarchyId());
        dto.setReportingToId(doc.getReportingToId());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setMfBranchId(doc.getMfBranchId());
        dto.setMfRefCode(doc.getMfRefCode());
        dto.setHasOperation(doc.getHasOperation());
        dto.setBkashWalletNo(doc.getBkashWalletNo());
        dto.setOperationCategory(doc.getOperationCategory());
        dto.setParentProjectId(doc.getParentProjectId());
        dto.setOfficeType(doc.getOfficeType());
        dto.setBusinessDayStatus(doc.getBusinessDayStatus());
        dto.setBusinessDate(doc.getBusinessDate());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.ProjectInfo mapProjectInfo(ProjectInfoDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.ProjectInfo dto = new com.bits.member.application.dto.sourcedata.ProjectInfo();
        dto.setId(doc.id());
        dto.setProposalId(doc.getProposalId());
        dto.setProjectCountryId(doc.getProjectCountryId());
        dto.setProjectCode(doc.getProjectCode());
        dto.setProjectRefCode(doc.getProjectRefCode());
        dto.setProjectName(doc.getProjectName());
        dto.setProjectDescription(doc.getProjectDescription());
        dto.setProjectShortCode(doc.getProjectShortCode());
        dto.setProjectSetupDate(doc.getProjectSetupDate());
        dto.setProjectEffectiveDate(doc.getProjectEffectiveDate());
        dto.setProjectStartDate(doc.getProjectStartDate());
        dto.setProjectEndDate(doc.getProjectEndDate());
        dto.setDomainStatusId(doc.getDomainStatusId());
        dto.setProgramInfoId(doc.getProgramInfoId());
        dto.setBookClosing(doc.getBookClosing());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setParentProjectInfoId(doc.getParentProjectInfoId());
        dto.setIsIndependent(doc.getIsIndependent());
        dto.setIsOverhead(doc.getIsOverhead());
        dto.setHoType(doc.getHoType());
        dto.setBoType(doc.getBoType());
        dto.setMfProjectRefCode(doc.getMfProjectRefCode());
        dto.setIsNgoBeuro(doc.getIsNgoBeuro());
        dto.setBeuroFromDate(doc.getBeuroFromDate());
        dto.setBeuroToDate(doc.getBeuroToDate());
        dto.setIsTrendxProject(doc.getIsTrendxProject());
        dto.setIsSmartCollection(doc.getIsSmartCollection());
        dto.setHasMfOperation(doc.getHasMfOperation());
        dto.setProjectStatusId(doc.getProjectStatusId());
        dto.setStartMonth(doc.getStartMonth());
        dto.setEndMonth(doc.getEndMonth());
        dto.setMfEndMonth(doc.getMfEndMonth());
        dto.setHasFinOperation(doc.getHasFinOperation());
        dto.setSourceOfFundId(doc.getSourceOfFundId());
        dto.setForeignCurrency(doc.getForeignCurrency());
        dto.setLocalCurrency(doc.getLocalCurrency());
        dto.setForeignAmount(doc.getForeignAmount());
        dto.setLocalAmount(doc.getLocalAmount());
        dto.setSigningDate(doc.getSigningDate());
        dto.setProjectStatus(doc.getProjectStatus());
        dto.setAssociationType(doc.getAssociationType());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.ProjectPolicyInfo mapProjectPolicyInfo(ProjectPolicyInfoDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.ProjectPolicyInfo dto = new com.bits.member.application.dto.sourcedata.ProjectPolicyInfo();
        dto.setId(doc.id());
        dto.setProjectInfoId(doc.getProjectInfoId());
        dto.setAssociationType(doc.getAssociationType());
        dto.setHasLoans(doc.getHasLoans());
        dto.setHasSavings(doc.getHasSavings());
        dto.setHasLoanSecurity(doc.getHasLoanSecurity());
        dto.setHasLoanExposureLimit(doc.getHasLoanExposureLimit());
        dto.setHasPassbook(doc.getHasPassbook());
        dto.setPassbookPrice(doc.getPassbookPrice());
        dto.setIsTimeLimitRequired(doc.getIsTimeLimitRequired());
        dto.setNewLoanTimeLimit(doc.getNewLoanTimeLimit());
        dto.setRepeatLoanTimeLimit(doc.getRepeatLoanTimeLimit());
        dto.setHasDeathBenefit(doc.getHasDeathBenefit());
        dto.setDeathBenefitAmount(doc.getDeathBenefitAmount());
        dto.setMaxWriteOffAmount(doc.getMaxWriteOffAmount());
        dto.setLoanLossProvisionPercentage(doc.getLoanLossProvisionPercentage());
        dto.setHasPartialAdjustment(doc.getHasPartialAdjustment());
        dto.setHasMatchFund(doc.getHasMatchFund());
        dto.setIsTUP(doc.getIsTUP());
        dto.setIsWithDrawAllowed(doc.getIsWithDrawAllowed());
        dto.setWithdrawPercentage(doc.getWithdrawPercentage());
        dto.setNoOfAllowedWithdrawal(doc.getNoOfAllowedWithdrawal());
        dto.setHasMembershipFee(doc.getHasMembershipFee());
        dto.setHasFireInsurance(doc.getHasFireInsurance());
        dto.setFeeAmount(doc.getFeeAmount());
        dto.setNewBalancePercentage(doc.getNewBalancePercentage());
        dto.setNewTransactionDuration(doc.getNewTransactionDuration());
        dto.setNewTransactionPercentage(doc.getNewTransactionPercentage());
        dto.setNewBalanceTransactionChecking(doc.getNewBalanceTransactionChecking());
        dto.setRepeatBalancePercentage(doc.getRepeatBalancePercentage());
        dto.setRepeatTransactionDuration(doc.getRepeatTransactionDuration());
        dto.setRepeatTransactionPercentage(doc.getRepeatTransactionPercentage());
        dto.setSavingsMatchingPolicyId(doc.getSavingsMatchingPolicyId());
        dto.setAllowForCurrentSavings(doc.getAllowForCurrentSavings());
        dto.setTargetAmountNonactiveLoan(doc.getTargetAmountNonactiveLoan());
        dto.setSavingsPercentage(doc.getSavingsPercentage());
        dto.setCollectionFrequency(doc.getCollectionFrequency());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.GroupInfo mapGroupInfo(GroupInfoDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.GroupInfo dto = new com.bits.member.application.dto.sourcedata.GroupInfo();
        dto.setId(doc.id());
        dto.setGroupCode(doc.getGroupCode());
        dto.setGroupName(doc.getGroupName());
        dto.setAssignedPoId(doc.getAssignedPoId());
        dto.setOrientationDate(doc.getOrientationDate());
        dto.setGroupCreationDate(doc.getGroupCreationDate());
        dto.setLastPoAssignedDate(doc.getLastPoAssignedDate());
        dto.setGroupReferenceNumber(doc.getGroupReferenceNumber());
        dto.setSpotAddress(doc.getSpotAddress());
        dto.setGroupStatusId(doc.getGroupStatusId());
        dto.setDomainStatusId(doc.getDomainStatusId());
        dto.setCloseReasonId(doc.getCloseReasonId());
        dto.setClosingDate(doc.getClosingDate());
        dto.setMeetingDayId(doc.getMeetingDayId());
        dto.setMeetingTime(doc.getMeetingTime());
        dto.setDemarcationArea(doc.getDemarcationArea());
        dto.setWeekNumber(doc.getWeekNumber());
        dto.setLoanCollectionFrequencyId(doc.getLoanCollectionFrequencyId());
        dto.setLoanCollectionStartDate(doc.getLoanCollectionStartDate());
        dto.setSavingsCollectionFrequencyId(doc.getSavingsCollectionFrequencyId());
        dto.setSavingsCollectionStartDate(doc.getSavingsCollectionStartDate());
        dto.setNextCollectionDate(doc.getNextCollectionDate());
        dto.setBranchInfoId(doc.getBranchInfoId());
        dto.setProjectInfoId(doc.getProjectInfoId());
        dto.setGroupScannedForm(doc.getGroupScannedForm());
        dto.setIsTransferredGroup(doc.getIsTransferredGroup());
        dto.setGroupCategoryId(doc.getGroupCategoryId());
        dto.setVoCategoryId(doc.getVoCategoryId());
        dto.setServiceTerritoryId(doc.getServiceTerritoryId());
        dto.setLongitude(doc.getLongitude());
        dto.setLatitude(doc.getLatitude());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setApplicableGender(doc.getApplicableGender());
        dto.setGroupStatus(doc.getGroupStatus());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.EmployeeCoreInfo mapEmployeeCoreInfo(EmployeeCoreInfoDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.EmployeeCoreInfo dto = new com.bits.member.application.dto.sourcedata.EmployeeCoreInfo();
        dto.setId(doc.id());
        dto.setHomeCountryId(doc.getHomeCountryId());
        dto.setPinNo(doc.getPinNo());
        dto.setJoiningDate(doc.getJoiningDate());
        dto.setSalutationId(doc.getSalutationId());
        dto.setFirstName(doc.getFirstName());
        dto.setMiddleName(doc.getMiddleName());
        dto.setLastName(doc.getLastName());
        dto.setNickName(doc.getNickName());
        dto.setGenderId(doc.getGenderId());
        dto.setEmployeeDob(doc.getEmployeeDob());
        dto.setDomainStatusId(doc.getDomainStatusId());
        dto.setEmployeeStatusId(doc.getEmployeeStatusId());
        dto.setProvisionEndDate(doc.getProvisionEndDate());
        dto.setCurJobStatusId(doc.getCurJobStatusId());
        dto.setIsOnDeputation(doc.getIsOnDeputation());
        dto.setDeputationEndDate(doc.getDeputationEndDate());
        dto.setCurJobStartDate(doc.getCurJobStartDate());
        dto.setApprovalStatusId(doc.getApprovalStatusId());
        dto.setApprovalDate(doc.getApprovalDate());
        dto.setIsIssuedLetter(doc.getIsIssuedLetter());
        dto.setIsIssuedRetireLetter(doc.getIsIssuedRetireLetter());
        dto.setEmployeeLevelId(doc.getEmployeeLevelId());
        dto.setEDesignationId(doc.getEDesignationId());
        dto.setFDesignationId(doc.getFDesignationId());
        dto.setProgramTypeId(doc.getProgramTypeId());
        dto.setCoreProjectId(doc.getCoreProjectId());
        dto.setCoreProgramId(doc.getCoreProgramId());
        dto.setDepartmentId(doc.getDepartmentId());
        dto.setSupervisorId(doc.getSupervisorId());
        dto.setNoticePeriod(doc.getNoticePeriod());
        dto.setWorkingHour(doc.getWorkingHour());
        dto.setWorkingDayInWeek(doc.getWorkingDayInWeek());
        dto.setNomineeForm(doc.getNomineeForm());
        dto.setIsExpatriate(doc.getIsExpatriate());
        dto.setEmailAddress(doc.getEmailAddress());
        dto.setUnitId(doc.getUnitId());
        dto.setRollNo(doc.getRollNo());
        dto.setRecruitReqNo(doc.getRecruitReqNo());
        dto.setPreviousEmpCoreInfoId(doc.getPreviousEmpCoreInfoId());
        dto.setEmployeeName(doc.getEmployeeName());
        dto.setNationalIdNo(doc.getNationalIdNo());
        dto.setSmartNIDNo(doc.getSmartNIDNo());
        dto.setPayGroupId(doc.getPayGroupId());
        dto.setCurrencyName(doc.getCurrencyName());
        dto.setIsIncrement(doc.getIsIncrement());
        dto.setIsLeave(doc.getIsLeave());
        dto.setIsAttendance(doc.getIsAttendance());
        dto.setJoiningDateASPA(doc.getJoiningDateASPA());
        dto.setReligionId(doc.getReligionId());
        dto.setIsReceive(doc.getIsReceive());
        dto.setIsPostRefRequired(doc.getIsPostRefRequired());
        dto.setIsPostRefCompleted(doc.getIsPostRefCompleted());
        dto.setIsBackCheckCompleted(doc.getIsBackCheckCompleted());
        dto.setOrganogram(doc.getOrganogram());
        dto.setPosition(doc.getPosition());
        dto.setRefErecruitId(doc.getRefErecruitId());
        dto.setTinTypeId(doc.getTinTypeId());
        dto.setTinNumber(doc.getTinNumber());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setName(doc.getName());
        dto.setEmployeeCode(doc.getEmployeeCode());
        dto.setBranchInfoId(doc.getBranchInfoId());
        dto.setProjectInfoId(doc.getProjectInfoId());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.MemberClassification mapMemberClassification(MemberClassificationDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.MemberClassification dto = new com.bits.member.application.dto.sourcedata.MemberClassification();
        dto.setId(doc.id());
        dto.setCategoryName(doc.getCategoryName());
        dto.setAgeFrom(doc.getAgeFrom());
        dto.setAgeTo(doc.getAgeTo());
        dto.setIsAllowedLoan(doc.getIsAllowedLoan());
        dto.setHasSavings(doc.getHasSavings());
        dto.setHasRMG(doc.getHasRMG());
        dto.setHasERMG(doc.getHasERMG());
        dto.setIsDisallowMemberFees(doc.getIsDisallowMemberFees());
        dto.setDomainStatusId(doc.getDomainStatusId());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setAllowedLoan(doc.getAllowedLoan());
        dto.setDisallowMemberFees(doc.getDisallowMemberFees());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.SavingsProduct mapSavingsProduct(SavingsProductDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.SavingsProduct dto = new com.bits.member.application.dto.sourcedata.SavingsProduct();
        dto.setId(doc.id());
        dto.setCountryId(doc.getCountryId());
        dto.setProductCode(doc.getProductCode());
        dto.setProductRefCode(doc.getProductRefCode());
        dto.setProductName(doc.getProductName());
        dto.setSetupDate(doc.getSetupDate());
        dto.setProductTypeId(doc.getProductTypeId());
        dto.setInterestRate(doc.getInterestRate());
        dto.setRateTypeId(doc.getRateTypeId());
        dto.setDescription(doc.getDescription());
        dto.setSavingsProductSubTypeId(doc.getSavingsProductSubTypeId());
        dto.setClientPercentageLower(doc.getClientPercentageLower());
        dto.setClientPercentageUpper(doc.getClientPercentageUpper());
        dto.setProfitPaymentDay(doc.getProfitPaymentDay());
        dto.setHasMatchFund(doc.getHasMatchFund());
        dto.setDomainStatusId(doc.getDomainStatusId());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setVersion(doc.getVersion());
        dto.setProductType(doc.getProductType());
        dto.setCollectionFrequency(doc.getCollectionFrequency());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.SavingsProductPolicy mapSavingsProductPolicy(SavingsProductPolicyDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.SavingsProductPolicy dto = new com.bits.member.application.dto.sourcedata.SavingsProductPolicy();
        dto.setId(doc.id());
        dto.setNoOfAllowedWithdrawal(doc.getNoOfAllowedWithdrawal());
        dto.setMinimumBalance(doc.getMinimumBalance());
        dto.setMinDepositAmount(doc.getMinDepositAmount());
        dto.setCalculationFrequency(doc.getCalculationFrequency());
        dto.setProvisionFrequency(doc.getProvisionFrequency());
        dto.setCalculationMethod(doc.getCalculationMethod());
        dto.setCreditFrequency(doc.getCreditFrequency());
        dto.setPolicyRepaymentsPlanId(doc.getPolicyRepaymentsPlanId());
        dto.setSavingsProductId(doc.getSavingsProductId());
        dto.setDomainStatusId(doc.getDomainStatusId());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.Relationship mapRelationship(RelationshipDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.Relationship dto = new com.bits.member.application.dto.sourcedata.Relationship();
        dto.setId(doc.id());
        dto.setName(doc.getName());
        dto.setDescription(doc.getDescription());
        dto.setIsRelative(doc.getIsRelative());
        dto.setStatusId(doc.getStatusId());
        dto.setRelative(doc.getRelative());
        dto.setSpouseRelationship(doc.getSpouseRelationship());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.Country mapCountry(CountryDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.Country dto = new com.bits.member.application.dto.sourcedata.Country();
        dto.setId(doc.id());
        dto.setName(doc.getName());
        dto.setCode(doc.getCode());
        dto.setShortName(doc.getShortName());
        dto.setShortCode(doc.getShortCode());
        dto.setCallingCode(doc.getCallingCode());
        dto.setHasOperation(doc.getHasOperation());
        dto.setLocalCurrencyName(doc.getLocalCurrencyName());
        dto.setForeignCurrencyName(doc.getForeignCurrencyName());
        dto.setMinimumDenomination(doc.getMinimumDenomination());
        dto.setTimeZone(doc.getTimeZone());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.MemberStatus mapMemberStatus(MemberStatusDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.MemberStatus dto = new com.bits.member.application.dto.sourcedata.MemberStatus();
        dto.setId(doc.id());
        dto.setName(doc.getName());
        dto.setDescription(doc.getDescription());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.Occupation mapOccupation(OccupationDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.Occupation dto = new com.bits.member.application.dto.sourcedata.Occupation();
        dto.setId(doc.id());
        dto.setName(doc.getName());
        dto.setDescription(doc.getDescription());
        dto.setOccupationCode(doc.getOccupationCode());
        dto.setOccupationName(doc.getOccupationName());
        dto.setActive(doc.getActive());
        return dto;
    }

    private com.bits.member.application.dto.sourcedata.SavingsAccount mapSavingsAccount(SavingsAccountDocument doc) {
        if (doc == null) return null;
        com.bits.member.application.dto.sourcedata.SavingsAccount dto = new com.bits.member.application.dto.sourcedata.SavingsAccount();
        dto.setId(doc.id());
        dto.setCountryId(doc.getCountryId());
        dto.setAccountNo(doc.getAccountNo());
        dto.setAccountName(doc.getAccountName());
        dto.setAccountTypeId(doc.getAccountTypeId());
        dto.setCreationDate(doc.getCreationDate());
        dto.setClosingDate(doc.getClosingDate());
        dto.setInitialDeposit(doc.getInitialDeposit());
        dto.setInstallmentAmount(doc.getInstallmentAmount());
        dto.setOfficeInfoId(doc.getOfficeInfoId());
        dto.setGroupInfoId(doc.getGroupInfoId());
        dto.setProjectInfoId(doc.getProjectInfoId());
        dto.setSavingsProductId(doc.getSavingsProductId());
        dto.setInterestRate(doc.getInterestRate());
        dto.setCalculationFrequency(doc.getCalculationFrequency());
        dto.setProvisionFrequency(doc.getProvisionFrequency());
        dto.setCalculationMethod(doc.getCalculationMethod());
        dto.setCreditFrequency(doc.getCreditFrequency());
        dto.setAccountStatusId(doc.getAccountStatusId());
        dto.setPortfolioStatusId(doc.getPortfolioStatusId());
        dto.setDomainStatusId(doc.getDomainStatusId());
        dto.setSavingsBalance(doc.getSavingsBalance());
        dto.setProvisionedInterest(doc.getProvisionedInterest());
        dto.setCalculatedInterest(doc.getCalculatedInterest());
        dto.setIsTransferredSavings(doc.getIsTransferredSavings());
        dto.setHasMatchedFund(doc.getHasMatchedFund());
        dto.setMatchFundMaxAmount(doc.getMatchFundMaxAmount());
        dto.setMatchRatio(doc.getMatchRatio());
        dto.setMatchFundExpiredDate(doc.getMatchFundExpiredDate());
        dto.setCohortMappingId(doc.getCohortMappingId());
        dto.setNextCollectionDate(doc.getNextCollectionDate());
        dto.setLastTransferredDate(doc.getLastTransferredDate());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setUpdatedBy(doc.getUpdatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setLastUpdated(doc.getLastUpdated());
        dto.setCurrentSavingsAccountType(doc.getCurrentSavingsAccountType());
        dto.setCurrentAccountMobile(doc.getCurrentAccountMobile());
        dto.setOtpSendType(doc.getOtpSendType());
        dto.setLastInterestCalculationDate(doc.getLastInterestCalculationDate());
        dto.setIsInterMemberTransferred(doc.getIsInterMemberTransferred());
        dto.setUidCycleNo(doc.getUidCycleNo());
        dto.setUidStatus(doc.getUidStatus());
        dto.setActive(doc.getActive());
        return dto;
    }

}

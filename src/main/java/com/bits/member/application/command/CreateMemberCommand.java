package com.bits.member.application.command;

import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.NomineeInfo;
import com.bits.ddd.shared.messaging.CommandMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateMemberCommand extends CommandMessage {

    private final String operatorId;
    private final String branchInfoId;
    private final String projectInfoId;
    private final String groupInfoId;
    private final String assignedPoId;
    private final String memberClassificationId;
    private final String savingsProductId;
    private final BigDecimal targetAmount;
    private final LocalDate applicationDate;
    private final String fName;
    private final String mName;
    private final String lName;
    private final String genderId;
    private final String maritalStatusId;
    private final LocalDate dateOfBirth;
    private final String occupationId;
    private final String fatherName;
    private final String motherName;
    private final String spouseName;
    private final String nationalId;
    private final String smartCardId;
    private final String passportNo;
    private final String drivingLicenseNo;
    private final String photoIdNo;
    private final Integer otherIdTypeId;
    private final String otherIdTypeNo;
    private final String contactNo;
    private final String presentAddress;
    private final String presentThanaId;
    private final String permanentAddress;
    private final String permanentThanaId;
    private final String passbookNo;
    private final List<NomineeInfo> nominees;
    private final GuarantorInfo guarantorInfo;
    private final String bankId;
    private final String bankBranchId;
    private final String bankAccountNumber;
    private final String routingNumber;
    private final String tinNumber;
    private final String memberCustomField;
    private final String academicQualificationId;
    private final String referredBy;

    public CreateMemberCommand(
            String tracerId,
            String operatorId,
            String branchInfoId,
            String projectInfoId,
            String groupInfoId,
            String assignedPoId,
            String memberClassificationId,
            String savingsProductId,
            BigDecimal targetAmount,
            LocalDate applicationDate,
            String fName,
            String mName,
            String lName,
            String genderId,
            String maritalStatusId,
            LocalDate dateOfBirth,
            String occupationId,
            String fatherName,
            String motherName,
            String spouseName,
            String nationalId,
            String smartCardId,
            String passportNo,
            String drivingLicenseNo,
            String photoIdNo,
            Integer otherIdTypeId,
            String otherIdTypeNo,
            String contactNo,
            String presentAddress,
            String presentThanaId,
            String permanentAddress,
            String permanentThanaId,
            String passbookNo,
            List<NomineeInfo> nominees,
            GuarantorInfo guarantorInfo,
            String bankId,
            String bankBranchId,
            String bankAccountNumber,
            String routingNumber,
            String tinNumber,
            String memberCustomField,
            String academicQualificationId,
            String referredBy) {
        super(tracerId);
        this.operatorId = operatorId;
        this.branchInfoId = branchInfoId;
        this.projectInfoId = projectInfoId;
        this.groupInfoId = groupInfoId;
        this.assignedPoId = assignedPoId;
        this.memberClassificationId = memberClassificationId;
        this.savingsProductId = savingsProductId;
        this.targetAmount = targetAmount;
        this.applicationDate = applicationDate;
        this.fName = fName;
        this.mName = mName;
        this.lName = lName;
        this.genderId = genderId;
        this.maritalStatusId = maritalStatusId;
        this.dateOfBirth = dateOfBirth;
        this.occupationId = occupationId;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.spouseName = spouseName;
        this.nationalId = nationalId;
        this.smartCardId = smartCardId;
        this.passportNo = passportNo;
        this.drivingLicenseNo = drivingLicenseNo;
        this.photoIdNo = photoIdNo;
        this.otherIdTypeId = otherIdTypeId;
        this.otherIdTypeNo = otherIdTypeNo;
        this.contactNo = contactNo;
        this.presentAddress = presentAddress;
        this.presentThanaId = presentThanaId;
        this.permanentAddress = permanentAddress;
        this.permanentThanaId = permanentThanaId;
        this.passbookNo = passbookNo;
        this.nominees = nominees;
        this.guarantorInfo = guarantorInfo;
        this.bankId = bankId;
        this.bankBranchId = bankBranchId;
        this.bankAccountNumber = bankAccountNumber;
        this.routingNumber = routingNumber;
        this.tinNumber = tinNumber;
        this.memberCustomField = memberCustomField;
        this.academicQualificationId = academicQualificationId;
        this.referredBy = referredBy;
    }
}

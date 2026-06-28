package com.bits.member.presentation.controller.dto;

import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.NomineeInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateMemberRequest(
        String operatorId,
        String branchInfoId,
        String projectInfoId,
        String groupInfoId,
        String assignedPoId,
        String memberClassificationId,
        String savingsProductId,
        BigDecimal targetAmount,
        LocalDate applicationDate,
        String firstName,
        String middleName,
        String lastName,
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
}

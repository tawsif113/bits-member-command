package com.bits.member.domain.param;

import com.bits.member.application.dto.DeduplicationResult;
import com.bits.member.application.dto.MemberSourceData;
import com.bits.member.domain.entity.ContactInfo;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.NomineeInfo;
import com.bits.member.domain.entity.PersonalInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MemberCreationData(
        String tracerId,
        String operatorId,
        LocalDate businessDate,
        String memberNo,
        String countryId,
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
        String memberCustomField,
        String tinNumber,
        String referredBy,
        String passbookNo,
        String bankId,
        String bankBranchId,
        String bankAccountNumber,
        String routingNumber,
        String academicQualificationId,
        PersonalInfo personalInfo,
        ContactInfo contactInfo,
        List<NomineeInfo> nominees,
        GuarantorInfo guarantorInfo,
        MemberSourceData sourceData,
        DeduplicationResult deduplicationResult) {
}

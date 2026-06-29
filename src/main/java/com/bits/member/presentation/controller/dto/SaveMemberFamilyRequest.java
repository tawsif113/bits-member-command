package com.bits.member.presentation.controller.dto;

import com.bits.member.domain.entity.FamilyInfo;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.GuardianInfo;
import com.bits.member.domain.entity.NomineeInfo;
import java.util.List;

public record SaveMemberFamilyRequest(
        String operatorId,
        List<NomineeInfo> nominees,
        GuardianInfo guardianInfo,
        GuarantorInfo guarantorInfo,
        FamilyInfo familyInfo) {
}

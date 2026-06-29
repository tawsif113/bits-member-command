package com.bits.member.domain.param;

import com.bits.member.application.dto.sourcedata.Relationship;
import com.bits.member.domain.entity.FamilyInfo;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.GuardianInfo;
import com.bits.member.domain.entity.NomineeInfo;
import java.util.List;

public record MemberFamilySaveData(
        String traceId,
        String operatorId,
        List<NomineeInfo> nominees,
        GuardianInfo guardianInfo,
        GuarantorInfo guarantorInfo,
        FamilyInfo familyInfo,
        List<Relationship> relationships) {
}

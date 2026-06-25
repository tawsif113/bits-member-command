package com.bits.member.domain.valueobject;

import com.bits.ddd.shared.domain.value.DomainStatus;
import java.time.LocalDate;

public record MembershipStatusChangeHistory(
        String groupInfoId,
        String projectInfoId,
        String officeInfoId,
        LocalDate statusChangeDate,
        String memberStatusId,
        String oldMemberStatusId,
        DomainStatus domainStatus,
        String createdBy,
        String updatedBy) {
}

package com.bits.member.application.dto.sourcedata;

public record MemberClassification(
        String id,
        String categoryName,
        Integer ageFrom,
        Integer ageTo,
        Boolean allowedLoan,
        Boolean hasSavings,
        Boolean disallowMemberFees,
        Integer domainStatusId,
        Boolean active) {
}

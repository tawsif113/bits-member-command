package com.bits.member.domain.entity;

import java.util.List;

public record FamilyInfo(
        String houseHoldHeadName,
        Integer sonNo,
        Integer daughterNo,
        Integer maleNo,
        Integer femaleNo,
        Integer earningMemberNo,
        Integer otherOrgMemberNo,
        Boolean taxPayer,
        Boolean familyLoan,
        List<OtherOrganizationLoan> otherOrgLoans) {
}

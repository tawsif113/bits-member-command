package com.bits.member.domain.entity;

import java.math.BigDecimal;

public record OtherOrganizationLoan(
        String id,
        BigDecimal loanAmount,
        String organizationName) {
}

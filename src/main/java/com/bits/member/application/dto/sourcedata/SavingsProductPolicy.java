package com.bits.member.application.dto.sourcedata;

import java.math.BigDecimal;

public record SavingsProductPolicy(
        String id,
        String savingsProductId,
        BigDecimal minDepositAmount,
        BigDecimal minimumBalance,
        String calculationFrequency,
        Boolean active) {
}

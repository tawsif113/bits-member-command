package com.bits.member.application.dto.sourcedata;

import java.math.BigDecimal;

public record SavingsAccount(
        String id,
        String memberId,
        String savingsProductId,
        BigDecimal targetAmount,
        Boolean hasTransactions,
        String branchInfoId,
        Boolean active) {
}

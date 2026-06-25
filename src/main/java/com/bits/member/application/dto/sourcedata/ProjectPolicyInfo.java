package com.bits.member.application.dto.sourcedata;

import java.math.BigDecimal;

public record ProjectPolicyInfo(
        String id,
        String projectInfoId,
        String associationType,
        Boolean hasMembershipFee,
        BigDecimal feeAmount,
        Boolean hasPassbook,
        BigDecimal passbookPrice,
        String collectionFrequency,
        Boolean active) {
}

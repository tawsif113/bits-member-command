package com.bits.member.application.dto.sourcedata;

public record SavingsProduct(
        String id,
        String productCode,
        String productName,
        String productType,
        String collectionFrequency,
        Integer domainStatusId,
        Boolean active) {
}

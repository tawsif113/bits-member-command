package com.bits.member.domain.entity;

import java.math.BigDecimal;

public record HouseholdAsset(
        String id,
        String assetName,
        Integer assetQuantity,
        BigDecimal assetValue,
        Boolean deleted) {
}

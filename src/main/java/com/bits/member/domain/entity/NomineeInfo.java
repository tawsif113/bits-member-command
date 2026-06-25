package com.bits.member.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NomineeInfo(
        String id,
        String name,
        String relationshipId,
        BigDecimal sharePercent,
        LocalDate dateOfBirth,
        Integer age,
        String nationalId,
        String smartCardId,
        String passportNo,
        String photoIdNo,
        String contactNo) {
}

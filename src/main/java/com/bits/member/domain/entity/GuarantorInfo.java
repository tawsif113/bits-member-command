package com.bits.member.domain.entity;

import java.time.LocalDate;

public record GuarantorInfo(
        String guarantorName,
        String nationalId,
        LocalDate dateOfBirth,
        Integer age,
        String relationshipId,
        Boolean active,
        Integer domainStatusId) {
}

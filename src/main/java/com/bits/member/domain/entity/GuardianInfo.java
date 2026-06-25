package com.bits.member.domain.entity;

import java.time.LocalDate;

public record GuardianInfo(
        String guardianName,
        String nationalId,
        LocalDate dateOfBirth,
        Integer age,
        String address,
        String relationshipId,
        String nomineeId) {
}

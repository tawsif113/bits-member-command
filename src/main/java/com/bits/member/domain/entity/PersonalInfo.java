package com.bits.member.domain.entity;

import java.time.LocalDate;

public record PersonalInfo(
        String genderId,
        String maritalStatusId,
        LocalDate dateOfBirth,
        String occupationId,
        String fatherName,
        String motherName,
        String spouseName,
        LocalDate spouseDateOfBirth,
        String nationalId,
        String smartCardId,
        String passportNo,
        String drivingLicenseNo,
        String photoIdNo,
        Integer otherIdTypeId,
        String otherIdTypeNo,
        LocalDate expiryDate) {
}

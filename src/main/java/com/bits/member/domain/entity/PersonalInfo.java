package com.bits.member.domain.entity;

import java.time.LocalDate;

public record PersonalInfo(
        String salutationId,
        String nationalId,
        String smartCardId,
        String passportNo,
        String drivingLicenseNo,
        String photoIdNo,
        String genderId,
        String maritalStatusId,
        Integer age,
        Integer biometricStatus,
        String occupationId,
        LocalDate dateOfBirth,
        String fatherName,
        String motherName,
        String spouseName,
        LocalDate spouseDateOfBirth, // mapped to spDateOfBirth in document
        String spNationalId,
        String spSmartCardId,
        String spPassportNo,
        String spPhotoIdNo,
        String bikashWalletNo,
        String rocketWalletNo,
        String referralInfoId,
        String photoReference,
        Integer otherIdTypeId,
        String otherIdTypeNo,
        LocalDate expiryDate,
        String placeOfIssuingCountry,
        Boolean isPersonWithDisability) {
}

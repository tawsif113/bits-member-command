package com.bits.member.domain.valueobject;

public record MembershipDocument(
        String membershipFormReference,
        String nationalIdReference,
        String passportReference,
        String drivingLicenseReference,
        String photoIdReference,
        String surveyFormReference,
        String otherFormReference) {
}

package com.bits.member.domain.entity;

public record MemberAddress(
        String addressTitleId,
        String address,
        String countryId,
        String cityId,
        String thanaId,
        String zipCode) {
}

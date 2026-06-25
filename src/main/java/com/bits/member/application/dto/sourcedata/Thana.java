package com.bits.member.application.dto.sourcedata;

public record Thana(
        String id,
        String thanaCode,
        String thanaName,
        String districtId,
        String countryId,
        Boolean active) {
}

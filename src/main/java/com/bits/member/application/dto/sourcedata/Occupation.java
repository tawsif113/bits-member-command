package com.bits.member.application.dto.sourcedata;

public record Occupation(
        String id,
        String occupationName,
        String occupationCode,
        Boolean active) {
}

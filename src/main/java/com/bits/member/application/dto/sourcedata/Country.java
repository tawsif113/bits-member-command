package com.bits.member.application.dto.sourcedata;

public record Country(
        String id,
        String name,
        String code,
        String timeZone,
        Boolean active) {
}

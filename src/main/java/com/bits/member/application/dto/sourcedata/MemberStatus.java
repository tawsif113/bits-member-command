package com.bits.member.application.dto.sourcedata;

public record MemberStatus(
        String id,
        String statusCode,
        String statusName,
        Boolean active) {
}

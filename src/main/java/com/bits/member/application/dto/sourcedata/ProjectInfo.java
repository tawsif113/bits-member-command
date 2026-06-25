package com.bits.member.application.dto.sourcedata;

public record ProjectInfo(
        String id,
        String projectCode,
        String projectName,
        String projectStatus,
        String associationType,
        Boolean active) {
}

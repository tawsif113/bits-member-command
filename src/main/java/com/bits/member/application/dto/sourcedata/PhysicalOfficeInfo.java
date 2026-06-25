package com.bits.member.application.dto.sourcedata;

import java.time.LocalDate;

public record PhysicalOfficeInfo(
        String id,
        String officeCode,
        String officeName,
        String officeType,
        String businessDayStatus,
        LocalDate businessDate,
        Boolean active) {
}

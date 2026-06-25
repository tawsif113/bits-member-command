package com.bits.member.application.dto.sourcedata;

import java.time.LocalDate;

public record GroupInfo(
        String id,
        String groupCode,
        String groupName,
        String applicableGender,
        String groupStatus,
        String branchInfoId,
        String projectInfoId,
        String assignedPoId,
        LocalDate lastPoAssignedDate,
        Boolean active) {
}

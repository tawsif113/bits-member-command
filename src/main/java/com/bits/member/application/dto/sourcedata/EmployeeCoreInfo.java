package com.bits.member.application.dto.sourcedata;

public record EmployeeCoreInfo(
        String id,
        String employeeCode,
        String employeeName,
        String branchInfoId,
        String projectInfoId,
        Boolean active) {
}

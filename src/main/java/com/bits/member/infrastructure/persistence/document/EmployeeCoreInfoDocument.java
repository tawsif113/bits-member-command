package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee_core_info_snapshots")
public record EmployeeCoreInfoDocument(
        @Id String id,
        String employeeCode,
        String employeeName,
        String branchInfoId,
        String projectInfoId,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

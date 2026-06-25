package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group_info_snapshots")
public record GroupInfoDocument(
        @Id String id,
        String groupCode,
        String groupName,
        String applicableGender,
        String groupStatus,
        String branchInfoId,
        String projectInfoId,
        String assignedPoId,
        LocalDate lastPoAssignedDate,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

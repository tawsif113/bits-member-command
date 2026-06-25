package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "project_info_snapshots")
public record ProjectInfoDocument(
        @Id String id,
        String projectCode,
        String projectName,
        String projectStatus,
        String associationType,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

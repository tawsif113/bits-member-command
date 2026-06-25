package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "member_status_snapshots")
public record MemberStatusDocument(
        @Id String id,
        String statusCode,
        String statusName,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

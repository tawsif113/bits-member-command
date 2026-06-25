package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "occupation_snapshots")
public record OccupationDocument(
        @Id String id,
        String occupationName,
        String occupationCode,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

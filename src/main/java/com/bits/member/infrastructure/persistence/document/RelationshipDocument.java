package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "relationship_snapshots")
public record RelationshipDocument(
        @Id String id,
        String name,
        Boolean relative,
        Boolean spouseRelationship,
        Integer statusId,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

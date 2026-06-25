package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "country_snapshots")
public record CountryDocument(
        @Id String id,
        String name,
        String code,
        String timeZone,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

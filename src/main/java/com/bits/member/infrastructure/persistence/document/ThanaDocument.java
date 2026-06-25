package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "thana_snapshots")
public record ThanaDocument(
        @Id String id,
        String thanaCode,
        String thanaName,
        String districtId,
        String countryId,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

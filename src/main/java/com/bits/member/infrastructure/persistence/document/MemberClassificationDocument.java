package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "member_classification_snapshots")
public record MemberClassificationDocument(
        @Id String id,
        String categoryName,
        Integer ageFrom,
        Integer ageTo,
        Boolean allowedLoan,
        Boolean hasSavings,
        Boolean disallowMemberFees,
        Integer domainStatusId,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

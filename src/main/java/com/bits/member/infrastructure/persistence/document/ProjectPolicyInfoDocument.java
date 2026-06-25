package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "project_policy_info_snapshots")
public record ProjectPolicyInfoDocument(
        @Id String id,
        String projectInfoId,
        String associationType,
        Boolean hasMembershipFee,
        BigDecimal feeAmount,
        Boolean hasPassbook,
        BigDecimal passbookPrice,
        String collectionFrequency,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

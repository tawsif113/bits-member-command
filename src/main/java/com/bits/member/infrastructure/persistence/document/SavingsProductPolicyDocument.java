package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "savings_product_policy_snapshots")
public record SavingsProductPolicyDocument(
        @Id String id,
        String savingsProductId,
        BigDecimal minDepositAmount,
        BigDecimal minimumBalance,
        String calculationFrequency,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

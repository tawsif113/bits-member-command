package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "savings_account_snapshots")
public record SavingsAccountDocument(
        @Id String id,
        String memberId,
        String savingsProductId,
        BigDecimal targetAmount,
        Boolean hasTransactions,
        String branchInfoId,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

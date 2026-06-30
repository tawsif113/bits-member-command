package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.dto.SourceData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "savings_product_policy_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class SavingsProductPolicyDocument extends SourceData<String> {

    @Id
    private String id;

    private Integer noOfAllowedWithdrawal;
    private BigDecimal minimumBalance;
    private BigDecimal minDepositAmount;
    private String calculationFrequency;
    private String provisionFrequency;
    private String calculationMethod;
    private String creditFrequency;
    private Long policyRepaymentsPlanId;
    private Long savingsProductId;
    private Long domainStatusId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private Boolean active;

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

package com.bits.member.application.dto.sourcedata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsProductPolicy {
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
}

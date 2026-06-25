package com.bits.member.application.dto.sourcedata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public String id() {
        return id;
    }

    public Integer noOfAllowedWithdrawal() {
        return noOfAllowedWithdrawal;
    }
    public BigDecimal minimumBalance() {
        return minimumBalance;
    }
    public BigDecimal minDepositAmount() {
        return minDepositAmount;
    }
    public String calculationFrequency() {
        return calculationFrequency;
    }
    public String provisionFrequency() {
        return provisionFrequency;
    }
    public String calculationMethod() {
        return calculationMethod;
    }
    public String creditFrequency() {
        return creditFrequency;
    }
    public Long policyRepaymentsPlanId() {
        return policyRepaymentsPlanId;
    }
    public Long savingsProductId() {
        return savingsProductId;
    }
    public Long domainStatusId() {
        return domainStatusId;
    }
    public Long createdBy() {
        return createdBy;
    }
    public Long updatedBy() {
        return updatedBy;
    }
    public LocalDateTime dateCreated() {
        return dateCreated;
    }
    public LocalDateTime lastUpdated() {
        return lastUpdated;
    }
    public Boolean active() {
        return active;
    }

}

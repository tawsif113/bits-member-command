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
public class ProjectPolicyInfo {
    private String id;
    private String projectInfoId;
    private String associationType;
    private Boolean hasLoans;
    private Boolean hasSavings;
    private Boolean hasLoanSecurity;
    private Boolean hasLoanExposureLimit;
    private Boolean hasPassbook;
    private BigDecimal passbookPrice;
    private Boolean isTimeLimitRequired;
    private Integer newLoanTimeLimit;
    private Integer repeatLoanTimeLimit;
    private Boolean hasDeathBenefit;
    private BigDecimal deathBenefitAmount;
    private BigDecimal maxWriteOffAmount;
    private BigDecimal loanLossProvisionPercentage;
    private Boolean hasPartialAdjustment;
    private Boolean hasMatchFund;
    private Boolean isTUP;
    private Boolean isWithDrawAllowed;
    private BigDecimal withdrawPercentage;
    private Integer noOfAllowedWithdrawal;
    private Boolean hasMembershipFee;
    private Boolean hasFireInsurance;
    private BigDecimal feeAmount;
    private Integer newBalancePercentage;
    private Integer newTransactionDuration;
    private Integer newTransactionPercentage;
    private Integer newBalanceTransactionChecking;
    private Integer repeatBalancePercentage;
    private Integer repeatTransactionDuration;
    private Integer repeatTransactionPercentage;
    private Long savingsMatchingPolicyId;
    private Boolean allowForCurrentSavings;
    private BigDecimal targetAmountNonactiveLoan;
    private BigDecimal savingsPercentage;
    private String collectionFrequency;
    private Boolean active;

    public String id() {
        return id;
    }

    public String projectInfoId() {
        return projectInfoId;
    }
    public String associationType() {
        return associationType;
    }
    public Boolean hasLoans() {
        return hasLoans;
    }
    public Boolean hasSavings() {
        return hasSavings;
    }
    public Boolean hasLoanSecurity() {
        return hasLoanSecurity;
    }
    public Boolean hasLoanExposureLimit() {
        return hasLoanExposureLimit;
    }
    public Boolean hasPassbook() {
        return hasPassbook;
    }
    public BigDecimal passbookPrice() {
        return passbookPrice;
    }
    public Boolean isTimeLimitRequired() {
        return isTimeLimitRequired;
    }
    public Integer newLoanTimeLimit() {
        return newLoanTimeLimit;
    }
    public Integer repeatLoanTimeLimit() {
        return repeatLoanTimeLimit;
    }
    public Boolean hasDeathBenefit() {
        return hasDeathBenefit;
    }
    public BigDecimal deathBenefitAmount() {
        return deathBenefitAmount;
    }
    public BigDecimal maxWriteOffAmount() {
        return maxWriteOffAmount;
    }
    public BigDecimal loanLossProvisionPercentage() {
        return loanLossProvisionPercentage;
    }
    public Boolean hasPartialAdjustment() {
        return hasPartialAdjustment;
    }
    public Boolean hasMatchFund() {
        return hasMatchFund;
    }
    public Boolean isTUP() {
        return isTUP;
    }
    public Boolean isWithDrawAllowed() {
        return isWithDrawAllowed;
    }
    public BigDecimal withdrawPercentage() {
        return withdrawPercentage;
    }
    public Integer noOfAllowedWithdrawal() {
        return noOfAllowedWithdrawal;
    }
    public Boolean hasMembershipFee() {
        return hasMembershipFee;
    }
    public Boolean hasFireInsurance() {
        return hasFireInsurance;
    }
    public BigDecimal feeAmount() {
        return feeAmount;
    }
    public Integer newBalancePercentage() {
        return newBalancePercentage;
    }
    public Integer newTransactionDuration() {
        return newTransactionDuration;
    }
    public Integer newTransactionPercentage() {
        return newTransactionPercentage;
    }
    public Integer newBalanceTransactionChecking() {
        return newBalanceTransactionChecking;
    }
    public Integer repeatBalancePercentage() {
        return repeatBalancePercentage;
    }
    public Integer repeatTransactionDuration() {
        return repeatTransactionDuration;
    }
    public Integer repeatTransactionPercentage() {
        return repeatTransactionPercentage;
    }
    public Long savingsMatchingPolicyId() {
        return savingsMatchingPolicyId;
    }
    public Boolean allowForCurrentSavings() {
        return allowForCurrentSavings;
    }
    public BigDecimal targetAmountNonactiveLoan() {
        return targetAmountNonactiveLoan;
    }
    public BigDecimal savingsPercentage() {
        return savingsPercentage;
    }
    public String collectionFrequency() {
        return collectionFrequency;
    }
    public Boolean active() {
        return active;
    }

}

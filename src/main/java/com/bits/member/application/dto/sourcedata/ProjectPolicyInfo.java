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
}

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
public class SavingsAccount {
    private String id;
    private String countryId;
    private String accountNo;
    private String accountName;
    private String accountTypeId;
    private LocalDate creationDate;
    private LocalDate closingDate;
    private BigDecimal initialDeposit;
    private BigDecimal installmentAmount;
    private Long officeInfoId;
    private Long groupInfoId;
    private String projectInfoId;
    private String savingsProductId;
    private BigDecimal interestRate;
    private String calculationFrequency;
    private String provisionFrequency;
    private String calculationMethod;
    private String creditFrequency;
    private String accountStatusId;
    private String portfolioStatusId;
    private Long domainStatusId;
    private BigDecimal savingsBalance;
    private BigDecimal provisionedInterest;
    private BigDecimal calculatedInterest;
    private Boolean isTransferredSavings;
    private Boolean hasMatchedFund;
    private BigDecimal matchFundMaxAmount;
    private BigDecimal matchRatio;
    private LocalDate matchFundExpiredDate;
    private Long cohortMappingId;
    private LocalDate nextCollectionDate;
    private LocalDate lastTransferredDate;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private Integer currentSavingsAccountType;
    private String currentAccountMobile;
    private Integer otpSendType;
    private LocalDate lastInterestCalculationDate;
    private Boolean isInterMemberTransferred;
    private Integer uidCycleNo;
    private Integer uidStatus;
    private Boolean active;
}

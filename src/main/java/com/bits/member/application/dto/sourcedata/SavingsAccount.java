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

    public String id() {
        return id;
    }

    public String countryId() {
        return countryId;
    }
    public String accountNo() {
        return accountNo;
    }
    public String accountName() {
        return accountName;
    }
    public String accountTypeId() {
        return accountTypeId;
    }
    public LocalDate creationDate() {
        return creationDate;
    }
    public LocalDate closingDate() {
        return closingDate;
    }
    public BigDecimal initialDeposit() {
        return initialDeposit;
    }
    public BigDecimal installmentAmount() {
        return installmentAmount;
    }
    public Long officeInfoId() {
        return officeInfoId;
    }
    public Long groupInfoId() {
        return groupInfoId;
    }
    public String projectInfoId() {
        return projectInfoId;
    }
    public String savingsProductId() {
        return savingsProductId;
    }
    public BigDecimal interestRate() {
        return interestRate;
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
    public String accountStatusId() {
        return accountStatusId;
    }
    public String portfolioStatusId() {
        return portfolioStatusId;
    }
    public Long domainStatusId() {
        return domainStatusId;
    }
    public BigDecimal savingsBalance() {
        return savingsBalance;
    }
    public BigDecimal provisionedInterest() {
        return provisionedInterest;
    }
    public BigDecimal calculatedInterest() {
        return calculatedInterest;
    }
    public Boolean isTransferredSavings() {
        return isTransferredSavings;
    }
    public Boolean hasMatchedFund() {
        return hasMatchedFund;
    }
    public BigDecimal matchFundMaxAmount() {
        return matchFundMaxAmount;
    }
    public BigDecimal matchRatio() {
        return matchRatio;
    }
    public LocalDate matchFundExpiredDate() {
        return matchFundExpiredDate;
    }
    public Long cohortMappingId() {
        return cohortMappingId;
    }
    public LocalDate nextCollectionDate() {
        return nextCollectionDate;
    }
    public LocalDate lastTransferredDate() {
        return lastTransferredDate;
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
    public Integer currentSavingsAccountType() {
        return currentSavingsAccountType;
    }
    public String currentAccountMobile() {
        return currentAccountMobile;
    }
    public Integer otpSendType() {
        return otpSendType;
    }
    public LocalDate lastInterestCalculationDate() {
        return lastInterestCalculationDate;
    }
    public Boolean isInterMemberTransferred() {
        return isInterMemberTransferred;
    }
    public Integer uidCycleNo() {
        return uidCycleNo;
    }
    public Integer uidStatus() {
        return uidStatus;
    }
    public Boolean active() {
        return active;
    }

}

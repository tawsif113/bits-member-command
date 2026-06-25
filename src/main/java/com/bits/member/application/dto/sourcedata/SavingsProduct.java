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
public class SavingsProduct {
    private String id;
    private String countryId;
    private String productCode;
    private String productRefCode;
    private String productName;
    private LocalDate setupDate;
    private String productTypeId;
    private BigDecimal interestRate;
    private String rateTypeId;
    private String description;
    private Long savingsProductSubTypeId;
    private Integer clientPercentageLower;
    private Integer clientPercentageUpper;
    private Integer profitPaymentDay;
    private Boolean hasMatchFund;
    private Long domainStatusId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private Long version;
    private String productType;
    private String collectionFrequency;
    private Boolean active;

    public String id() {
        return id;
    }

    public String countryId() {
        return countryId;
    }
    public String productCode() {
        return productCode;
    }
    public String productRefCode() {
        return productRefCode;
    }
    public String productName() {
        return productName;
    }
    public LocalDate setupDate() {
        return setupDate;
    }
    public String productTypeId() {
        return productTypeId;
    }
    public BigDecimal interestRate() {
        return interestRate;
    }
    public String rateTypeId() {
        return rateTypeId;
    }
    public String description() {
        return description;
    }
    public Long savingsProductSubTypeId() {
        return savingsProductSubTypeId;
    }
    public Integer clientPercentageLower() {
        return clientPercentageLower;
    }
    public Integer clientPercentageUpper() {
        return clientPercentageUpper;
    }
    public Integer profitPaymentDay() {
        return profitPaymentDay;
    }
    public Boolean hasMatchFund() {
        return hasMatchFund;
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
    public Long version() {
        return version;
    }
    public String productType() {
        return productType;
    }
    public String collectionFrequency() {
        return collectionFrequency;
    }
    public Boolean active() {
        return active;
    }

}

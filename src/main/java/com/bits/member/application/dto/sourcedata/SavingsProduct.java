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
}

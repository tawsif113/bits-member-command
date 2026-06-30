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

@Document(collection = "savings_product_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class SavingsProductDocument extends SourceData<String> {

    @Id
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

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

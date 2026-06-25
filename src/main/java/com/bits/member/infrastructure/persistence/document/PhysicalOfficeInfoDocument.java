package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "physical_office_info_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class PhysicalOfficeInfoDocument extends SourceData<String> {

    @Id
    private String id;

    private String officeCode;
    private String officeRefCode;
    private String officeName;
    private String officeTypeId;
    private String areaTypeId;
    private LocalDate setupDate;
    private LocalDate effectiveDate;
    private String registeredAddressId;
    private String businessAddressId;
    private Long officeStatusId;
    private String officeCountryId;
    private String parentOfficeId;
    private String mobileNo;
    private Boolean isHrOffice;
    private Boolean isMfOffice;
    private Boolean isDevOffice;
    private Boolean isUpazilaAccountsOffice;
    private Boolean isIndp;
    private Boolean verifyTB;
    private Boolean verifyPortfolio;
    private Boolean verifySavings;
    private String officeHierarchyId;
    private String reportingToId;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private Long createdBy;
    private Long updatedBy;
    private String mfBranchId;
    private String mfRefCode;
    private Boolean hasOperation;
    private String bkashWalletNo;
    private String operationCategory;
    private Long parentProjectId;
    private String officeType;
    private String businessDayStatus;
    private LocalDate businessDate;
    private Boolean active;

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

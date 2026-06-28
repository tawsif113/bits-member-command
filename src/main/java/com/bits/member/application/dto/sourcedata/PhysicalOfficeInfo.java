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
public class PhysicalOfficeInfo {
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
}

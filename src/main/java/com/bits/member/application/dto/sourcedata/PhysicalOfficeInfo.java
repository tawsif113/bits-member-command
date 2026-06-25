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

    public String id() {
        return id;
    }

    public String officeCode() {
        return officeCode;
    }
    public String officeRefCode() {
        return officeRefCode;
    }
    public String officeName() {
        return officeName;
    }
    public String officeTypeId() {
        return officeTypeId;
    }
    public String areaTypeId() {
        return areaTypeId;
    }
    public LocalDate setupDate() {
        return setupDate;
    }
    public LocalDate effectiveDate() {
        return effectiveDate;
    }
    public String registeredAddressId() {
        return registeredAddressId;
    }
    public String businessAddressId() {
        return businessAddressId;
    }
    public Long officeStatusId() {
        return officeStatusId;
    }
    public String officeCountryId() {
        return officeCountryId;
    }
    public String parentOfficeId() {
        return parentOfficeId;
    }
    public String mobileNo() {
        return mobileNo;
    }
    public Boolean isHrOffice() {
        return isHrOffice;
    }
    public Boolean isMfOffice() {
        return isMfOffice;
    }
    public Boolean isDevOffice() {
        return isDevOffice;
    }
    public Boolean isUpazilaAccountsOffice() {
        return isUpazilaAccountsOffice;
    }
    public Boolean isIndp() {
        return isIndp;
    }
    public Boolean verifyTB() {
        return verifyTB;
    }
    public Boolean verifyPortfolio() {
        return verifyPortfolio;
    }
    public Boolean verifySavings() {
        return verifySavings;
    }
    public String officeHierarchyId() {
        return officeHierarchyId;
    }
    public String reportingToId() {
        return reportingToId;
    }
    public LocalDateTime dateCreated() {
        return dateCreated;
    }
    public LocalDateTime lastUpdated() {
        return lastUpdated;
    }
    public Long createdBy() {
        return createdBy;
    }
    public Long updatedBy() {
        return updatedBy;
    }
    public String mfBranchId() {
        return mfBranchId;
    }
    public String mfRefCode() {
        return mfRefCode;
    }
    public Boolean hasOperation() {
        return hasOperation;
    }
    public String bkashWalletNo() {
        return bkashWalletNo;
    }
    public String operationCategory() {
        return operationCategory;
    }
    public Long parentProjectId() {
        return parentProjectId;
    }
    public String officeType() {
        return officeType;
    }
    public String businessDayStatus() {
        return businessDayStatus;
    }
    public LocalDate businessDate() {
        return businessDate;
    }
    public Boolean active() {
        return active;
    }

}

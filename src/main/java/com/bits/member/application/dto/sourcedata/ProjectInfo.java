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
public class ProjectInfo {
    private String id;
    private String proposalId;
    private String projectCountryId;
    private String projectCode;
    private String projectRefCode;
    private String projectName;
    private String projectDescription;
    private String projectShortCode;
    private LocalDate projectSetupDate;
    private LocalDate projectEffectiveDate;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;
    private Long domainStatusId;
    private String programInfoId;
    private Boolean bookClosing;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private Long parentProjectInfoId;
    private Boolean isIndependent;
    private Boolean isOverhead;
    private String hoType;
    private String boType;
    private String mfProjectRefCode;
    private Boolean isNgoBeuro;
    private LocalDate beuroFromDate;
    private LocalDate beuroToDate;
    private Boolean isTrendxProject;
    private Boolean isSmartCollection;
    private Boolean hasMfOperation;
    private String projectStatusId;
    private Integer startMonth;
    private Integer endMonth;
    private Integer mfEndMonth;
    private Boolean hasFinOperation;
    private Long sourceOfFundId;
    private String foreignCurrency;
    private String localCurrency;
    private Long foreignAmount;
    private Long localAmount;
    private LocalDate signingDate;
    private String projectStatus;
    private String associationType;
    private Boolean active;

    public String id() {
        return id;
    }

    public String proposalId() {
        return proposalId;
    }
    public String projectCountryId() {
        return projectCountryId;
    }
    public String projectCode() {
        return projectCode;
    }
    public String projectRefCode() {
        return projectRefCode;
    }
    public String projectName() {
        return projectName;
    }
    public String projectDescription() {
        return projectDescription;
    }
    public String projectShortCode() {
        return projectShortCode;
    }
    public LocalDate projectSetupDate() {
        return projectSetupDate;
    }
    public LocalDate projectEffectiveDate() {
        return projectEffectiveDate;
    }
    public LocalDate projectStartDate() {
        return projectStartDate;
    }
    public LocalDate projectEndDate() {
        return projectEndDate;
    }
    public Long domainStatusId() {
        return domainStatusId;
    }
    public String programInfoId() {
        return programInfoId;
    }
    public Boolean bookClosing() {
        return bookClosing;
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
    public Long parentProjectInfoId() {
        return parentProjectInfoId;
    }
    public Boolean isIndependent() {
        return isIndependent;
    }
    public Boolean isOverhead() {
        return isOverhead;
    }
    public String hoType() {
        return hoType;
    }
    public String boType() {
        return boType;
    }
    public String mfProjectRefCode() {
        return mfProjectRefCode;
    }
    public Boolean isNgoBeuro() {
        return isNgoBeuro;
    }
    public LocalDate beuroFromDate() {
        return beuroFromDate;
    }
    public LocalDate beuroToDate() {
        return beuroToDate;
    }
    public Boolean isTrendxProject() {
        return isTrendxProject;
    }
    public Boolean isSmartCollection() {
        return isSmartCollection;
    }
    public Boolean hasMfOperation() {
        return hasMfOperation;
    }
    public String projectStatusId() {
        return projectStatusId;
    }
    public Integer startMonth() {
        return startMonth;
    }
    public Integer endMonth() {
        return endMonth;
    }
    public Integer mfEndMonth() {
        return mfEndMonth;
    }
    public Boolean hasFinOperation() {
        return hasFinOperation;
    }
    public Long sourceOfFundId() {
        return sourceOfFundId;
    }
    public String foreignCurrency() {
        return foreignCurrency;
    }
    public String localCurrency() {
        return localCurrency;
    }
    public Long foreignAmount() {
        return foreignAmount;
    }
    public Long localAmount() {
        return localAmount;
    }
    public LocalDate signingDate() {
        return signingDate;
    }
    public String projectStatus() {
        return projectStatus;
    }
    public String associationType() {
        return associationType;
    }
    public Boolean active() {
        return active;
    }

}

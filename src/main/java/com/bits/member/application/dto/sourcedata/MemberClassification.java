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
public class MemberClassification {
    private String id;
    private String categoryName;
    private Integer ageFrom;
    private Integer ageTo;
    private Boolean isAllowedLoan;
    private Boolean hasSavings;
    private Boolean hasRMG;
    private Boolean hasERMG;
    private Boolean isDisallowMemberFees;
    private Long domainStatusId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private Boolean allowedLoan;
    private Boolean disallowMemberFees;
    private Boolean active;

    public String id() {
        return id;
    }

    public String categoryName() {
        return categoryName;
    }
    public Integer ageFrom() {
        return ageFrom;
    }
    public Integer ageTo() {
        return ageTo;
    }
    public Boolean isAllowedLoan() {
        return isAllowedLoan;
    }
    public Boolean hasSavings() {
        return hasSavings;
    }
    public Boolean hasRMG() {
        return hasRMG;
    }
    public Boolean hasERMG() {
        return hasERMG;
    }
    public Boolean isDisallowMemberFees() {
        return isDisallowMemberFees;
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
    public Boolean allowedLoan() {
        return allowedLoan;
    }
    public Boolean disallowMemberFees() {
        return disallowMemberFees;
    }
    public Boolean active() {
        return active;
    }

}

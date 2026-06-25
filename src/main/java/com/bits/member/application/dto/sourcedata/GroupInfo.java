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
public class GroupInfo {
    private String id;
    private String groupCode;
    private String groupName;
    private String assignedPoId;
    private LocalDate orientationDate;
    private LocalDate groupCreationDate;
    private LocalDate lastPoAssignedDate;
    private String groupReferenceNumber;
    private String spotAddress;
    private String groupStatusId;
    private Long domainStatusId;
    private String closeReasonId;
    private LocalDate closingDate;
    private String meetingDayId;
    private String meetingTime;
    private String demarcationArea;
    private Long weekNumber;
    private String loanCollectionFrequencyId;
    private LocalDate loanCollectionStartDate;
    private String savingsCollectionFrequencyId;
    private LocalDate savingsCollectionStartDate;
    private LocalDate nextCollectionDate;
    private String branchInfoId;
    private String projectInfoId;
    private String groupScannedForm;
    private Boolean isTransferredGroup;
    private Long groupCategoryId;
    private Long voCategoryId;
    private Long serviceTerritoryId;
    private String longitude;
    private String latitude;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private String applicableGender;
    private String groupStatus;
    private Boolean active;

    public String id() {
        return id;
    }

    public String groupCode() {
        return groupCode;
    }
    public String groupName() {
        return groupName;
    }
    public String assignedPoId() {
        return assignedPoId;
    }
    public LocalDate orientationDate() {
        return orientationDate;
    }
    public LocalDate groupCreationDate() {
        return groupCreationDate;
    }
    public LocalDate lastPoAssignedDate() {
        return lastPoAssignedDate;
    }
    public String groupReferenceNumber() {
        return groupReferenceNumber;
    }
    public String spotAddress() {
        return spotAddress;
    }
    public String groupStatusId() {
        return groupStatusId;
    }
    public Long domainStatusId() {
        return domainStatusId;
    }
    public String closeReasonId() {
        return closeReasonId;
    }
    public LocalDate closingDate() {
        return closingDate;
    }
    public String meetingDayId() {
        return meetingDayId;
    }
    public String meetingTime() {
        return meetingTime;
    }
    public String demarcationArea() {
        return demarcationArea;
    }
    public Long weekNumber() {
        return weekNumber;
    }
    public String loanCollectionFrequencyId() {
        return loanCollectionFrequencyId;
    }
    public LocalDate loanCollectionStartDate() {
        return loanCollectionStartDate;
    }
    public String savingsCollectionFrequencyId() {
        return savingsCollectionFrequencyId;
    }
    public LocalDate savingsCollectionStartDate() {
        return savingsCollectionStartDate;
    }
    public LocalDate nextCollectionDate() {
        return nextCollectionDate;
    }
    public String branchInfoId() {
        return branchInfoId;
    }
    public String projectInfoId() {
        return projectInfoId;
    }
    public String groupScannedForm() {
        return groupScannedForm;
    }
    public Boolean isTransferredGroup() {
        return isTransferredGroup;
    }
    public Long groupCategoryId() {
        return groupCategoryId;
    }
    public Long voCategoryId() {
        return voCategoryId;
    }
    public Long serviceTerritoryId() {
        return serviceTerritoryId;
    }
    public String longitude() {
        return longitude;
    }
    public String latitude() {
        return latitude;
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
    public String applicableGender() {
        return applicableGender;
    }
    public String groupStatus() {
        return groupStatus;
    }
    public Boolean active() {
        return active;
    }

}

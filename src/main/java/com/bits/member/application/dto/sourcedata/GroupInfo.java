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
}

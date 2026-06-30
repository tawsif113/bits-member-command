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

@Document(collection = "group_info_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class GroupInfoDocument extends SourceData<String> {

    @Id
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

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

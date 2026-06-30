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

@Document(collection = "project_info_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class ProjectInfoDocument extends SourceData<String> {

    @Id
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

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

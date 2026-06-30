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

@Document(collection = "member_classification_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class MemberClassificationDocument extends SourceData<String> {

    @Id
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

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

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
}

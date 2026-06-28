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
public class Relationship {
    private String id;
    private String name;
    private String description;
    private Boolean isRelative;
    private String statusId;
    private Boolean relative;
    private Boolean spouseRelationship;
    private Boolean active;
}

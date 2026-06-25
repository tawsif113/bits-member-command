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
public class Relationship {
    private String id;
    private String name;
    private String description;
    private Boolean isRelative;
    private String statusId;
    private Boolean relative;
    private Boolean spouseRelationship;
    private Boolean active;

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }
    public String description() {
        return description;
    }
    public Boolean isRelative() {
        return isRelative;
    }
    public String statusId() {
        return statusId;
    }
    public Boolean relative() {
        return relative;
    }
    public Boolean spouseRelationship() {
        return spouseRelationship;
    }
    public Boolean active() {
        return active;
    }

}

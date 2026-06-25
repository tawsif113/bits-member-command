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
public class MemberStatus {
    private String id;
    private String name;
    private String description;
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
    public Boolean active() {
        return active;
    }

}

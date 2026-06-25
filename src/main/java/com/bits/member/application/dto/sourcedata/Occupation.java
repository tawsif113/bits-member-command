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
public class Occupation {
    private String id;
    private String name;
    private String description;
    private String occupationCode;
    private String occupationName;
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
    public String occupationCode() {
        return occupationCode;
    }
    public String occupationName() {
        return occupationName;
    }
    public Boolean active() {
        return active;
    }

}

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
public class Thana {
    private String id;
    private String districtId;
    private String thanaName;
    private String thanaCode;
    private String hrThanaId;
    private String countryId;
    private Boolean active;

    public String id() {
        return id;
    }

    public String districtId() {
        return districtId;
    }
    public String thanaName() {
        return thanaName;
    }
    public String thanaCode() {
        return thanaCode;
    }
    public String hrThanaId() {
        return hrThanaId;
    }
    public String countryId() {
        return countryId;
    }
    public Boolean active() {
        return active;
    }

}

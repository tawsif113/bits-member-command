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
public class Thana {
    private String id;
    private String districtId;
    private String thanaName;
    private String thanaCode;
    private String hrThanaId;
    private String countryId;
    private Boolean active;
}

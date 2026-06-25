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
public class Country {
    private String id;
    private String name;
    private String code;
    private String shortName;
    private String shortCode;
    private String callingCode;
    private Boolean hasOperation;
    private String localCurrencyName;
    private String foreignCurrencyName;
    private BigDecimal minimumDenomination;
    private String timeZone;
    private Boolean active;

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }
    public String code() {
        return code;
    }
    public String shortName() {
        return shortName;
    }
    public String shortCode() {
        return shortCode;
    }
    public String callingCode() {
        return callingCode;
    }
    public Boolean hasOperation() {
        return hasOperation;
    }
    public String localCurrencyName() {
        return localCurrencyName;
    }
    public String foreignCurrencyName() {
        return foreignCurrencyName;
    }
    public BigDecimal minimumDenomination() {
        return minimumDenomination;
    }
    public String timeZone() {
        return timeZone;
    }
    public Boolean active() {
        return active;
    }

}

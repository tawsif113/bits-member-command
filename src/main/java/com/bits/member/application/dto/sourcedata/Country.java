package com.bits.member.application.dto.sourcedata;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
}

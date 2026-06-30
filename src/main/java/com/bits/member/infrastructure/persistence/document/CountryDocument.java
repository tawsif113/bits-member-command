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

@Document(collection = "country_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class CountryDocument extends SourceData<String> {

    @Id
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

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

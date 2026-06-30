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

@Document(collection = "occupation_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class OccupationDocument extends SourceData<String> {

    @Id
    private String id;

    private String name;
    private String description;
    private String occupationCode;
    private String occupationName;
    private Boolean active;

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

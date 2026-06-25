package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.shared.persistence.document.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "physical_office_info_snapshots")
public record PhysicalOfficeInfoDocument(
        @Id String id,
        String officeCode,
        String officeName,
        String officeType,
        String businessDayStatus,
        LocalDate businessDate,
        Boolean active,
        LocalDateTime lastEventTimestamp) implements BaseEntity<String> {
}

package com.bits.member.domain.specification.context;

import com.bits.ddd.specification.context.ValidationContext;
import com.bits.member.application.dto.DeduplicationResult;
import com.bits.member.application.dto.MemberSourceData;
import com.bits.member.domain.aggregate.Member;
import java.time.LocalDate;

public record MemberValidationContext(
        MemberSourceData sourceData,
        LocalDate businessDate,
        DeduplicationResult deduplicationResult,
        Member aggregate) implements ValidationContext {
}

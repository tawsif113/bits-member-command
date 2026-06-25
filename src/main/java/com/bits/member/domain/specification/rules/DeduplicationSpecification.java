package com.bits.member.domain.specification.rules;

import com.bits.ddd.domain.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.application.dto.DeduplicationResult;
import java.util.HashMap;
import java.util.Map;

public class DeduplicationSpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        DeduplicationResult dedupe = context.deduplicationResult();
        if (dedupe == null) {
            return errors;
        }

        // 1. Check if dedupe service call failed
        if (dedupe.failed()) {
            errors.put(MemberMessageKey.DEDUPE.getKey(), LocalizedMessage.builder()
                    .key("member.dedupe.api.error")
                    .args(new Object[]{dedupe.errorDetail()})
                    .build());
            return errors;
        }

        // 2. Check each duplicate type
        if (dedupe.nationalIdDuplicate()) {
            errors.put(MemberMessageKey.NATIONAL_ID.getKey(), LocalizedMessage.builder()
                    .key("member.nid.duplicate")
                    .build());
        }
        if (dedupe.smartCardDuplicate()) {
            errors.put(MemberMessageKey.SMART_CARD.getKey(), LocalizedMessage.builder()
                    .key("member.smart.card.duplicate")
                    .build());
        }
        if (dedupe.otherIdDuplicate()) {
            errors.put(MemberMessageKey.OTHER_ID.getKey(), LocalizedMessage.builder()
                    .key("member.other.id.duplicate")
                    .build());
        }
        if (dedupe.spouseNidDuplicate()) {
            errors.put(MemberMessageKey.SPOUSE_NID.getKey(), LocalizedMessage.builder()
                    .key("member.spouse.nid.duplicate")
                    .build());
        }
        if (dedupe.generalDuplicate()) {
            errors.put(MemberMessageKey.DEDUPE.getKey(), LocalizedMessage.builder()
                    .key("member.duplicate.information")
                    .build());
        }

        return errors;
    }
}

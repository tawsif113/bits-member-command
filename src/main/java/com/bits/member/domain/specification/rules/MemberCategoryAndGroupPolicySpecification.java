package com.bits.member.domain.specification.rules;

import com.bits.ddd.domain.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.specification.context.MemberValidationContext;
import java.util.Collections;
import java.util.Map;

public class MemberCategoryAndGroupPolicySpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        // TODO: implement rules from Member-Command-DDD-EARS for CreateMember.
        return Collections.emptyMap();
    }
}

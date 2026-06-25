package com.bits.member.domain.specification.rules;

import com.bits.ddd.domain.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.application.dto.sourcedata.ProjectPolicyInfo;
import com.bits.member.application.dto.sourcedata.SavingsProduct;
import com.bits.member.application.dto.sourcedata.SavingsProductPolicy;
import com.bits.member.domain.aggregate.Member;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SavingsProductSpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member aggregate = context.aggregate();
        if (aggregate == null) {
            return errors;
        }

        if (context.sourceData() == null) {
            return errors;
        }

        // 1. Check Savings Product presence
        SavingsProduct product = context.sourceData().getSavingsProduct();
        if (product == null) {
            errors.put(MemberMessageKey.SAVINGS_PRODUCT.getKey(), LocalizedMessage.builder()
                    .key("member.savings.product.invalid")
                    .build());
            return errors;
        }

        // 2. Frequency Check
        ProjectPolicyInfo policy = context.sourceData().getProjectPolicyInfo();
        if (policy != null && policy.collectionFrequency() != null && product.collectionFrequency() != null) {
            if (!product.collectionFrequency().equalsIgnoreCase(policy.collectionFrequency())) {
                errors.put(MemberMessageKey.SAVINGS_PRODUCT.getKey(), LocalizedMessage.builder()
                        .key("member.savings.product.frequency.invalid")
                        .build());
            }
        }

        // 3. Target amount floor check
        SavingsProductPolicy productPolicy = context.sourceData().getSavingsProductPolicy();
        if (productPolicy != null && productPolicy.minDepositAmount() != null && aggregate.getTargetAmount() != null) {
            BigDecimal minInstallment = productPolicy.minDepositAmount();
            if (aggregate.getTargetAmount().compareTo(minInstallment) < 0) {
                errors.put(MemberMessageKey.TARGET_AMOUNT.getKey(), LocalizedMessage.builder()
                        .key("member.target.amount.below.minimum")
                        .build());
            }
        }

        // 4. Update-only transaction check (skipped/guarded since it is creation flow or can check if savingsAccount is loaded)
        // If savingsAccount is present and product has changed (in update flow)
        if (context.sourceData().getSavingsAccount() != null) {
            // Guard/stub if ever called on update
            // Since this is primarily create, we don't block unless we know it's a conflict
        }

        return errors;
    }
}

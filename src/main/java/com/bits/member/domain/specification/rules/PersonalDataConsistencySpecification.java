package com.bits.member.domain.specification.rules;

import com.bits.ddd.domain.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.PersonalInfo;
import java.util.HashMap;
import java.util.Map;

public class PersonalDataConsistencySpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member aggregate = context.aggregate();
        if (aggregate == null) {
            return errors;
        }

        PersonalInfo personal = aggregate.getPersonalInfo();
        if (personal != null) {
            // 1. Member vs Spouse Identity document conflicts
            if ("2".equals(personal.maritalStatusId())) {
                checkIdentityConflict(personal.nationalId(), personal.spNationalId(), "National ID", errors);
                checkIdentityConflict(personal.smartCardId(), personal.spSmartCardId(), "Smart Card ID", errors);
                checkIdentityConflict(personal.passportNo(), personal.spPassportNo(), "Passport", errors);
                checkIdentityConflict(personal.otherIdTypeNo(), personal.spPhotoIdNo(), "Other ID", errors);
            }

            // 2. Member vs Guarantor NID conflict
            GuarantorInfo guarantor = aggregate.getGuarantorInfo();
            if (guarantor != null && guarantor.nationalId() != null && !guarantor.nationalId().trim().isEmpty()) {
                String mNid = personal.nationalId();
                if (mNid != null && mNid.trim().equalsIgnoreCase(guarantor.nationalId().trim())) {
                    errors.put(MemberMessageKey.IDENTITY_CONFLICT.getKey(), LocalizedMessage.builder()
                            .key("member.guarantor.same.id")
                            .args(new Object[]{"National ID"})
                            .build());
                }
            }
        }

        // 3. Bank Info Consistency
        boolean hasAccount = aggregate.getBankAccountNumber() != null && !aggregate.getBankAccountNumber().trim().isEmpty();
        boolean hasRouting = aggregate.getRoutingNumber() != null && !aggregate.getRoutingNumber().trim().isEmpty();
        boolean hasBranch = aggregate.getBankBranchId() != null && !aggregate.getBankBranchId().trim().isEmpty();
        if ((hasAccount || hasRouting) && !hasBranch) {
            errors.put(MemberMessageKey.BANK.getKey(), LocalizedMessage.builder()
                    .key("member.bank.branch.required")
                    .build());
        }

        return errors;
    }

    private void checkIdentityConflict(String memberId, String spouseId, String cardTypeName, Map<String, LocalizedMessage> errors) {
        if (memberId != null && !memberId.trim().isEmpty() && spouseId != null && !spouseId.trim().isEmpty()) {
            if (memberId.trim().equalsIgnoreCase(spouseId.trim())) {
                errors.put(MemberMessageKey.IDENTITY_CONFLICT.getKey(), LocalizedMessage.builder()
                        .key("member.spouse.same.id")
                        .args(new Object[]{cardTypeName})
                        .build());
            }
        }
    }
}

package com.bits.member.domain.specification.rules;

import com.bits.ddd.domain.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.NomineeInfo;
import com.bits.member.domain.entity.PersonalInfo;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class IdentityDocumentSpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member aggregate = context.aggregate();
        if (aggregate == null) {
            return errors;
        }

        LocalDate businessDate = context.businessDate();

        // 1. Validate Member identity documents
        PersonalInfo personal = aggregate.getPersonalInfo();
        if (personal != null) {
            validatePartyDocument(personal.nationalId(), personal.smartCardId(), personal.otherIdTypeNo(),
                    personal.passportNo(), personal.expiryDate(), "Member", errors, businessDate);

            // 2. Validate Spouse identity documents (when married)
            if ("2".equals(personal.maritalStatusId())) {
                validatePartyDocument(personal.spNationalId(), personal.spSmartCardId(), personal.spPhotoIdNo(),
                        personal.spPassportNo(), null, "Spouse", errors, businessDate);
            }
        }

        // 3. Validate Guarantor NID
        GuarantorInfo guarantor = aggregate.getGuarantorInfo();
        if (guarantor != null && guarantor.nationalId() != null && !guarantor.nationalId().trim().isEmpty()) {
            validateNationalId(guarantor.nationalId(), "Guarantor", errors);
        }

        // 4. Validate each Nominee
        if (aggregate.getNominees() != null) {
            for (NomineeInfo nominee : aggregate.getNominees()) {
                validatePartyDocument(nominee.nationalId(), nominee.smartCardId(), nominee.photoIdNo(),
                        nominee.passportNo(), null, "Nominee", errors, businessDate);
            }
        }

        return errors;
    }

    private void validatePartyDocument(String nid, String smartCard, String otherId, String passport,
                                       LocalDate expiryDate, String actorLabel, Map<String, LocalizedMessage> errors,
                                       LocalDate businessDate) {
        if (nid != null && !nid.trim().isEmpty()) {
            validateNationalId(nid, actorLabel, errors);
        }
        if (smartCard != null && !smartCard.trim().isEmpty()) {
            if (smartCard.trim().length() != 10) {
                errors.put(actorLabel + ".smartCard", LocalizedMessage.builder()
                        .key(actorLabel.toLowerCase() + ".smartCard.invalid")
                        .args(new Object[]{actorLabel + " Smart Card ID must be 10 digit"})
                        .build());
            }
        }
        if (otherId != null && !otherId.trim().isEmpty()) {
            if (otherId.trim().length() > 20) {
                errors.put(actorLabel + ".otherId", LocalizedMessage.builder()
                        .key(actorLabel.toLowerCase() + ".otherId.invalid")
                        .args(new Object[]{actorLabel + " Other Id Number Length is incorrect, it should be less then 20 characters."})
                        .build());
            }
        }
        if (passport != null && !passport.trim().isEmpty()) {
            if (expiryDate == null || (businessDate != null && expiryDate.isBefore(businessDate))) {
                errors.put(actorLabel + ".passport", LocalizedMessage.builder()
                        .key(actorLabel.toLowerCase() + ".passport.invalid")
                        .args(new Object[]{actorLabel + " Passport Expiry Date Required or Format not correct"})
                        .build());
            }
        }
    }

    private void validateNationalId(String nid, String actorLabel, Map<String, LocalizedMessage> errors) {
        String trimmed = nid.trim();
        if (trimmed.length() != 13 && trimmed.length() != 17) {
            errors.put(actorLabel + ".nid", LocalizedMessage.builder()
                    .key(actorLabel.toLowerCase() + ".nid.invalid")
                    .args(new Object[]{actorLabel + " National ID must be 13/17 digit"})
                    .build());
        }
    }
}

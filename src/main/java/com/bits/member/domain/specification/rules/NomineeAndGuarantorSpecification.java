package com.bits.member.domain.specification.rules;

import com.bits.ddd.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.application.dto.sourcedata.Relationship;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.GuardianInfo;
import com.bits.member.domain.entity.NomineeInfo;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NomineeAndGuarantorSpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member aggregate = context.aggregate();
        if (aggregate == null) {
            return errors;
        }

        LocalDate businessDate = context.businessDate();
        String maritalStatusId = aggregate.getPersonalInfo() != null ? aggregate.getPersonalInfo().maritalStatusId() : null;
        boolean isUnmarried = maritalStatusId == null || !"2".equals(maritalStatusId);

        // Build valid relationship ID and spouse-relationship ID sets
        Set<String> validRelIds = new HashSet<>();
        Set<String> spouseRelIds = new HashSet<>();
        spouseRelIds.add("20"); // fallback standard spouse relationship id
        if (context.sourceData() != null && context.sourceData().getRelationships() != null) {
            for (Relationship rel : context.sourceData().getRelationships()) {
                if (Boolean.TRUE.equals(rel.getActive())) {
                    validRelIds.add(rel.getId());
                    if (Boolean.TRUE.equals(rel.getSpouseRelationship())) {
                        spouseRelIds.add(rel.getId());
                    }
                }
            }
        }

        boolean hasMinorNominee = false;

        // 1. Nominees validation
        if (aggregate.getNominees() != null) {
            for (NomineeInfo nominee : aggregate.getNominees()) {
                String nomId = nominee.id() != null ? nominee.id() : "";
                // Name
                if (nominee.name() == null || nominee.name().trim().isEmpty()) {
                    errors.put("nominee.name." + nomId, LocalizedMessage.builder()
                            .key("member.nominee.name.required")
                            .build());
                }
                // Relationship presence & validity
                String relId = nominee.relationshipId();
                if (relId == null || !validRelIds.contains(relId)) {
                    errors.put("nominee.rel." + nomId, LocalizedMessage.builder()
                            .key("member.nominee.relationship.invalid")
                            .build());
                } else if (isUnmarried && spouseRelIds.contains(relId)) {
                    // Spouse relationship while unmarried
                    errors.put("nominee.rel." + nomId, LocalizedMessage.builder()
                            .key("member.nominee.spouse.unmarried")
                            .build());
                }

                // Check age / DOB for minor nominee
                int age = -1;
                if (nominee.age() != null) {
                    age = nominee.age();
                } else if (nominee.dateOfBirth() != null && businessDate != null) {
                    age = Period.between(nominee.dateOfBirth(), businessDate).getYears();
                }
                if (age >= 0 && age < 18) {
                    hasMinorNominee = true;
                }
            }
        }

        // 2. Guardian validation for minor nominee
        GuardianInfo guardian = aggregate.getGuardianInfo();
        if (hasMinorNominee && guardian == null) {
            errors.put(MemberMessageKey.GUARDIAN.getKey(), LocalizedMessage.builder()
                    .key("member.guardian.required.for.minor")
                    .build());
        }

        if (guardian != null) {
            String gNid = guardian.nationalId();
            if (gNid != null && !gNid.trim().isEmpty()) {
                String trimmed = gNid.trim();
                if (trimmed.length() != 13 && trimmed.length() != 17) {
                    errors.put(MemberMessageKey.GUARDIAN_NID.getKey(), LocalizedMessage.builder()
                            .key("member.nid.format.invalid")
                            .build());
                }
            }
        }

        // 3. Guarantor validation
        GuarantorInfo guarantor = aggregate.getGuarantorInfo();
        if (guarantor != null) {
            // Guarantor NID
            String gNid = guarantor.nationalId();
            if (gNid == null || gNid.trim().isEmpty()) {
                errors.put(MemberMessageKey.GUARANTOR_NID.getKey(), LocalizedMessage.builder()
                        .key("member.guarantor.nid.required")
                        .build());
            } else {
                String trimmed = gNid.trim();
                if (trimmed.length() != 13 && trimmed.length() != 17) {
                    errors.put(MemberMessageKey.GUARANTOR_NID.getKey(), LocalizedMessage.builder()
                            .key("member.nid.format.invalid")
                            .build());
                }
            }

            // Relationship
            String gRelId = guarantor.relationshipId();
            if (gRelId == null || !validRelIds.contains(gRelId)) {
                errors.put(MemberMessageKey.GUARANTOR_REL.getKey(), LocalizedMessage.builder()
                        .key("member.guarantor.relationship.invalid")
                        .build());
            } else if (isUnmarried && spouseRelIds.contains(gRelId)) {
                errors.put(MemberMessageKey.GUARANTOR_REL.getKey(), LocalizedMessage.builder()
                        .key("member.guarantor.spouse.unmarried")
                        .build());
            }
        }

        return errors;
    }
}

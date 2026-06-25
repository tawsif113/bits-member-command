package com.bits.member.domain.specification.rules;

import com.bits.ddd.domain.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.entity.ContactInfo;
import com.bits.member.domain.entity.MemberAddress;
import com.bits.member.domain.entity.PersonalInfo;
import java.util.HashMap;
import java.util.Map;

public class FieldPresenceAndFormatSpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member aggregate = context.aggregate();
        if (aggregate == null) {
            return errors;
        }

        // 1. Member Name
        String name = aggregate.getMemberName();
        if (name == null || name.trim().isEmpty()) {
            errors.put(MemberMessageKey.MEMBER_NAME.getKey(), LocalizedMessage.builder().key("member.name.required").build());
        } else {
            if (!name.matches("^[a-zA-Z\\s.-]+$") || Character.isDigit(name.trim().charAt(0))) {
                errors.put(MemberMessageKey.MEMBER_NAME.getKey(), LocalizedMessage.builder().key("member.name.format.unsupported").build());
            }
        }

        // 2. PersonalInfo fields
        PersonalInfo personal = aggregate.getPersonalInfo();
        if (personal == null) {
            errors.put(MemberMessageKey.GENDER.getKey(), LocalizedMessage.builder().key("member.gender.required").build());
            errors.put("maritalStatusId", LocalizedMessage.builder().key("member.marital.status.required").build());
            errors.put(MemberMessageKey.DOB.getKey(), LocalizedMessage.builder().key("member.dob.required").build());
            errors.put("fatherName", LocalizedMessage.builder().key("member.father.name.required").build());
            errors.put("motherName", LocalizedMessage.builder().key("member.mother.name.required").build());
        } else {
            // Gender
            if (personal.genderId() == null || personal.genderId().trim().isEmpty()) {
                errors.put(MemberMessageKey.GENDER.getKey(), LocalizedMessage.builder().key("member.gender.required").build());
            }
            // Marital status
            if (personal.maritalStatusId() == null || personal.maritalStatusId().trim().isEmpty()) {
                errors.put("maritalStatusId", LocalizedMessage.builder().key("member.marital.status.required").build());
            }
            // DOB
            if (personal.dateOfBirth() == null) {
                errors.put(MemberMessageKey.DOB.getKey(), LocalizedMessage.builder().key("member.dob.required").build());
            }
            // Father name
            String fName = personal.fatherName();
            if (fName == null || fName.trim().isEmpty()) {
                errors.put("fatherName", LocalizedMessage.builder().key("member.father.name.required").build());
            } else if (!fName.matches("^[a-zA-Z\\s.-]+$") || Character.isDigit(fName.trim().charAt(0))) {
                errors.put("fatherName", LocalizedMessage.builder().key("member.father.name.format.unsupported").build());
            }
            // Mother name
            String mName = personal.motherName();
            if (mName == null || mName.trim().isEmpty()) {
                errors.put("motherName", LocalizedMessage.builder().key("member.mother.name.required").build());
            } else if (!mName.matches("^[a-zA-Z\\s.-]+$") || Character.isDigit(mName.trim().charAt(0))) {
                errors.put("motherName", LocalizedMessage.builder().key("member.mother.name.format.unsupported").build());
            }

            // Married specific fields (maritalStatusId == "2")
            if ("2".equals(personal.maritalStatusId())) {
                String sName = personal.spouseName();
                if (sName == null || sName.trim().isEmpty()) {
                    errors.put("spouseName", LocalizedMessage.builder().key("member.spouse.name.required").build());
                } else if (!sName.matches("^[a-zA-Z\\s.-]+$") || Character.isDigit(sName.trim().charAt(0))) {
                    errors.put("spouseName", LocalizedMessage.builder().key("member.spouse.name.format.unsupported").build());
                }
                if (personal.spouseDateOfBirth() == null) {
                    errors.put("spouseDateOfBirth", LocalizedMessage.builder().key("member.spouse.dob.required").build());
                }
            }
        }

        // 3. ContactInfo fields
        ContactInfo contact = aggregate.getContactInfo();
        if (contact == null) {
            errors.put("contactNo", LocalizedMessage.builder().key("member.mobile.required").build());
            errors.put("presentAddress", LocalizedMessage.builder().key("member.present.address.required").build());
            errors.put("permanentAddress", LocalizedMessage.builder().key("member.permanent.address.required").build());
        } else {
            // Mobile Number
            String contactNo = contact.contactNo();
            if (contactNo == null || contactNo.trim().isEmpty()) {
                errors.put("contactNo", LocalizedMessage.builder().key("member.mobile.required").build());
            } else {
                if (!contactNo.matches("^(?:\\+88|88)?01[3-9]\\d{8}$")) {
                    errors.put("contactNo", LocalizedMessage.builder().key("member.mobile.format.unsupported").build());
                }
            }

            // Addresses: present (1) and permanent (2)
            boolean presentFound = false;
            boolean permanentFound = false;
            if (contact.addresses() != null) {
                for (MemberAddress addr : contact.addresses()) {
                    if ("1".equals(addr.addressTitleId())) {
                        presentFound = true;
                        if (addr.address() == null || addr.address().trim().isEmpty()) {
                            errors.put("presentAddress", LocalizedMessage.builder().key("member.present.address.required").build());
                        }
                        if (addr.thanaId() == null || addr.thanaId().trim().isEmpty()) {
                            errors.put("presentThana", LocalizedMessage.builder().key("member.present.upazila.required").build());
                        }
                    } else if ("2".equals(addr.addressTitleId())) {
                        permanentFound = true;
                        if (addr.address() == null || addr.address().trim().isEmpty()) {
                            errors.put("permanentAddress", LocalizedMessage.builder().key("member.permanent.address.required").build());
                        }
                        if (addr.thanaId() == null || addr.thanaId().trim().isEmpty()) {
                            errors.put("permanentThana", LocalizedMessage.builder().key("member.permanent.upazila.required").build());
                        }
                    }
                }
            }
            if (!presentFound) {
                errors.put("presentAddress", LocalizedMessage.builder().key("member.present.address.required").build());
                errors.put("presentThana", LocalizedMessage.builder().key("member.present.upazila.required").build());
            }
            if (!permanentFound) {
                errors.put("permanentAddress", LocalizedMessage.builder().key("member.permanent.address.required").build());
                errors.put("permanentThana", LocalizedMessage.builder().key("member.permanent.upazila.required").build());
            }
        }

        // 4. Savings product & Target amount
        if (aggregate.getSavingsProductId() == null || aggregate.getSavingsProductId().trim().isEmpty()) {
            errors.put(MemberMessageKey.SAVINGS_PRODUCT.getKey(), LocalizedMessage.builder().key("member.savings.product.required").build());
        }
        if (aggregate.getTargetAmount() == null) {
            errors.put(MemberMessageKey.TARGET_AMOUNT.getKey(), LocalizedMessage.builder().key("member.target.amount.required").build());
        }

        // 5. Identity document general presence (at least one of NID, SmartCard, Passport, Other)
        if (personal != null) {
            boolean hasNid = personal.nationalId() != null && !personal.nationalId().trim().isEmpty();
            boolean hasSmart = personal.smartCardId() != null && !personal.smartCardId().trim().isEmpty();
            boolean hasPassport = personal.passportNo() != null && !personal.passportNo().trim().isEmpty();
            boolean hasOther = personal.otherIdTypeNo() != null && !personal.otherIdTypeNo().trim().isEmpty();
            if (!hasNid && !hasSmart && !hasPassport && !hasOther) {
                errors.put("identityDocument", LocalizedMessage.builder().key("member.identity.required").build());
            }
        }

        return errors;
    }
}

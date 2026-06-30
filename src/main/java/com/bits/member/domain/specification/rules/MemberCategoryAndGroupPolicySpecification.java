package com.bits.member.domain.specification.rules;

import com.bits.ddd.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.application.dto.sourcedata.EmployeeCoreInfo;
import com.bits.member.application.dto.sourcedata.GroupInfo;
import com.bits.member.application.dto.sourcedata.MemberClassification;
import com.bits.member.application.dto.sourcedata.ProjectPolicyInfo;
import com.bits.member.domain.aggregate.Member;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class MemberCategoryAndGroupPolicySpecification implements Specification<MemberValidationContext> {

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

        LocalDate businessDate = context.businessDate();

        // 1. Member Category check
        MemberClassification classification = context.sourceData().getMemberClassification();
        if (classification == null) {
            errors.put(MemberMessageKey.CATEGORY.getKey(), LocalizedMessage.builder()
                    .key("member.category.invalid")
                    .build());
            return errors; // Cannot proceed without classification
        }

        // 2. Age Range check
        if (aggregate.getPersonalInfo() != null && aggregate.getPersonalInfo().dateOfBirth() != null && businessDate != null) {
            int age = Period.between(aggregate.getPersonalInfo().dateOfBirth(), businessDate).getYears();
            Integer ageFrom = classification.getAgeFrom();
            Integer ageTo = classification.getAgeTo();
            if (ageFrom != null && ageTo != null) {
                if (age < ageFrom || age > ageTo) {
                    errors.put(MemberMessageKey.CATEGORY.getKey(), LocalizedMessage.builder()
                            .key("member.category.age.invalid")
                            .build());
                }
            }
        }

        ProjectPolicyInfo policy = context.sourceData().getProjectPolicyInfo();
        if (policy != null) {
            String assocType = policy.getAssociationType();
            if (assocType != null) {
                if (assocType.equalsIgnoreCase("GROUP")) {
                    // Group-associated project
                    GroupInfo group = context.sourceData().getGroupInfo();
                    if (group == null) {
                        errors.put(MemberMessageKey.GROUP.getKey(), LocalizedMessage.builder()
                                .key("member.vo.mandatory")
                                .build());
                    } else {
                        // Check if group is in the branch
                        if (group.getBranchInfoId() != null && !group.getBranchInfoId().equals(aggregate.getBranchInfoId())) {
                            errors.put(MemberMessageKey.GROUP.getKey(), LocalizedMessage.builder()
                                    .key("member.vo.invalid")
                                    .build());
                        }
                        // Check group active status
                        String groupStatus = group.getGroupStatus();
                        if (groupStatus == null || (!groupStatus.equalsIgnoreCase("ACTIVE") && !groupStatus.equalsIgnoreCase("1"))) {
                            errors.put(MemberMessageKey.GROUP.getKey(), LocalizedMessage.builder()
                                    .key("member.vo.not.active")
                                    .build());
                        }
                        // Check gender policy
                        String appGender = group.getApplicableGender();
                        String mGender = aggregate.getPersonalInfo() != null ? aggregate.getPersonalInfo().genderId() : null;
                        if (appGender != null && mGender != null) {
                            if (!appGender.equalsIgnoreCase("BOTH") && !appGender.equalsIgnoreCase("3") && !appGender.equalsIgnoreCase(mGender)) {
                                errors.put(MemberMessageKey.GENDER.getKey(), LocalizedMessage.builder()
                                        .key("member.vo.gender.policy")
                                        .build());
                            }
                        }
                    }
                } else if (assocType.equalsIgnoreCase("MEMBER") || assocType.equalsIgnoreCase("DIRECT")) {
                    // Direct-associated project (PO)
                    EmployeeCoreInfo employee = context.sourceData().getEmployeeCoreInfo();
                    if (employee == null) {
                        errors.put(MemberMessageKey.PO.getKey(), LocalizedMessage.builder()
                                .key("member.po.required")
                                .build());
                    } else {
                        // Verify PO is assigned to branch and project
                        if ((employee.getBranchInfoId() != null && !employee.getBranchInfoId().equals(aggregate.getBranchInfoId()))
                                || (employee.getProjectInfoId() != null && !employee.getProjectInfoId().equals(aggregate.getProjectInfoId()))) {
                            errors.put(MemberMessageKey.PO.getKey(), LocalizedMessage.builder()
                                    .key("member.po.invalid")
                                    .build());
                        }
                    }
                }
            }
        }

        return errors;
    }
}

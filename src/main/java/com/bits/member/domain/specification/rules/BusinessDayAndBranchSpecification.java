package com.bits.member.domain.specification.rules;

import com.bits.ddd.specification.rules.Specification;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.application.dto.sourcedata.PhysicalOfficeInfo;
import com.bits.member.domain.aggregate.Member;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BusinessDayAndBranchSpecification implements Specification<MemberValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(MemberValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member aggregate = context.aggregate();
        if (aggregate == null) {
            return errors;
        }

        if (context.sourceData() == null || context.sourceData().getPhysicalOfficeInfo() == null) {
            return errors; // cannot proceed with office validations
        }

        PhysicalOfficeInfo office = context.sourceData().getPhysicalOfficeInfo();
        LocalDate businessDate = context.businessDate();

        // 1. Office Type Check (only branch office)
        String type = office.getOfficeType();
        if (type == null || (!type.equalsIgnoreCase("BRANCH") && !type.equalsIgnoreCase("BRANCH_OFFICE"))) {
            errors.put(MemberMessageKey.OFFICE.getKey(), LocalizedMessage.builder()
                    .key("member.branch.only")
                    .build());
        }

        // 2. Business Day Open Check
        String status = office.getBusinessDayStatus();
        if (status == null || !status.equalsIgnoreCase("OPEN")) {
            errors.put(MemberMessageKey.BUSINESS_DAY.getKey(), LocalizedMessage.builder()
                    .key("member.business.day.not.open")
                    .build());
        }

        // 3. Application Date Check
        if (aggregate.getApplicationDate() != null && businessDate != null) {
            if (aggregate.getApplicationDate().isAfter(businessDate)) {
                errors.put(MemberMessageKey.APP_DATE.getKey(), LocalizedMessage.builder()
                        .key("member.app.date.invalid")
                        .build());
            }
        }

        // 4. DOB Check
        if (aggregate.getPersonalInfo() != null && aggregate.getPersonalInfo().dateOfBirth() != null && businessDate != null) {
            LocalDate dob = aggregate.getPersonalInfo().dateOfBirth();
            if (!dob.isBefore(businessDate)) {
                errors.put(MemberMessageKey.DOB.getKey(), LocalizedMessage.builder()
                        .key("member.dob.must.be.before.business.date")
                        .build());
            }
        }

        return errors;
    }
}

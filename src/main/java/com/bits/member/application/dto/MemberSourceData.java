package com.bits.member.application.dto;

import com.bits.ddd.application.dto.SourceData;
import com.bits.member.application.dto.sourcedata.Country;
import com.bits.member.application.dto.sourcedata.EmployeeCoreInfo;
import com.bits.member.application.dto.sourcedata.GroupInfo;
import com.bits.member.application.dto.sourcedata.MemberClassification;
import com.bits.member.application.dto.sourcedata.MemberStatus;
import com.bits.member.application.dto.sourcedata.Occupation;
import com.bits.member.application.dto.sourcedata.PhysicalOfficeInfo;
import com.bits.member.application.dto.sourcedata.ProjectInfo;
import com.bits.member.application.dto.sourcedata.ProjectPolicyInfo;
import com.bits.member.application.dto.sourcedata.Relationship;
import com.bits.member.application.dto.sourcedata.SavingsAccount;
import com.bits.member.application.dto.sourcedata.SavingsProduct;
import com.bits.member.application.dto.sourcedata.SavingsProductPolicy;
import com.bits.member.application.dto.sourcedata.Thana;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSourceData extends SourceData {
    private PhysicalOfficeInfo physicalOfficeInfo;
    private ProjectInfo projectInfo;
    private ProjectPolicyInfo projectPolicyInfo;
    private GroupInfo groupInfo;
    private EmployeeCoreInfo employeeCoreInfo;
    private MemberClassification memberClassification;
    private SavingsProduct savingsProduct;
    private SavingsProductPolicy savingsProductPolicy;
    private List<Relationship> relationships;
    private Country country;
    private MemberStatus memberStatus;
    private Occupation occupation;
    private SavingsAccount savingsAccount;
    private List<Thana> thanas;
}

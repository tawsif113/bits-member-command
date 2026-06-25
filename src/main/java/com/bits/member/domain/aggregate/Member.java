package com.bits.member.domain.aggregate;

import com.bits.ddd.domain.aggregate.AggregateRoot;
import com.bits.ddd.shared.domain.enums.DomainStatus;
import com.bits.member.domain.entity.ContactInfo;
import com.bits.member.domain.entity.GuardianInfo;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.NomineeInfo;
import com.bits.member.domain.entity.PersonalInfo;
import com.bits.member.domain.mapper.MemberEventMapper;
import com.bits.member.domain.param.MemberCreationData;
import com.bits.member.domain.valueobject.MembershipStatusChangeHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Member extends AggregateRoot<String> {

    private String id;
    private String memberNo;
    private String memberName;
    private String branchInfoId;
    private String projectInfoId;
    private String groupInfoId;
    private String assignedPoId;
    private String memberClassificationId;
    private String memberStatusId;
    private Integer memberDomainStatus;
    private LocalDate applicationDate;
    private LocalDate membershipDate;
    private PersonalInfo personalInfo;
    private ContactInfo contactInfo;
    private List<NomineeInfo> nominees = new ArrayList<>();
    private GuardianInfo guardianInfo;
    private GuarantorInfo guarantorInfo;
    private List<MembershipStatusChangeHistory> statusHistory = new ArrayList<>();
    private String createdBy;
    private String updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;

    @Override
    public String id() {
        return id;
    }

    public static Member create(MemberCreationData creationData) {
        // TODO: implement full CreateMember behavior from Member-Command-DDD-EARS.
        // Expected flow: populate fields, build MemberValidationContext, run all 8 specs,
        // append initial MembershipStatusChangeHistory, then add MemberCreatedEvent.
        Member member = new Member();
        member.id = UUID.randomUUID().toString();
        member.tracerId = creationData.tracerId();
        member.version = 0L;
        member.status = DomainStatus.CREATED;
        member.memberNo = creationData.memberNo();
        member.branchInfoId = creationData.branchInfoId();
        member.projectInfoId = creationData.projectInfoId();
        member.groupInfoId = creationData.groupInfoId();
        member.assignedPoId = creationData.assignedPoId();
        member.memberClassificationId = creationData.memberClassificationId();
        member.applicationDate = creationData.applicationDate();
        member.membershipDate = creationData.businessDate();
        member.personalInfo = creationData.personalInfo();
        member.contactInfo = creationData.contactInfo();
        member.nominees = creationData.nominees() == null ? new ArrayList<>() : new ArrayList<>(creationData.nominees());
        member.guarantorInfo = creationData.guarantorInfo();
        member.createdBy = creationData.operatorId();
        member.updatedBy = creationData.operatorId();
        member.dateCreated = LocalDateTime.now();
        member.lastUpdated = member.dateCreated;
        member.addEvent(MemberEventMapper.toCreatedEvent(member));
        return member;
    }
}

package com.bits.member.domain.aggregate;

import com.bits.ddd.domain.aggregate.AggregateRoot;
import com.bits.ddd.domain.specification.rules.Specification;
import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.member.domain.entity.ContactInfo;
import com.bits.member.domain.entity.GuardianInfo;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.NomineeInfo;
import com.bits.member.domain.entity.PersonalInfo;
import com.bits.member.domain.exception.MemberValidationException;
import com.bits.member.domain.event.MemberCreatedEvent;
import com.bits.member.domain.event.MemberFailedEvent;
import com.bits.member.domain.mapper.MemberEventMapper;
import com.bits.member.domain.param.MemberCreationData;
import com.bits.member.domain.specification.context.MemberValidationContext;
import com.bits.member.domain.specification.rules.*;
import com.bits.member.domain.valueobject.MembershipStatusChangeHistory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "members")
public class Member extends AggregateRoot<String> {

    @Id
    private String id;
    private String countryId;
    private String branchInfoId;
    private String assignedPoId;
    private String projectInfoId;
    private String groupInfoId;
    private String memberNo;
    private String referenceNo;
    private String groupRefNo;
    private String memberName;
    private String fName;
    private String mName;
    private String lName;
    private LocalDate applicationDate;
    private String surveyReportNo;
    private LocalDate membershipDate;
    private LocalDate lastPoAssignedDate;
    private String memberStatusId;
    private LocalDate expiredDate;
    private LocalDate closingDate;
    private Integer memberDomainStatus;
    private Boolean isTransferredMember;
    private String closeReasonId;
    private String transferTransactionRefNo;
    private Integer loanCycleNo;
    private String memberCustomField;
    private String memberClassificationId;
    private String referredBy;
    private String passbookNo;
    private String tinNumber;
    private String academicQualificationId;
    private String bankId;
    private String bankBranchId;
    private String bankAccountNumber;
    private String routingNumber;
    private Integer incidentStatus;
    private String bufferId;
    private Integer apiDataSourceId;
    private Long uuidNo;
    private String savingsProductId;
    private BigDecimal targetAmount;
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
        Member member = new Member();
        member.id = UUID.randomUUID().toString();
        member.tracerId = creationData.tracerId();
        member.version = 0L;
        member.status = DomainStatus.CREATED;
        
        member.countryId = creationData.countryId();
        member.branchInfoId = creationData.branchInfoId();
        member.assignedPoId = creationData.assignedPoId();
        member.projectInfoId = creationData.projectInfoId();
        member.groupInfoId = creationData.groupInfoId();
        member.memberNo = creationData.memberNo();
        
        member.fName = creationData.fName();
        member.mName = creationData.mName();
        member.lName = creationData.lName();
        member.memberName = buildFullName(creationData.fName(), creationData.mName(), creationData.lName());
        
        member.applicationDate = creationData.applicationDate();
        member.membershipDate = creationData.businessDate();
        member.lastPoAssignedDate = creationData.businessDate();
        member.memberStatusId = "1"; // Active = "1"
        member.memberDomainStatus = 1; // Active = 1
        member.isTransferredMember = false;
        member.loanCycleNo = 0;
        
        member.memberCustomField = creationData.memberCustomField();
        member.memberClassificationId = creationData.memberClassificationId();
        member.referredBy = creationData.referredBy();
        member.passbookNo = creationData.passbookNo();
        member.tinNumber = creationData.tinNumber();
        member.academicQualificationId = creationData.academicQualificationId();
        member.bankId = creationData.bankId();
        member.bankBranchId = creationData.bankBranchId();
        member.bankAccountNumber = creationData.bankAccountNumber();
        member.routingNumber = creationData.routingNumber();
        member.savingsProductId = creationData.savingsProductId();
        member.targetAmount = creationData.targetAmount();
        
        member.personalInfo = creationData.personalInfo();
        member.contactInfo = creationData.contactInfo();
        member.nominees = creationData.nominees() == null ? new ArrayList<>() : new ArrayList<>(creationData.nominees());
        member.guarantorInfo = creationData.guarantorInfo();
        
        member.createdBy = creationData.operatorId();
        member.updatedBy = creationData.operatorId();
        member.dateCreated = LocalDateTime.now();
        member.lastUpdated = member.dateCreated;

        // ── Validation context and run spec chain ──
        MemberValidationContext context = new MemberValidationContext(
                creationData.sourceData(),
                creationData.businessDate(),
                creationData.deduplicationResult(),
                member
        );

        Specification<MemberValidationContext> compositeSpec =
                new FieldPresenceAndFormatSpecification()
                        .and(new IdentityDocumentSpecification())
                        .and(new BusinessDayAndBranchSpecification())
                        .and(new MemberCategoryAndGroupPolicySpecification())
                        .and(new SavingsProductSpecification())
                        .and(new DeduplicationSpecification())
                        .and(new NomineeAndGuarantorSpecification())
                        .and(new PersonalDataConsistencySpecification());

        Map<String, com.bits.ddd.shared.localization.LocalizedMessage> errors = compositeSpec.validate(context);
        if (errors != null && !errors.isEmpty()) {
            throw new MemberValidationException(MemberFailedEvent.validationError(creationData.tracerId(), errors));
        }

        // ── Post-validation nominee share redistribution ──
        if (member.nominees != null && !member.nominees.isEmpty()) {
            BigDecimal size = BigDecimal.valueOf(member.nominees.size());
            BigDecimal equalShare = BigDecimal.valueOf(100).divide(size, 2, java.math.RoundingMode.HALF_UP);
            List<NomineeInfo> redistributed = new ArrayList<>();
            for (NomineeInfo n : member.nominees) {
                redistributed.add(new NomineeInfo(
                        n.id(), n.name(), n.relationshipId(), equalShare,
                        n.dateOfBirth(), n.age(), n.nationalId(), n.smartCardId(),
                        n.passportNo(), n.photoIdNo(), n.contactNo()
                ));
            }
            member.nominees = redistributed;
        }

        // ── Initial status history entry ──
        member.statusHistory.add(new MembershipStatusChangeHistory(
                member.groupInfoId,
                member.projectInfoId,
                member.branchInfoId,
                creationData.businessDate(),
                "1", // Active status id is "1"
                "0", // old status is "0"
                DomainStatus.CREATED,
                creationData.operatorId(),
                creationData.operatorId()
        ));

        member.addEvent(MemberEventMapper.toCreatedEvent(member));
        return member;
    }

    private static String buildFullName(String fName, String mName, String lName) {
        StringBuilder sb = new StringBuilder();
        if (fName != null && !fName.trim().isEmpty()) {
            sb.append(fName.trim());
        }
        if (mName != null && !mName.trim().isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(mName.trim());
        }
        if (lName != null && !lName.trim().isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(lName.trim());
        }
        return sb.toString();
    }
}

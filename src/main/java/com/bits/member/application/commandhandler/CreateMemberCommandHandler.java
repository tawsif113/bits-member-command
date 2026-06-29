package com.bits.member.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.application.handler.CommandHandler;
import com.bits.ddd.application.service.MessageProcessor;
import com.bits.ddd.application.service.SourceDataContext;
import com.bits.ddd.application.service.SourceDataProvider;
import com.bits.ddd.infra.persistence.service.DomainPersistenceService;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.DeduplicationResult;
import com.bits.member.application.dto.MemberSourceData;
import com.bits.member.application.mapper.MemberDataMapper;
import com.bits.member.application.mapper.MemberSourceDataMapper;
import com.bits.member.application.service.DeduplicationService;
import com.bits.member.application.service.MemberConcurrencyLockService;
import com.bits.member.application.service.MemberNumberGenerator;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.enums.MemberErrorCode;
import com.bits.member.domain.param.MemberCreationData;
import com.bits.member.infrastructure.persistence.document.*;
import com.bits.member.infrastructure.persistence.repository.RelationshipDocumentRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
@RegisterCommandHandler
public class CreateMemberCommandHandler implements CommandHandler<CreateMemberCommand> {

    @PersistDomain
    private final DomainPersistenceService<Member, String> persistenceService;
    private final SourceDataProvider<CreateMemberCommand> sourceDataProvider;
    private final MessageProcessor messageProcessor;
    private final MemberConcurrencyLockService lockService;
    private final DeduplicationService deduplicationService;
    private final MemberNumberGenerator memberNumberGenerator;
    private final MemberSourceDataMapper memberSourceDataMapper;
    private final RelationshipDocumentRepository relationshipRepository;

    public CreateMemberCommandHandler(
            DomainPersistenceService<Member, String> persistenceService,
            SourceDataProvider<CreateMemberCommand> sourceDataProvider,
            MessageProcessor messageProcessor,
            MemberConcurrencyLockService lockService,
            DeduplicationService deduplicationService,
            MemberNumberGenerator memberNumberGenerator,
            MemberSourceDataMapper memberSourceDataMapper,
            RelationshipDocumentRepository relationshipRepository) {
        this.persistenceService = persistenceService;
        this.sourceDataProvider = sourceDataProvider;
        this.messageProcessor = messageProcessor;
        this.lockService = lockService;
        this.deduplicationService = deduplicationService;
        this.memberNumberGenerator = memberNumberGenerator;
        this.memberSourceDataMapper = memberSourceDataMapper;
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public void handle(CreateMemberCommand command) {
        String identityLockKey = lockService.identityLockKey(command);
        String branchProjectGroupLockKey = lockService.branchProjectGroupLockKey(command);
        boolean identityLocked = false;
        boolean branchProjectGroupLocked = false;
        try {
            identityLocked = lockService.acquire(identityLockKey);
            branchProjectGroupLocked = lockService.acquire(branchProjectGroupLockKey);

            if (!identityLocked || !branchProjectGroupLocked) {
                throw new DomainValidationException(
                        MemberErrorCode.MEMBER_CREATION_LOCKED.getCode(),
                        "Member Creation process is on going. Please wait and check after few minutes."
                );
            }

            DeduplicationResult deduplicationResult = deduplicationService.checkForCreate(command);
            SourceDataContext context = sourceDataProvider.provide(command);
            MemberSourceData sourceData = mapToMemberSourceData(command, context);

            String memberNo = memberNumberGenerator.nextMemberNo(command, sourceData);
            LocalDate businessDate = sourceData.getPhysicalOfficeInfo() == null
                    ? null
                    : sourceData.getPhysicalOfficeInfo().getBusinessDate();

            MemberCreationData creationData = MemberDataMapper.toCreationData(
                    command, sourceData, deduplicationResult, businessDate, memberNo);

            Member member = Member.create(creationData);
            persistenceService.persist(member);
            messageProcessor.publish(member.getEvents());
            member.clearEvents();
        } finally {
            if (branchProjectGroupLocked) {
                lockService.release(branchProjectGroupLockKey);
            }
            if (identityLocked) {
                lockService.release(identityLockKey);
            }
        }
    }

    private MemberSourceData mapToMemberSourceData(CreateMemberCommand command, SourceDataContext context) {
        MemberSourceData sourceData = new MemberSourceData();

        PhysicalOfficeInfoDocument officeDoc = context.get("physicalOfficeInfo", PhysicalOfficeInfoDocument.class);
        sourceData.setPhysicalOfficeInfo(memberSourceDataMapper.map(officeDoc));

        ProjectInfoDocument projectDoc = context.get("projectInfo", ProjectInfoDocument.class);
        sourceData.setProjectInfo(memberSourceDataMapper.map(projectDoc));

        ProjectPolicyInfoDocument projectPolicyDoc = context.get("projectPolicyInfo", ProjectPolicyInfoDocument.class);
        sourceData.setProjectPolicyInfo(memberSourceDataMapper.map(projectPolicyDoc));

        CountryDocument countryDoc = context.get("country", CountryDocument.class);
        sourceData.setCountry(memberSourceDataMapper.map(countryDoc));

        if (command.getGroupInfoId() != null) {
            GroupInfoDocument groupDoc = context.get("groupInfo", GroupInfoDocument.class);
            sourceData.setGroupInfo(memberSourceDataMapper.map(groupDoc));
        }

        if (command.getAssignedPoId() != null) {
            EmployeeCoreInfoDocument poDoc = context.get("employeeCoreInfo", EmployeeCoreInfoDocument.class);
            sourceData.setEmployeeCoreInfo(memberSourceDataMapper.map(poDoc));
        }

        MemberClassificationDocument classDoc = context.get("memberClassification", MemberClassificationDocument.class);
        sourceData.setMemberClassification(memberSourceDataMapper.map(classDoc));

        SavingsProductDocument productDoc = context.get("savingsProduct", SavingsProductDocument.class);
        sourceData.setSavingsProduct(memberSourceDataMapper.map(productDoc));

        SavingsProductPolicyDocument productPolicyDoc = context.get("savingsProductPolicy", SavingsProductPolicyDocument.class);
        sourceData.setSavingsProductPolicy(memberSourceDataMapper.map(productPolicyDoc));

        java.util.List<com.bits.member.application.dto.sourcedata.Relationship> relationships = new java.util.ArrayList<>();
        for (RelationshipDocument relationshipDocument : relationshipRepository.findAll()) {
            if (Boolean.TRUE.equals(relationshipDocument.getActive())) {
                relationships.add(memberSourceDataMapper.map(relationshipDocument));
            }
        }
        sourceData.setRelationships(relationships);

        if (command.getOccupationId() != null) {
            OccupationDocument occDoc = context.get("occupation", OccupationDocument.class);
            sourceData.setOccupation(memberSourceDataMapper.map(occDoc));
        }

        java.util.List<com.bits.member.application.dto.sourcedata.Thana> thanaList = new java.util.ArrayList<>();
        if (command.getPresentThanaId() != null) {
            ThanaDocument thanaDoc = context.get("presentThana", ThanaDocument.class);
            thanaList.add(memberSourceDataMapper.map(thanaDoc));
        }
        if (command.getPermanentThanaId() != null) {
            ThanaDocument thanaDoc = context.get("permanentThana", ThanaDocument.class);
            thanaList.add(memberSourceDataMapper.map(thanaDoc));
        }
        sourceData.setThanas(thanaList);

        return sourceData;
    }
}

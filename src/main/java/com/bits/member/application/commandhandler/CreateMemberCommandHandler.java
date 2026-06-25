package com.bits.member.application.commandhandler;

import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.application.handler.CommandHandler;
import com.bits.ddd.application.service.MessageProcessor;
import com.bits.ddd.infra.persistence.service.DomainPersistenceService;
import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.DeduplicationResult;
import com.bits.member.application.dto.MemberSourceData;
import com.bits.member.application.mapper.MemberDataMapper;
import com.bits.member.application.service.DeduplicationService;
import com.bits.member.application.service.MemberConcurrencyLockService;
import com.bits.member.application.service.MemberNumberGenerator;
import com.bits.member.application.service.MemberSourceDataRequest;
import com.bits.member.application.service.MemberSourceDataService;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.param.MemberCreationData;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
@RegisterCommandHandler
public class CreateMemberCommandHandler implements CommandHandler<CreateMemberCommand> {

    private final DomainPersistenceService<Member, String> persistenceService;
    private final MemberSourceDataService sourceDataService;
    private final MessageProcessor messageProcessor;
    private final MemberConcurrencyLockService lockService;
    private final DeduplicationService deduplicationService;
    private final MemberNumberGenerator memberNumberGenerator;

    public CreateMemberCommandHandler(
            DomainPersistenceService<Member, String> persistenceService,
            MemberSourceDataService sourceDataService,
            MessageProcessor messageProcessor,
            MemberConcurrencyLockService lockService,
            DeduplicationService deduplicationService,
            MemberNumberGenerator memberNumberGenerator) {
        this.persistenceService = persistenceService;
        this.sourceDataService = sourceDataService;
        this.messageProcessor = messageProcessor;
        this.lockService = lockService;
        this.deduplicationService = deduplicationService;
        this.memberNumberGenerator = memberNumberGenerator;
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
            // TODO: reject when either lock cannot be acquired.
            DeduplicationResult deduplicationResult = deduplicationService.checkForCreate(command);
            MemberSourceData sourceData = sourceDataService.getSourceData(MemberSourceDataRequest.getCreateMap(command));
            String memberNo = memberNumberGenerator.nextMemberNo(command, sourceData);
            LocalDate businessDate = sourceData.getPhysicalOfficeInfo() == null
                    ? null
                    : sourceData.getPhysicalOfficeInfo().businessDate();
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
}

package com.bits.member.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.application.handler.CommandHandler;
import com.bits.ddd.application.service.MessageProcessor;
import com.bits.ddd.infra.persistence.service.DomainPersistenceService;
import com.bits.member.application.command.SaveMemberFamilyCommand;
import com.bits.member.application.dto.sourcedata.Relationship;
import com.bits.member.application.mapper.MemberDataMapper;
import com.bits.member.application.mapper.MemberSourceDataMapper;
import com.bits.member.application.service.MemberQueryService;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.infrastructure.persistence.document.RelationshipDocument;
import com.bits.member.infrastructure.persistence.repository.RelationshipDocumentRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RegisterCommandHandler
public class SaveMemberFamilyCommandHandler implements CommandHandler<SaveMemberFamilyCommand> {

    @PersistDomain
    private final DomainPersistenceService<Member, String> persistenceService;
    private final MemberQueryService memberQueryService;
    private final RelationshipDocumentRepository relationshipRepository;
    private final MemberSourceDataMapper memberSourceDataMapper;
    private final MessageProcessor messageProcessor;

    public SaveMemberFamilyCommandHandler(
            DomainPersistenceService<Member, String> persistenceService,
            MemberQueryService memberQueryService,
            RelationshipDocumentRepository relationshipRepository,
            MemberSourceDataMapper memberSourceDataMapper,
            MessageProcessor messageProcessor) {
        this.persistenceService = persistenceService;
        this.memberQueryService = memberQueryService;
        this.relationshipRepository = relationshipRepository;
        this.memberSourceDataMapper = memberSourceDataMapper;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void handle(SaveMemberFamilyCommand command) {
        Member member = memberQueryService.fetchByIdOrHandleFailure(command.getMemberId(), command.getTracerId());

        List<Relationship> relationships = new ArrayList<>();
        for (RelationshipDocument relationshipDocument : relationshipRepository.findAll()) {
            if (Boolean.TRUE.equals(relationshipDocument.getActive())) {
                relationships.add(memberSourceDataMapper.map(relationshipDocument));
            }
        }

        member.saveFamily(MemberDataMapper.toFamilySaveData(command, relationships));
        persistenceService.persist(member);
        messageProcessor.publish(member.getEvents());
        member.clearEvents();
    }
}

package com.bits.member.infrastructure.config;

import com.bits.ddd.infra.persistence.repository.DomainEventRepository;
import com.bits.ddd.infra.persistence.repository.OutboxRepository;
import com.bits.ddd.infra.persistence.repository.mongo.AggregateRepository;
import com.bits.ddd.infra.persistence.service.DomainPersistenceService;
import com.bits.member.domain.aggregate.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberPersistenceConfig {

    @Bean
    public DomainPersistenceService<Member, String> domainPersistenceService(
            AggregateRepository<Member, String> memberRepository,
            DomainEventRepository eventRepository,
            OutboxRepository outboxRepository) {
        return new com.bits.member.domain.aggregate.MemberPersistenceService(memberRepository, eventRepository, outboxRepository);
    }
}

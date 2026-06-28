package com.bits.member.infrastructure.config;

import com.bits.ddd.infra.persistence.repository.DomainEventRepository;
import com.bits.ddd.infra.persistence.repository.OutboxRepository;
import com.bits.ddd.infra.persistence.repository.mongo.AggregateRepository;
import com.bits.ddd.infra.persistence.service.DomainPersistenceService;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.aggregate.MemberPersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MemberPersistenceConfig {

    @Bean
    @Primary
    public DomainPersistenceService<Member, String> domainPersistenceService(
            AggregateRepository<Member, String> memberRepository,
            DomainEventRepository eventRepository,
            OutboxRepository outboxRepository) {
        return new MemberPersistenceService(memberRepository, eventRepository, outboxRepository);
    }
}

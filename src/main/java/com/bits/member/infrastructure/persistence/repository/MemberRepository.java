package com.bits.member.infrastructure.persistence.repository;

import com.bits.ddd.infra.persistence.repository.mongo.AggregateRepository;
import com.bits.member.domain.aggregate.Member;

public interface MemberRepository extends AggregateRepository<Member, String> {
}

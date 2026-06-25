package com.bits.member.infrastructure.persistence.repository;

import com.bits.ddd.shared.persistence.repository.DomainRepository;
import com.bits.member.domain.aggregate.Member;

public interface MemberRepository extends DomainRepository<Member, String> {
}

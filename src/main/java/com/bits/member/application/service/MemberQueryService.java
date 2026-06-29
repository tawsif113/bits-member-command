package com.bits.member.application.service;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.enums.MemberMessageKey;
import com.bits.member.domain.event.MemberFailedEvent;
import com.bits.member.domain.exception.MemberValidationException;
import com.bits.member.infrastructure.persistence.repository.MemberRepository;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberQueryService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member fetchByIdOrHandleFailure(String id, String traceId) {
        return memberRepository.findById(id).orElseThrow(() -> {
            Map<String, LocalizedMessage> errors = Map.of(
                    MemberMessageKey.MEMBER.getKey(),
                    LocalizedMessage.builder()
                            .key(MemberMessageKey.NOT_FOUND.getKey())
                            .args(new Object[] {id})
                            .build());
            return new MemberValidationException(MemberFailedEvent.validationError(traceId, errors));
        });
    }
}

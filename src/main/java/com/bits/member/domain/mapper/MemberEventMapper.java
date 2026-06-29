package com.bits.member.domain.mapper;

import com.bits.member.domain.aggregate.Member;
import com.bits.member.domain.event.MemberCreatedEvent;
import com.bits.member.domain.event.MemberFamilySavedEvent;

public final class MemberEventMapper {

    private MemberEventMapper() {
    }

    public static MemberCreatedEvent toCreatedEvent(Member member) {
        long version = member.getVersion() == null ? 0L : member.getVersion();
        return new MemberCreatedEvent(
                member.id(),
                member.getMemberNo(),
                member.getMemberName(),
                member.getBranchInfoId(),
                member.getProjectInfoId(),
                version,
                member.getTracerId());
    }

    public static MemberFamilySavedEvent toFamilySavedEvent(Member member) {
        long version = member.getVersion() == null ? 0L : member.getVersion();
        return new MemberFamilySavedEvent(
                member.id(),
                member.getMemberNo(),
                member.getNominees() == null ? 0 : member.getNominees().size(),
                member.getGuarantorInfo() != null,
                version,
                member.getTracerId());
    }
}

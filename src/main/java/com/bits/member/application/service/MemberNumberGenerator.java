package com.bits.member.application.service;

import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.MemberSourceData;

public interface MemberNumberGenerator {
    String nextMemberNo(CreateMemberCommand command, MemberSourceData sourceData);
}

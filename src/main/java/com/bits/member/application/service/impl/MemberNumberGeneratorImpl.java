package com.bits.member.application.service.impl;

import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.MemberSourceData;
import com.bits.member.application.service.MemberNumberGenerator;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MemberNumberGeneratorImpl implements MemberNumberGenerator {
    @Override
    public String nextMemberNo(CreateMemberCommand command, MemberSourceData sourceData) {
        return "M-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

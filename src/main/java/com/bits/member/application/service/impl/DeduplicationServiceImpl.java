package com.bits.member.application.service.impl;

import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.DeduplicationResult;
import com.bits.member.application.service.DeduplicationService;
import org.springframework.stereotype.Service;

@Service
public class DeduplicationServiceImpl implements DeduplicationService {
    @Override
    public DeduplicationResult checkForCreate(CreateMemberCommand command) {
        return DeduplicationResult.notChecked();
    }
}

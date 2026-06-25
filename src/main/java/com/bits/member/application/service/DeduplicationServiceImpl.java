package com.bits.member.application.service;

import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.DeduplicationResult;
import org.springframework.stereotype.Service;

@Service
public class DeduplicationServiceImpl implements DeduplicationService {
    @Override
    public DeduplicationResult checkForCreate(CreateMemberCommand command) {
        return DeduplicationResult.notChecked();
    }
}

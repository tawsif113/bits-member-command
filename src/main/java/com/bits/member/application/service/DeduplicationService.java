package com.bits.member.application.service;

import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.DeduplicationResult;

public interface DeduplicationService {
    DeduplicationResult checkForCreate(CreateMemberCommand command);
}

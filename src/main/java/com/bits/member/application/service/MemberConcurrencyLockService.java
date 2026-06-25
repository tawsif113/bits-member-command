package com.bits.member.application.service;

import com.bits.member.application.command.CreateMemberCommand;

public interface MemberConcurrencyLockService {
    String identityLockKey(CreateMemberCommand command);
    String branchProjectGroupLockKey(CreateMemberCommand command);
    boolean acquire(String lockKey);
    void release(String lockKey);
}

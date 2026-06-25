package com.bits.member.application.service;

import com.bits.member.application.command.CreateMemberCommand;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class MemberConcurrencyLockServiceImpl implements MemberConcurrencyLockService {

    private final ConcurrentHashMap<String, Boolean> locks = new ConcurrentHashMap<>();

    @Override
    public String identityLockKey(CreateMemberCommand command) {
        return "lock:identity:" + command.getNationalId() + ":" + command.getSmartCardId();
    }

    @Override
    public String branchProjectGroupLockKey(CreateMemberCommand command) {
        return "lock:branch-project-group:" + command.getBranchInfoId() + ":" + command.getProjectInfoId() + ":" + command.getGroupInfoId();
    }

    @Override
    public boolean acquire(String lockKey) {
        return locks.putIfAbsent(lockKey, Boolean.TRUE) == null;
    }

    @Override
    public void release(String lockKey) {
        locks.remove(lockKey);
    }
}

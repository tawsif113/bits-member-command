package com.bits.member.application.command;

import com.bits.ddd.shared.messaging.CommandMessage;
import com.bits.member.domain.entity.FamilyInfo;
import com.bits.member.domain.entity.GuarantorInfo;
import com.bits.member.domain.entity.GuardianInfo;
import com.bits.member.domain.entity.NomineeInfo;
import java.util.List;
import lombok.Getter;

@Getter
public class SaveMemberFamilyCommand extends CommandMessage {

    private final String operatorId;
    private final String memberId;
    private final List<NomineeInfo> nominees;
    private final GuardianInfo guardianInfo;
    private final GuarantorInfo guarantorInfo;
    private final FamilyInfo familyInfo;

    public SaveMemberFamilyCommand(
            String tracerId,
            String operatorId,
            String memberId,
            List<NomineeInfo> nominees,
            GuardianInfo guardianInfo,
            GuarantorInfo guarantorInfo,
            FamilyInfo familyInfo) {
        super(tracerId);
        this.operatorId = operatorId;
        this.memberId = memberId;
        this.nominees = nominees;
        this.guardianInfo = guardianInfo;
        this.guarantorInfo = guarantorInfo;
        this.familyInfo = familyInfo;
    }
}

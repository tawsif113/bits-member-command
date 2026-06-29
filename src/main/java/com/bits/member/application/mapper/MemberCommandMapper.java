package com.bits.member.application.mapper;

import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.command.SaveMemberFamilyCommand;
import com.bits.member.presentation.controller.dto.CreateMemberRequestDto;
import com.bits.member.presentation.controller.dto.SaveMemberFamilyRequest;

public final class MemberCommandMapper {

    private MemberCommandMapper() {
    }

    public static CreateMemberCommand toCreateCommand(String tracerId, CreateMemberRequestDto request) {
        return new CreateMemberCommand(
                tracerId,
                request.operatorId(),
                request.branchInfoId(),
                request.projectInfoId(),
                request.groupInfoId(),
                request.assignedPoId(),
                request.memberClassificationId(),
                request.savingsProductId(),
                request.targetAmount(),
                request.applicationDate(),
                request.firstName(),
                request.middleName(),
                request.lastName(),
                request.genderId(),
                request.maritalStatusId(),
                request.dateOfBirth(),
                request.occupationId(),
                request.fatherName(),
                request.motherName(),
                request.spouseName(),
                request.nationalId(),
                request.smartCardId(),
                request.passportNo(),
                request.drivingLicenseNo(),
                request.photoIdNo(),
                request.otherIdTypeId(),
                request.otherIdTypeNo(),
                request.contactNo(),
                request.presentAddress(),
                request.presentThanaId(),
                request.permanentAddress(),
                request.permanentThanaId(),
                request.passbookNo(),
                request.nominees(),
                request.guarantorInfo(),
                request.bankId(),
                request.bankBranchId(),
                request.bankAccountNumber(),
                request.routingNumber(),
                request.tinNumber(),
                request.memberCustomField(),
                request.academicQualificationId(),
                request.referredBy());
    }

    public static SaveMemberFamilyCommand toSaveMemberFamilyCommand(
            String tracerId,
            String id,
            SaveMemberFamilyRequest request) {
        return new SaveMemberFamilyCommand(
                tracerId,
                request.operatorId(),
                id,
                request.nominees(),
                request.guardianInfo(),
                request.guarantorInfo(),
                request.familyInfo());
    }
}

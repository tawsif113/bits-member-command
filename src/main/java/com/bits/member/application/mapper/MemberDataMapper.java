package com.bits.member.application.mapper;

import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.dto.DeduplicationResult;
import com.bits.member.application.dto.MemberSourceData;
import com.bits.member.domain.entity.ContactInfo;
import com.bits.member.domain.entity.MemberAddress;
import com.bits.member.domain.entity.PersonalInfo;
import com.bits.member.domain.param.MemberCreationData;
import java.time.LocalDate;
import java.util.List;

public final class MemberDataMapper {

    private MemberDataMapper() {
    }

    public static MemberCreationData toCreationData(
            CreateMemberCommand command,
            MemberSourceData sourceData,
            DeduplicationResult deduplicationResult,
            LocalDate businessDate,
            String memberNo) {
        PersonalInfo personalInfo = new PersonalInfo(
                null, // salutationId
                command.getNationalId(),
                command.getSmartCardId(),
                command.getPassportNo(),
                command.getDrivingLicenseNo(),
                command.getPhotoIdNo(),
                command.getGenderId(),
                command.getMaritalStatusId(),
                null, // age
                6,    // biometricStatus
                command.getOccupationId(),
                command.getDateOfBirth(),
                command.getFatherName(),
                command.getMotherName(),
                command.getSpouseName(),
                null, // spouseDateOfBirth
                null, // spNationalId
                null, // spSmartCardId
                null, // spPassportNo
                null, // spPhotoIdNo
                null, // bikashWalletNo
                null, // rocketWalletNo
                null, // referralInfoId
                null, // photoReference
                command.getOtherIdTypeId(),
                command.getOtherIdTypeNo(),
                null, // expiryDate
                null, // placeOfIssuingCountry
                null  // isPersonWithDisability
        );
        ContactInfo contactInfo = new ContactInfo(
                command.getContactNo(),
                null,
                List.of(
                        new MemberAddress("1", command.getPresentAddress(), null, null, command.getPresentThanaId(), null),
                        new MemberAddress("2", command.getPermanentAddress(), null, null, command.getPermanentThanaId(), null)));
        return new MemberCreationData(
                command.getTracerId(),
                command.getOperatorId(),
                businessDate,
                memberNo,
                sourceData.getCountry() == null ? null : sourceData.getCountry().getId(),
                command.getBranchInfoId(),
                command.getProjectInfoId(),
                command.getGroupInfoId(),
                command.getAssignedPoId(),
                command.getMemberClassificationId(),
                command.getSavingsProductId(),
                command.getTargetAmount(),
                command.getApplicationDate(),
                command.getFirstName(),
                command.getMiddleName(),
                command.getLastName(),
                command.getMemberCustomField(),
                command.getTinNumber(),
                command.getReferredBy(),
                command.getPassbookNo(),
                command.getBankId(),
                command.getBankBranchId(),
                command.getBankAccountNumber(),
                command.getRoutingNumber(),
                command.getAcademicQualificationId(),
                personalInfo,
                contactInfo,
                command.getNominees(),
                command.getGuarantorInfo(),
                sourceData,
                deduplicationResult);
    }
}

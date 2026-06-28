package com.bits.member.application.mapper;

import com.bits.member.application.dto.sourcedata.*;
import com.bits.member.infrastructure.persistence.document.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberSourceDataMapper {

    PhysicalOfficeInfo map(PhysicalOfficeInfoDocument doc);

    ProjectInfo map(ProjectInfoDocument doc);

    GroupInfo map(GroupInfoDocument doc);

    @Mapping(source = "EDesignationId", target = "eDesignationId")
    @Mapping(source = "FDesignationId", target = "fDesignationId")
    EmployeeCoreInfo map(EmployeeCoreInfoDocument doc);

    MemberClassification map(MemberClassificationDocument doc);

    SavingsProduct map(SavingsProductDocument doc);

    Occupation map(OccupationDocument doc);

    Thana map(ThanaDocument doc);

    Country map(CountryDocument doc);

    Relationship map(RelationshipDocument doc);

    SavingsProductPolicy map(SavingsProductPolicyDocument doc);

    ProjectPolicyInfo map(ProjectPolicyInfoDocument doc);

    MemberStatus map(MemberStatusDocument doc);

    SavingsAccount map(SavingsAccountDocument doc);
}

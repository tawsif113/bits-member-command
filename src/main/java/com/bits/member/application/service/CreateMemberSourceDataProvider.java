package com.bits.member.application.service;

import com.bits.ddd.application.service.SourceDataContext;
import com.bits.ddd.application.service.SourceDataCoordinator;
import com.bits.ddd.application.service.SourceDataProvider;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.domain.enums.MemberErrorCode;
import com.bits.member.infrastructure.persistence.repository.*;
import org.springframework.stereotype.Component;

@Component
public class CreateMemberSourceDataProvider implements SourceDataProvider<CreateMemberCommand> {

    private final SourceDataCoordinator coordinator;
    private final PhysicalOfficeInfoDocumentRepository officeRepository;
    private final ProjectInfoDocumentRepository projectRepository;
    private final ProjectPolicyInfoDocumentRepository projectPolicyRepository;
    private final GroupInfoDocumentRepository groupRepository;
    private final EmployeeCoreInfoDocumentRepository employeeRepository;
    private final MemberClassificationDocumentRepository classificationRepository;
    private final SavingsProductDocumentRepository productRepository;
    private final SavingsProductPolicyDocumentRepository savingsProductPolicyRepository;
    private final CountryDocumentRepository countryRepository;
    private final OccupationDocumentRepository occupationRepository;
    private final ThanaDocumentRepository thanaRepository;

    public CreateMemberSourceDataProvider(
            SourceDataCoordinator coordinator,
            PhysicalOfficeInfoDocumentRepository officeRepository,
            ProjectInfoDocumentRepository projectRepository,
            ProjectPolicyInfoDocumentRepository projectPolicyRepository,
            GroupInfoDocumentRepository groupRepository,
            EmployeeCoreInfoDocumentRepository employeeRepository,
            MemberClassificationDocumentRepository classificationRepository,
            SavingsProductDocumentRepository productRepository,
            SavingsProductPolicyDocumentRepository savingsProductPolicyRepository,
            CountryDocumentRepository countryRepository,
            OccupationDocumentRepository occupationRepository,
            ThanaDocumentRepository thanaRepository) {
        this.coordinator = coordinator;
        this.officeRepository = officeRepository;
        this.projectRepository = projectRepository;
        this.projectPolicyRepository = projectPolicyRepository;
        this.groupRepository = groupRepository;
        this.employeeRepository = employeeRepository;
        this.classificationRepository = classificationRepository;
        this.productRepository = productRepository;
        this.savingsProductPolicyRepository = savingsProductPolicyRepository;
        this.countryRepository = countryRepository;
        this.occupationRepository = occupationRepository;
        this.thanaRepository = thanaRepository;
    }

    @Override
    public SourceDataContext provide(CreateMemberCommand command) {
        SourceDataCoordinator.SourceDataFetchBuilder builder = coordinator.builder(command.getTracerId());

        // 1. PhysicalOfficeInfo (Required)
        builder.add("physicalOfficeInfo", officeRepository, command.getBranchInfoId(), "branchInfoId",
                LocalizedMessage.builder().key("PHYSICAL_OFFICE_NOT_FOUND").build());

        // 2. ProjectInfo (Required)
        builder.add("projectInfo", projectRepository, command.getProjectInfoId(), "projectInfoId",
                LocalizedMessage.builder().key("PROJECT_NOT_FOUND").build());

        // 3. Country (required for member identity/address mapping)
        builder.addCustom("country", countryRepository,
                repo -> repo.findByFieldValue("code", "BD"),
                "code",
                LocalizedMessage.builder().key("COUNTRY_NOT_FOUND").build());

        // 4. ProjectPolicyInfo (needed by category/savings validation)
        builder.addCustom("projectPolicyInfo", projectPolicyRepository,
                repo -> repo.findByFieldValue("projectInfoId", command.getProjectInfoId()),
                "projectInfoId",
                LocalizedMessage.builder().key("PROJECT_POLICY_NOT_FOUND").build());

        // 5. GroupInfo (only when supplied)
        if (command.getGroupInfoId() != null) {
            builder.add("groupInfo", groupRepository, command.getGroupInfoId(), "groupInfoId",
                    LocalizedMessage.builder().key("GROUP_NOT_FOUND").build());
        }

        // 6. EmployeeCoreInfo (only when supplied)
        if (command.getAssignedPoId() != null) {
            builder.add("employeeCoreInfo", employeeRepository, command.getAssignedPoId(), "assignedPoId",
                    LocalizedMessage.builder().key("EMPLOYEE_NOT_FOUND").build());
        }

        // 7. MemberClassification (Required)
        builder.add("memberClassification", classificationRepository, command.getMemberClassificationId(), "memberClassificationId",
                LocalizedMessage.builder().key("MEMBER_CLASSIFICATION_NOT_FOUND").build());

        // 8. SavingsProduct (Required)
        builder.add("savingsProduct", productRepository, command.getSavingsProductId(), "savingsProductId",
                LocalizedMessage.builder().key("SAVINGS_PRODUCT_NOT_FOUND").build());

        // 9. SavingsProductPolicy (needed for target amount validation)
        builder.addCustom("savingsProductPolicy", savingsProductPolicyRepository,
                repo -> repo.findByFieldValue("savingsProductId", command.getSavingsProductId()),
                "savingsProductId",
                LocalizedMessage.builder().key("SAVINGS_PRODUCT_POLICY_NOT_FOUND").build());

        // 10. Occupation (Optional)
        if (command.getOccupationId() != null) {
            builder.add("occupation", occupationRepository, command.getOccupationId(), "occupationId",
                    LocalizedMessage.builder().key("OCCUPATION_NOT_FOUND").build());
        }

        // 11. Present Thana (Optional)
        if (command.getPresentThanaId() != null) {
            builder.add("presentThana", thanaRepository, command.getPresentThanaId(), "presentThanaId",
                    LocalizedMessage.builder().key("PRESENT_THANA_NOT_FOUND").build());
        }

        // 12. Permanent Thana (Optional)
        if (command.getPermanentThanaId() != null) {
            builder.add("permanentThana", thanaRepository, command.getPermanentThanaId(), "permanentThanaId",
                    LocalizedMessage.builder().key("PERMANENT_THANA_NOT_FOUND").build());
        }

        return builder.fetch(errors -> new DomainValidationException(
                MemberErrorCode.SOURCE_DATA_ERROR.getCode(),
                errors.toString()
        ));
    }
}

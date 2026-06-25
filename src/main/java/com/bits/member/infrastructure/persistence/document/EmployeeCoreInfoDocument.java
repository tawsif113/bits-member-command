package com.bits.member.infrastructure.persistence.document;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee_core_info_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class EmployeeCoreInfoDocument extends SourceData<String> {

    @Id
    private String id;

    private Long homeCountryId;
    private String pinNo;
    private LocalDate joiningDate;
    private String salutationId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickName;
    private Long genderId;
    private LocalDate employeeDob;
    private Long domainStatusId;
    private String employeeStatusId;
    private LocalDate provisionEndDate;
    private String curJobStatusId;
    private Boolean isOnDeputation;
    private LocalDate deputationEndDate;
    private LocalDate curJobStartDate;
    private String approvalStatusId;
    private LocalDate approvalDate;
    private Boolean isIssuedLetter;
    private Boolean isIssuedRetireLetter;
    private Long employeeLevelId;
    private Long eDesignationId;
    private Long fDesignationId;
    private String programTypeId;
    private String coreProjectId;
    private String coreProgramId;
    private String departmentId;
    private String supervisorId;
    private Long noticePeriod;
    private Long workingHour;
    private Long workingDayInWeek;
    private Long nomineeForm;
    private Boolean isExpatriate;
    private String emailAddress;
    private Long unitId;
    private String rollNo;
    private String recruitReqNo;
    private Long previousEmpCoreInfoId;
    private String employeeName;
    private String nationalIdNo;
    private String smartNIDNo;
    private Long payGroupId;
    private String currencyName;
    private Boolean isIncrement;
    private Boolean isLeave;
    private Boolean isAttendance;
    private LocalDate joiningDateASPA;
    private Long religionId;
    private Boolean isReceive;
    private Boolean isPostRefRequired;
    private Boolean isPostRefCompleted;
    private Boolean isBackCheckCompleted;
    private Long organogram;
    private Long position;
    private String refErecruitId;
    private Long tinTypeId;
    private String tinNumber;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private String name;
    private String employeeCode;
    private String branchInfoId;
    private String projectInfoId;
    private Boolean active;

    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}

# Member — DDD/CQRS/Event-Sourcing Business Requirements Specification (EARS) — Query Side

> **Format:** Easy Approach to Requirements Syntax (EARS) — DDD/CQRS/ES Edition
> **Source:** Converted from multi-file layered-architecture EARS catalog `source/Member-Domain-EARS-Spec.md`
> **Architecture:** bits.ddd query-side service (CQRS read model)
> **Aggregate:** `Member` — read model document, `member_read` collection
> **Output path:** `output/member/Member-Query-DDD-EARS.md`
> **Traceability:** Every DDD-REQ cites the source EARS section it was derived from, in the format: 📎 Source: {SourceFileName} § "{Section / Sub-section heading}"
> **Companion file:** `output/member/Member-Command-DDD-EARS.md` — command side (CQRS split)

---

## Planning Phase Gate Decisions

> This section records every architectural decision confirmed during the interactive planning phase. Each gate decision is binding: the DDD-REQ requirements below must implement exactly the choice made here.
>
> **Source:** Gate Decision Ledger maintained by the agent throughout Phases 0–5. All 14 gate decisions are shared with the command file. Only decisions relevant to query-side architecture are expanded here.

### Gate 0a — Source File Approach
**Phase:** 0 | **Decision:** ✅ Branch B — multiple files (catalog: `source/Member-Domain-EARS-Spec.md`) | **Architectural effect:** Source citations route to originating EARS file per catalog.

### Gate 0b — Catalog Confirmation
**Phase:** 0 | **Decision:** ✅ 19 actions, 2 EARS files, 5 modules confirmed. | **Architectural effect:** Read operations `ShowMemberInfoAction`, `ListMemberInfoAction`, `EditMemberInfoAction`, `AjaxMemberInfoAction` origin `MemberManagement-EARS-Specification-Resolved.md`.

### Gate 1a — Domain Name, Aggregate Root, Domain Slug
**Phase:** 1 | **Decision:** ✅ Domain = `Member`, AR = `Member`, Slug = `member` | **Architectural effect:** Read model document: `MemberReadDocument`, collection: `member_read`.

### Gate 1b — Aggregate Root Behaviors
**Phase:** 1 | **Decision:** ✅ 4 query-side operations confirmed: `GetMemberById`, `EditMember`, `ListMembers`, `ValidateMember`. | **Architectural effect:** 4 Query objects + 4 Query Handlers generated in this file.

### Gate 2a — Entity Roles
**Phase:** 2 | **Decision:** ✅ Roles confirmed as per command file. | **Architectural effect:** `MemberReadDocument` mirrors all embedded entity fields from the command-side AR schema.

### Gate 2b — Sourcing Mechanism
**Phase:** 2 | **Decision:** ✅ EVENT. | **Architectural effect:** `MemberProjectionHandler` subscribes to all 13 command-side domain events to update `member_read` collection.

### Gate 3d — Behavior Applicability Matrix
**Phase:** 3 | **Decision:** ✅ Read operations have no domain spec chain; validation is at presentation/query layer only. | **Architectural effect:** No `MemberValidationContext` constructed in query handlers.

### Gate 4 — Output File Format
**Phase:** 4 | **Decision:** ✅ Split — this file is the Query side. | **Architectural effect:** All read model DDD-REQs are in this file only.

### Gate 5a — Saga Management
**Phase:** 5 | **Decision:** ✅ No Saga. | **Architectural effect:** `MemberProjectionHandler` is fire-and-forget — no distributed transaction coordination.

### Gate 5b — Security / Idempotency
**Phase:** 5 | **Decision:** ✅ No additional requirements. | **Architectural effect:** Query Controller follows standard BaseApiController pattern with session auth and feature-action ACL.

*(Gates 2c, 3a–3c, 5a–5b are fully documented in the command file and apply equally here.)*

---

## Document Conventions

| Marker | Meaning |
|--------|---------|
| `[INFERRED]` | Required by bits.ddd pattern; no explicit requirement in source EARS |
| `[READ-MODEL]` | CQRS query-side requirement — read model projection / query service / query endpoint |
| `[PSEUDOCODE]` | Java-adjacent pseudocode block |
| `[UNCHANGED]` | Copied verbatim from the original EARS (cross-cutting concern) |
| `[ASYNC]` | Asynchronous side effect — projection handler subscription |

**Source citation format:**
```
📎 Source: {SourceFileName} § "{Section / Sub-section heading}"
```

---

## Behavior Cross-Reference Index

> Query-side operations and their DDD-REQ numbers. Command-side index is in `Member-Command-DDD-EARS.md`.

### Query-Side Operations

| Operation | Entry Point | Query | Handler | Source | Response DTO | DDD-REQ Numbers |
|-----------|------------|-------|---------|--------|-------------|-----------------|
| GetMemberById | GET `/api/members/{key}/{id}` | `GetMemberByIdQuery` | `GetMemberByIdQueryHandler` | `MemberReadDocument` | `MemberDetailResponse` | Q-DDD-REQ-001, 003, 007, 008, 010, 012, 013 |
| EditMember | GET `/api/members/{id}/edit` | `EditMemberQuery` | `EditMemberQueryHandler` | `MemberReadDocument` | `MemberEditResponse` | Q-DDD-REQ-001, 004, 008, 011, 013 |
| ListMembers | GET `/api/members/{key}` | `SearchMembersQuery` | `SearchMembersQueryHandler` | `MemberReadDocument` | `Page<MemberListItem>` | Q-DDD-REQ-001, 005, 009, 011, 013 |
| ValidateMember | GET `/api/members/validate` | `ValidateMemberQuery` | `ValidateMemberQueryHandler` | External de-duplication + snapshot repos | `MemberValidationResponse` | Q-DDD-REQ-001, 006, 010, 013 |

---

## Query / Read Model — Inventory Matrix

| # | Item Type | Count | Items |
|---|-----------|-------|-------|
| 1 | Read Model Documents | 1 | `MemberReadDocument` |
| 2 | Query objects | 4 | `GetMemberByIdQuery`, `EditMemberQuery`, `SearchMembersQuery`, `ValidateMemberQuery` |
| 3 | Query Handlers | 4 | `GetMemberByIdQueryHandler`, `EditMemberQueryHandler`, `SearchMembersQueryHandler`, `ValidateMemberQueryHandler` |
| 4 | Projection Event Handlers | 1 | `MemberProjectionHandler` (13 event subscriptions) |
| 5 | Response DTOs | 4 | `MemberDetailResponse`, `MemberEditResponse`, `MemberListItem`, `MemberValidationResponse` |
| 6 | Query Request DTOs | 4 | `MemberGetRequest`, `MemberSearchRequest`, `MemberEditRequest`, `MemberValidationRequest` |
| 7 | Read Repositories | 1 | `MemberReadRepository` |
| 8 | Read Mappers | 1 | `MemberReadMapper` |

Every item declared in this matrix has its complete schema in the DDD-REQs below.

---

## Read Model Domain Layer

### Q-DDD-REQ-001 — Read Model Document: MemberReadDocument [READ-MODEL]

The Member domain system shall maintain `MemberReadDocument` as the CQRS read-model MongoDB document in `infrastructure/persistence/read/document/`, annotated `@Document(collection = "member_read")`.

This document is **never** written by the command side directly. It is kept current exclusively by `MemberProjectionHandler` subscribing to command-side domain events.

**Fields:**

| Field | Java type | @Field (snake_case) | Description |
|-------|-----------|---------------------|-------------|
| `id` | `String` | `_id` | Aggregate identity (matches command-side `Member.id`) |
| `memberNo` | `String` | `member_no` | Auto-generated member number |
| `memberName` | `String` | `member_name` | Full member name |
| `fName` | `String` | `f_name` | First name (nullable) |
| `mName` | `String` | `m_name` | Middle name (nullable) |
| `lName` | `String` | `l_name` | Last name (nullable) |
| `applicationDate` | `LocalDate` | `application_date` | Membership application date |
| `membershipDate` | `LocalDate` | `membership_date` | Membership effective date |
| `memberStatusId` | `String` | `member_status_id` | Member status reference |
| `memberStatusName` | `String` | `member_status_name` | Denormalized status name |
| `memberDomainStatus` | `Integer` | `member_domain_status` | 1 = active, 2 = inactive |
| `memberClassificationId` | `String` | `member_classification_id` | Category reference |
| `memberCategoryName` | `String` | `member_category_name` | Denormalized category name |
| `countryId` | `String` | `country_id` | Country reference |
| `branchInfoId` | `String` | `branch_info_id` | Branch office reference |
| `branchName` | `String` | `branch_name` | Denormalized branch name |
| `branchCode` | `String` | `branch_code` | Denormalized branch code |
| `projectInfoId` | `String` | `project_info_id` | Project reference |
| `projectName` | `String` | `project_name` | Denormalized project name |
| `groupInfoId` | `String` | `group_info_id` | Group (VO) reference (nullable) |
| `groupName` | `String` | `group_name` | Denormalized group name (nullable) |
| `groupCode` | `String` | `group_code` | Denormalized group code (nullable) |
| `assignedPoId` | `String` | `assigned_po_id` | Field officer reference (nullable) |
| `assignedPoName` | `String` | `assigned_po_name` | Denormalized PO name (nullable) |
| `lastPoAssignedDate` | `LocalDate` | `last_po_assigned_date` | PO assignment date |
| `genderId` | `String` | `gender_id` | Gender reference |
| `maritalStatusId` | `String` | `marital_status_id` | Marital status |
| `dateOfBirth` | `LocalDate` | `date_of_birth` | Date of birth |
| `age` | `Integer` | `age` | Computed age (refreshed at read time; stored for search) |
| `nationalId` | `String` | `national_id` | National identity (nullable) |
| `smartCardId` | `String` | `smart_card_id` | Smart-card identifier (nullable) |
| `passportNo` | `String` | `passport_no` | Passport number (nullable) |
| `drivingLicenseNo` | `String` | `driving_license_no` | Driving licence (nullable) |
| `photoIdNo` | `String` | `photo_id_no` | Photo identity (nullable) |
| `otherIdTypeId` | `Integer` | `other_id_type_id` | Other ID type (nullable) |
| `otherIdTypeNo` | `String` | `other_id_type_no` | Other ID number (nullable) |
| `occupationId` | `String` | `occupation_id` | Occupation reference (nullable) |
| `occupationName` | `String` | `occupation_name` | Denormalized occupation name (nullable) |
| `fatherName` | `String` | `father_name` | Father/husband name (nullable) |
| `motherName` | `String` | `mother_name` | Mother name (nullable) |
| `spouseName` | `String` | `spouse_name` | Spouse name (nullable) |
| `spDateOfBirth` | `LocalDate` | `sp_date_of_birth` | Spouse DOB (nullable) |
| `spNationalId` | `String` | `sp_national_id` | Spouse National ID (nullable) |
| `contactNo` | `String` | `contact_no` | Primary contact number |
| `contactNoOptional` | `String` | `contact_no_optional` | Secondary contact (nullable) |
| `bikashWalletNo` | `String` | `bikash_wallet_no` | bKash wallet (nullable) |
| `rocketWalletNo` | `String` | `rocket_wallet_no` | Rocket wallet (nullable) |
| `presentAddress` | `String` | `present_address` | Present address line (nullable) |
| `presentThanaId` | `String` | `present_thana_id` | Present upazila (nullable) |
| `presentThanaName` | `String` | `present_thana_name` | Denormalized present thana name (nullable) |
| `permanentAddress` | `String` | `permanent_address` | Permanent address line (nullable) |
| `permanentThanaId` | `String` | `permanent_thana_id` | Permanent upazila (nullable) |
| `permanentThanaName` | `String` | `permanent_thana_name` | Denormalized permanent thana name (nullable) |
| `passbookNo` | `String` | `passbook_no` | Current passbook number (nullable) |
| `savingsProductId` | `String` | `savings_product_id` | Savings product reference |
| `savingsProductName` | `String` | `savings_product_name` | Denormalized product name |
| `targetAmount` | `BigDecimal` | `target_amount` | Target savings amount |
| `nominees` | `List<NomineeReadView>` | `nominees` | Projected nominee list |
| `guardianInfo` | `GuardianReadView` | `guardian_info` | Projected guardian (nullable) |
| `guarantorInfo` | `GuarantorReadView` | `guarantor_info` | Projected guarantor (nullable) |
| `assets` | `List<HouseholdAssetReadView>` | `assets` | Projected asset list |
| `signatureReference` | `String` | `signature_reference` | Signature file reference (nullable) |
| `photoReference` | `String` | `photo_reference` | Photo file reference (nullable) |
| `tinNumber` | `String` | `tin_number` | Tax ID (nullable) |
| `bankId` | `String` | `bank_id` | Bank reference (nullable) |
| `bankBranchId` | `String` | `bank_branch_id` | Bank branch (nullable) |
| `bankAccountNumber` | `String` | `bank_account_number` | Bank account (nullable) |
| `routingNumber` | `String` | `routing_number` | Routing number (nullable) |
| `memberCustomField` | `String` | `member_custom_field` | Custom field (nullable) |
| `academicQualificationId` | `String` | `academic_qualification_id` | Qualification (nullable) |
| `loanCycleNo` | `Integer` | `loan_cycle_no` | Loan cycle counter |
| `bufferId` | `String` | `buffer_id` | DCS buffer ID (nullable) |
| `apiDataSourceId` | `Integer` | `api_data_source_id` | Channel origin (nullable) |
| `uuidNo` | `Long` | `uuid_no` | Universal ID (nullable) |
| `isActive` | `Boolean` | `is_active` | False when soft-deleted |
| `domainStatus` | `DomainStatus` | `domain_status` | bits.ddd lifecycle status |
| `createdBy` | `String` | `created_by` | Creating operator |
| `updatedBy` | `String` | `updated_by` | Last-updating operator |
| `dateCreated` | `LocalDateTime` | `date_created` | Creation timestamp |
| `lastUpdated` | `LocalDateTime` | `last_updated` | Last-update timestamp |

**Embedded read sub-documents:**

`NomineeReadView` (embedded in `nominees` list):

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Nominee ID |
| `name` | `String` | Nominee name |
| `relationshipId` | `String` | Relationship reference |
| `relationshipName` | `String` | Denormalized relationship name |
| `sharePercent` | `BigDecimal` | Nomination share |
| `dateOfBirth` | `LocalDate` | DOB (nullable) |
| `age` | `Integer` | Computed age (nullable) |
| `nationalId` | `String` | National ID (nullable) |
| `photoIdNo` | `String` | Photo identity (nullable) |
| `contactNo` | `String` | Contact (nullable) |

`GuardianReadView` (embedded):

| Field | Type | Description |
|-------|------|-------------|
| `guardianName` | `String` | Guardian name |
| `nationalId` | `String` | Guardian NID (nullable) |
| `age` | `Integer` | Computed age (nullable) |
| `relationshipId` | `String` | Relationship (nullable) |
| `address` | `String` | Address (nullable) |

`GuarantorReadView` (embedded):

| Field | Type | Description |
|-------|------|-------------|
| `guarantorName` | `String` | Guarantor name |
| `nationalId` | `String` | Guarantor NID (nullable) |
| `age` | `Integer` | Computed age (nullable) |
| `relationshipId` | `String` | Relationship (nullable) |
| `isActive` | `Boolean` | Whether active |

`HouseholdAssetReadView` (embedded):

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Asset ID |
| `assetName` | `String` | Asset name |
| `assetQuantity` | `Integer` | Quantity |
| `assetValue` | `BigDecimal` | Value |

**Index candidates:**

| Index name | Fields | Type | Query this supports |
|-----------|--------|------|---------------------|
| `_id` | `_id` | Default | Single-get by aggregate ID |
| `idx_key_member_no` | `branch_info_id ASC, member_no ASC` | Compound unique | Member number lookup within branch |
| `idx_key_project_status` | `project_info_id ASC, member_domain_status ASC` | Compound | Project-scoped listing |
| `idx_key_branch_project_status` | `branch_info_id ASC, project_info_id ASC, member_domain_status ASC` | Compound | Branch-project listing |
| `idx_key_group_status` | `group_info_id ASC, member_domain_status ASC` | Compound | Group-scoped listing |
| `idx_key_is_active` | `is_active ASC` | Single | Active/inactive filter |
| `idx_uuid_no` | `uuid_no ASC` | Sparse | Identity search |
| `idx_national_id` | `national_id ASC` | Sparse | NID search / de-duplication real-time |
| `idx_smart_card_id` | `smart_card_id ASC` | Sparse | Smart card search |
| `idx_sp_national_id` | `sp_national_id ASC` | Sparse | Spouse NID search |
| `idx_bikash_wallet` | `bikash_wallet_no ASC` | Sparse | Wallet de-duplication real-time |
| `idx_assigned_po` | `assigned_po_id ASC, branch_info_id ASC` | Compound | PO-based listing |
| `idx_created_at` | `date_created DESC` | Single | Default sort |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing", "Module: Member Validation Rules"
> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Validation Rules"

---

## Query Application Layer

### Q-DDD-REQ-002 — Projection Handler: MemberProjectionHandler [READ-MODEL] [ASYNC]

The Member domain system shall implement `MemberProjectionHandler` in `application/projection/`, annotated `@Service`. It shall subscribe to all 13 command-side domain events via `@RabbitListener` and upsert/project the `MemberReadDocument`.

| Event subscribed | Queue constant | Projection action |
|-----------------|---------------|-------------------|
| `MemberCreatedEvent` | `RabbitMQConstants.MEMBER_CREATED_QUEUE` | Full upsert of `MemberReadDocument` |
| `MemberUpdatedEvent` | `RabbitMQConstants.MEMBER_UPDATED_QUEUE` | Update changed fields |
| `MemberDeletedEvent` | `RabbitMQConstants.MEMBER_DELETED_QUEUE` | Set `isActive = false`, `domainStatus = INACTIVE` |
| `MemberActivatedEvent` | `RabbitMQConstants.MEMBER_ACTIVATED_QUEUE` | Update `memberDomainStatus = 1`, `memberStatusId`, append history |
| `MemberFamilySavedEvent` | `RabbitMQConstants.MEMBER_FAMILY_SAVED_QUEUE` | Update nominees, guardian, guarantor |
| `MemberFamilyUpdatedEvent` | `RabbitMQConstants.MEMBER_FAMILY_UPDATED_QUEUE` | Update family composition fields |
| `MemberPhotoUpdatedEvent` | `RabbitMQConstants.MEMBER_PHOTO_UPDATED_QUEUE` | Update `photoReference` |
| `MemberSignatureUpdatedEvent` | `RabbitMQConstants.MEMBER_SIGNATURE_UPDATED_QUEUE` | Update `signatureReference` |
| `MembershipDocumentUpdatedEvent` | `RabbitMQConstants.MEMBER_DOCUMENT_UPDATED_QUEUE` | Update document file references |
| `MemberAssetUpdatedEvent` | `RabbitMQConstants.MEMBER_ASSET_UPDATED_QUEUE` | Update `assets` list |
| `MemberProjectOfficerReassignedEvent` | `RabbitMQConstants.MEMBER_PO_REASSIGNED_QUEUE` | Update `assignedPoId`, `assignedPoName`, `lastPoAssignedDate` |
| `MemberAdmissionApprovedEvent` | `RabbitMQConstants.MEMBER_ADMISSION_APPROVED_QUEUE` | Full upsert + set `bufferId`, `apiDataSourceId = 2` |
| `MemberUpdateApprovedEvent` | `RabbitMQConstants.MEMBER_UPDATE_APPROVED_QUEUE` | Update changed fields + DCS buffer metadata |

**Behaviour — onMemberCreated(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_CREATED_QUEUE)
onMemberCreated(MemberCreatedEvent event):
  document = new MemberReadDocument()
  document.id                = event.memberId
  document.memberNo          = event.memberNo
  document.memberName        = event.memberName
  document.applicationDate   = event.applicationDate
  document.membershipDate    = event.membershipDate
  document.memberStatusId    = event.memberStatusId
  document.memberDomainStatus = 1
  document.memberClassificationId = event.memberClassificationId
  document.countryId         = event.countryId
  document.branchInfoId      = event.branchInfoId
  document.projectInfoId     = event.projectInfoId
  document.groupInfoId       = event.groupInfoId      // nullable
  document.assignedPoId      = event.assignedPoId     // nullable
  document.lastPoAssignedDate = event.membershipDate
  document.genderId          = event.genderId
  document.nationalId        = event.nationalId       // nullable
  document.dateCreated       = event.eventTimestamp
  document.lastUpdated       = event.eventTimestamp
  document.isActive          = true
  document.domainStatus      = DomainStatus.CREATED
  // Enrich denormalized reference names from snapshot repositories
  document.branchName        = physicalOfficeInfoRepository.findById(event.branchInfoId).map(d -> d.officeName).orElse(null)
  document.projectName       = projectInfoRepository.findById(event.projectInfoId).map(d -> d.projectName).orElse(null)
  document.groupName         = event.groupInfoId != null ? groupInfoRepository.findById(event.groupInfoId).map(d -> d.groupName).orElse(null) : null
  document.memberStatusName  = memberStatusRepository.findById(event.memberStatusId).map(d -> d.statusName).orElse(null)
  document.memberCategoryName = memberClassificationRepository.findById(event.memberClassificationId).map(d -> d.categoryName).orElse(null)
  memberReadRepository.save(document)
```

**Behaviour — onMemberUpdated(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_UPDATED_QUEUE)
onMemberUpdated(MemberUpdatedEvent event):
  document = memberReadRepository.findById(event.memberId).orElse(new MemberReadDocument())
  document.memberName        = event.memberName
  // Apply all updated fields from event
  document.lastUpdated       = event.eventTimestamp
  document.domainStatus      = DomainStatus.UPDATED
  // Re-enrich denormalized names for any changed reference IDs
  memberReadRepository.save(document)
```

**Behaviour — onMemberDeleted(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_DELETED_QUEUE)
onMemberDeleted(MemberDeletedEvent event):
  document = memberReadRepository.findById(event.memberId).orElse(new MemberReadDocument())
  document.isActive          = false
  document.memberDomainStatus = 2
  document.domainStatus      = DomainStatus.UPDATED
  document.lastUpdated       = event.eventTimestamp
  // Retain all fields; project minimal record so it still appears in inactive queries
  memberReadRepository.save(document)
```

**Behaviour — onMemberActivated(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_ACTIVATED_QUEUE)
onMemberActivated(MemberActivatedEvent event):
  document = memberReadRepository.findById(event.memberId).orElse(new MemberReadDocument())
  document.memberStatusId    = event.memberStatusId
  document.memberDomainStatus = 1
  document.isActive          = true
  document.domainStatus      = DomainStatus.UPDATED
  document.lastUpdated       = event.eventTimestamp
  memberReadRepository.save(document)
```

**Behaviour — onMemberFamilySaved(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_FAMILY_SAVED_QUEUE)
onMemberFamilySaved(MemberFamilySavedEvent event):
  document = memberReadRepository.findById(event.memberId).orElse(new MemberReadDocument())
  // nominees, guardianInfo, guarantorInfo are projected inline from the aggregate snapshot
  // (the event carries a nominee count, not the full list — full list fetched from command store if needed)
  document.domainStatus      = DomainStatus.UPDATED
  document.lastUpdated       = event.eventTimestamp
  memberReadRepository.save(document)
```

**Behaviour — onMemberPhotoUpdated(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_PHOTO_UPDATED_QUEUE)
onMemberPhotoUpdated(MemberPhotoUpdatedEvent event):
  document = memberReadRepository.findById(event.memberId).orElse(new MemberReadDocument())
  document.photoReference    = event.fileReference
  document.lastUpdated       = event.eventTimestamp
  memberReadRepository.save(document)
```

**Behaviour — onMemberSignatureUpdated(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_SIGNATURE_UPDATED_QUEUE)
onMemberSignatureUpdated(MemberSignatureUpdatedEvent event):
  document = memberReadRepository.findById(event.memberId).orElse(new MemberReadDocument())
  document.signatureReference = event.fileReference
  document.lastUpdated        = event.eventTimestamp
  memberReadRepository.save(document)
```

**Behaviour — onMemberProjectOfficerReassigned(event):** [PSEUDOCODE]

```pseudocode
@RabbitListener(queues = RabbitMQConstants.MEMBER_PO_REASSIGNED_QUEUE)
onMemberProjectOfficerReassigned(MemberProjectOfficerReassignedEvent event):
  document = memberReadRepository.findById(event.memberId).orElse(new MemberReadDocument())
  document.assignedPoId       = event.newPoId
  document.assignedPoName     = employeeCoreInfoRepository.findById(event.newPoId).map(d -> d.employeeName).orElse(null)
  document.lastPoAssignedDate = event.reassignedDate
  document.lastUpdated        = event.eventTimestamp
  memberReadRepository.save(document)
```

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing" [INFERRED for projection logic]
> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval" [INFERRED for projection logic]

---

### Q-DDD-REQ-003 — Query: GetMemberByIdQuery [READ-MODEL]

The Member domain system shall define `GetMemberByIdQuery` as an immutable Java `record` in `application/query/`.

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `branchInfoId` | `String` | `@NotBlank` | Branch scope key |
| `memberId` | `String` | `@NotBlank` | Aggregate identifier |
| `requestingUserId` | `String` | `@NotBlank` | Requesting user (for access check) |
| `permittedProjectIds` | `List<String>` | `@NotNull` | User's permitted projects (for access check) |
| `permittedOfficeIds` | `List<String>` | `@NotNull` | User's permitted offices (for access check) |
| `isProgrammeAdmin` | `Boolean` | `@NotNull` | Whether user is programme administrator |
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing"

---

### Q-DDD-REQ-004 — Query: EditMemberQuery [READ-MODEL]

The Member domain system shall define `EditMemberQuery` as an immutable Java `record` in `application/query/`.

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `memberId` | `String` | `@NotBlank` | Member to load for editing |
| `operatorOfficeId` | `String` | `@NotBlank` | Operator's office (for permitted categories filter) |
| `requestingUserId` | `String` | `@NotBlank` | Requesting user |
| `permittedProjectIds` | `List<String>` | `@NotNull` | User's permitted projects |
| `permittedOfficeIds` | `List<String>` | `@NotNull` | User's permitted offices |
| `isProgrammeAdmin` | `Boolean` | `@NotNull` | Programme administrator flag |
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Edit Form Loading"

---

### Q-DDD-REQ-005 — Query: SearchMembersQuery [READ-MODEL]

The Member domain system shall define `SearchMembersQuery` as an immutable Java `record` in `application/query/`.

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `branchInfoId` | `String` | `@NotBlank` | Branch scope key (fixed filter) |
| `projectInfoId` | `String` | `@Nullable` | Project filter |
| `groupInfoId` | `String` | `@Nullable` | Group (VO) filter |
| `memberStatusId` | `String` | `@Nullable` | Member status filter |
| `memberDomainStatus` | `Integer` | `@Nullable` | Active (1) or inactive (2) filter |
| `memberName` | `String` | `@Nullable` | Name search (partial match) |
| `memberNo` | `String` | `@Nullable` | Member number search |
| `nationalId` | `String` | `@Nullable` | NID search |
| `assignedPoId` | `String` | `@Nullable` | Project officer filter |
| `memberClassificationId` | `String` | `@Nullable` | Category filter |
| `dateCreatedFrom` | `LocalDate` | `@Nullable` | Registration date range start |
| `dateCreatedTo` | `LocalDate` | `@Nullable` | Registration date range end |
| `isAsyncFeed` | `Boolean` | `@NotNull` | True = return as data feed (for async list); false = return projects+offices |
| `page` | `int` | default 0 | Pagination page number |
| `size` | `int` | default 20 | Pagination page size |
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing"

---

### Q-DDD-REQ-006 — Query: ValidateMemberQuery [READ-MODEL]

The Member domain system shall define `ValidateMemberQuery` as an immutable Java `record` in `application/query/`. Used for real-time field checks during registration/edit screens.

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `validationType` | `String` | `@NotBlank` | "NATIONAL_ID", "SMART_CARD", "OTHER_ID", "WALLET", "GUARANTOR_NID", "DOB_AGE", "INSURANCE_AGE" |
| `fieldValue` | `String` | `@NotBlank` | Value to check |
| `memberId` | `String` | `@Nullable` | Current member ID (excluded from de-duplication on update) |
| `memberClassificationId` | `String` | `@Nullable` | For DOB_AGE check |
| `dateOfBirth` | `LocalDate` | `@Nullable` | For DOB_AGE and INSURANCE_AGE checks |
| `hasCurrentLoan` | `Boolean` | `@Nullable` | For age > 70 check |
| `hasActiveInsurance` | `Boolean` | `@Nullable` | For insurance age change check |
| `currentInsuranceAgeGroup` | `String` | `@Nullable` | For insurance age change check |
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Validation Rules"

---

### Q-DDD-REQ-007 — Query Handler: GetMemberByIdQueryHandler [READ-MODEL]

The Member domain system shall implement `GetMemberByIdQueryHandler` in `application/queryhandler/`, annotated `@Service`, implementing `QueryHandler<GetMemberByIdQuery>`.

**Responsibilities (in order):**
1. Load read model: `readRepository.findByIdAndIsActiveTrue(query.memberId())`
2. If not found: throw `MemberNotFoundException` with localized not-found message.
3. **Access control check:** If member's project not in `query.permittedProjectIds()` AND (project + office not in permitted) AND not programme admin → reject with "You do not have authority to view this member…".
4. **Computed field — age:** compute from `record.dateOfBirth` and current date; apply to response.
5. **Computed field — nominee ages:** compute each nominee's age from `nominee.dateOfBirth`.
6. **Computed field — guardian age:** compute from `guardianInfo.dateOfBirth`.
7. **Reference-data enrichments:** look up denormalized names already stored in read document.
8. Map to response DTO: `MemberReadMapper.toDetailResponse(record)`.
9. Return `MemberDetailResponse`.

**Injected dependencies:**
- `MemberReadRepository`
- `MemberReadMapper`

**Behaviour — handle(query):** [PSEUDOCODE]

```pseudocode
handle(GetMemberByIdQuery query):
  // Step 1 — Load from read model
  record = memberReadRepository.findByIdAndIsActiveTrue(query.memberId())
  IF record is null THEN
    THROW MemberNotFoundException(
      message = MessageKey.NOT_FOUND, args = [query.memberId()])
  END IF

  // Step 3 — Access control
  IF NOT query.isProgrammeAdmin() THEN
    IF record.projectInfoId NOT IN query.permittedProjectIds() THEN
      IF NOT (record.projectInfoId IN query.permittedProjectIds()
              AND record.branchInfoId IN query.permittedOfficeIds()) THEN
        THROW MemberAccessDeniedException("You do not have authority to view this member...")
      END IF
    END IF
  END IF

  // Step 4 — Computed fields
  record.age = computeAge(record.dateOfBirth, LocalDate.now())
  FOR EACH nominee IN record.nominees DO
    nominee.age = computeAge(nominee.dateOfBirth, LocalDate.now())
  END FOR
  IF record.guardianInfo is not null THEN
    record.guardianInfo.age = computeAge(record.guardianInfo.dateOfBirth, LocalDate.now())
  END IF

  // Denormalized names already stored in read document from projection
  // Step 8 — Map to response DTO
  RETURN MemberReadMapper.toDetailResponse(record)
```

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Authentication and Authorisation"

---

### Q-DDD-REQ-008 — Query Handler: EditMemberQueryHandler [READ-MODEL]

The Member domain system shall implement `EditMemberQueryHandler` in `application/queryhandler/`, annotated `@Service`, implementing `QueryHandler<EditMemberQuery>`.

**Responsibilities (in order):**
1. Load read model: `readRepository.findById(query.memberId())`.
2. If not found: throw `MemberNotFoundException` with "Member not found".
3. **Access control:** same project/office check as GetMemberById.
4. **Compute age** from dateOfBirth and current business date.
5. **Load permitted member categories:** from `MemberClassificationRepository` filtered by project and excluding categories excluded for operator's office.
6. **Load active passbook** or compute next passbook index.
7. **Compute derived savings fields:** current savings product, target amount, whether project is TUP (targeting-the-ultra-poor).
8. **Mark identical addresses:** if present and permanent address first two fields match, flag `addressesIdentical = true`.
9. Map to response DTO: `MemberReadMapper.toEditResponse(record, permittedCategories, ...)`.
10. Return `MemberEditResponse`.

**Injected dependencies:**
- `MemberReadRepository`
- `MemberClassificationRepository`
- `SavingsAccountRepository`
- `MemberReadMapper`

**Behaviour — handle(query):** [PSEUDOCODE]

```pseudocode
handle(EditMemberQuery query):
  record = memberReadRepository.findById(query.memberId())
  IF record is null THEN
    THROW MemberNotFoundException(message = "Member not found")
  END IF

  // Access control
  checkProjectOfficeAccess(record, query)

  // Compute age
  record.age = computeAge(record.dateOfBirth, LocalDate.now())

  // Permitted categories for the project, excluding those excluded for operator's office
  permittedCategories = memberClassificationRepository
    .findAllByIsActiveTrue()
    .filter(c -> isPermittedForProject(c, record.projectInfoId))
    .filter(c -> NOT isExcludedForOffice(c, query.operatorOfficeId()))
    .sortedByName()

  // Active passbook
  activePassbook = record.passbookNo
  nextPassbookIndex = activePassbook ?? computeNextPassbookIndex(record.memberNo)

  // Savings data
  savingsAccount = savingsAccountRepository.findByMemberId(record.id)
  isTupProject = projectInfoRepository.findById(record.projectInfoId).map(p -> p.isTupProject).orElse(false)

  // Address identity check
  addressesIdentical = record.presentAddress?.equals(record.permanentAddress)
                       AND record.presentThanaId?.equals(record.permanentThanaId)

  RETURN MemberReadMapper.toEditResponse(record, permittedCategories, activePassbook,
           nextPassbookIndex, savingsAccount, isTupProject, addressesIdentical)
```

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Edit Form Loading"

---

### Q-DDD-REQ-009 — Query Handler: SearchMembersQueryHandler [READ-MODEL]

The Member domain system shall implement `SearchMembersQueryHandler` in `application/queryhandler/`, annotated `@Service`, implementing `QueryHandler<SearchMembersQuery>`.

**Responsibilities (in order):**
1. If `query.isAsyncFeed()` is true: build criteria and return paged list of `MemberListItem`.
2. If `query.isAsyncFeed()` is false: return the authorised projects and visible offices to the operator (for rendering the list screen) — these are loaded from snapshot repositories.
3. Build MongoDB query from `SearchMembersQuery` filter fields.
4. Apply fixed filters (always-on): `branchInfoId = query.branchInfoId()`.
5. Apply optional filters (when non-null): projectInfoId, groupInfoId, memberStatusId, memberDomainStatus, memberName, memberNo, nationalId, assignedPoId, memberClassificationId, date range.
6. Apply default sort: `dateCreated DESC`.
7. Apply pagination.
8. Execute against `MemberReadRepository`.
9. Map to `Page<MemberListItem>`.

**Behaviour — handle(query):** [PSEUDOCODE]

```pseudocode
handle(SearchMembersQuery query):
  IF NOT query.isAsyncFeed() THEN
    // Return screen metadata (projects + offices)
    permittedProjects = projectInfoRepository.findAllByIsActiveTrue()
      .filter(p -> query.permittedProjectIds().contains(p.id))
    visibleOffices = physicalOfficeInfoRepository.findAllByBranchInfoId(query.branchInfoId())
    RETURN MemberListScreenResponse(permittedProjects, visibleOffices)
  END IF

  // Async data feed path
  criteria = Criteria.where("branch_info_id").is(query.branchInfoId())  // fixed filter

  // Optional filters — apply when non-null
  IF query.projectInfoId() is not null THEN criteria.and("project_info_id").is(query.projectInfoId()) END IF
  IF query.groupInfoId() is not null THEN criteria.and("group_info_id").is(query.groupInfoId()) END IF
  IF query.memberStatusId() is not null THEN criteria.and("member_status_id").is(query.memberStatusId()) END IF
  IF query.memberDomainStatus() is not null THEN criteria.and("member_domain_status").is(query.memberDomainStatus()) END IF
  IF query.memberName() is not null THEN criteria.and("member_name").regex(query.memberName(), "i") END IF
  IF query.memberNo() is not null THEN criteria.and("member_no").is(query.memberNo()) END IF
  IF query.nationalId() is not null THEN criteria.and("national_id").is(query.nationalId()) END IF
  IF query.assignedPoId() is not null THEN criteria.and("assigned_po_id").is(query.assignedPoId()) END IF
  IF query.memberClassificationId() is not null THEN criteria.and("member_classification_id").is(query.memberClassificationId()) END IF
  IF query.dateCreatedFrom() is not null THEN criteria.and("date_created").gte(query.dateCreatedFrom()) END IF
  IF query.dateCreatedTo() is not null THEN criteria.and("date_created").lte(query.dateCreatedTo()) END IF

  pageable = PageRequest.of(query.page(), query.size(), Sort.by("date_created").descending())
  results = memberReadRepository.findAll(criteria, pageable)
  RETURN results.map(MemberReadMapper::toListItem)
```

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing"

---

### Q-DDD-REQ-010 — Query Handler: ValidateMemberQueryHandler [READ-MODEL]

The Member domain system shall implement `ValidateMemberQueryHandler` in `application/queryhandler/`, annotated `@Service`, implementing `QueryHandler<ValidateMemberQuery>`.

**Responsibilities (in order) — by validationType:**

For `NATIONAL_ID`:
1. Call external de-duplication service for national identity.
2. If suspected duplicate found: return `MemberValidationResponse(valid=false, message="Same National ID already exists for another member.")`.
3. Otherwise: return `MemberValidationResponse(valid=true)`.

For `SMART_CARD`:
1. Call external de-duplication service for smart-card identifier.
2. Return response accordingly.

For `OTHER_ID` (passport, birth cert, driving licence):
1. Call external de-duplication service for other identity.
2. Return response with type-specific message.

For `WALLET`:
1. Check `memberReadRepository.findByBikashWalletNoOrRocketWalletNo(query.fieldValue(), query.memberId())`.
2. If found: return `MemberValidationResponse(valid=false, message="Same Wallet Number already exists for another member")`.

For `GUARANTOR_NID`:
1. Check a guarantor snapshot repository for NID duplicates.
2. Return accordingly.

For `DOB_AGE`:
1. Compute age from `query.dateOfBirth()`.
2. If `query.hasCurrentLoan() == true` AND age > 70: return `MemberValidationResponse(valid=false, message="Member age can not be greater than 70")`.
3. Check insurance age group change (if `query.hasActiveInsurance() == true`): call insurance service.
4. Load member classification, check age against `ageFrom–ageTo`.

For `INSURANCE_AGE`:
1. Call external insurance service for premium age eligibility.
2. Return service result.

**Injected dependencies:**
- `MemberReadRepository`
- `DeduplicationService` (external HTTP client)
- `InsuranceAgeService` (external HTTP client)
- Various snapshot repositories

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Validation Rules"

---

### Q-DDD-REQ-011 — Response DTO: MemberDetailResponse [READ-MODEL]

The Member domain system shall define `MemberDetailResponse` as an immutable Java `record` in `application/query/dto/`. Used for single-record enriched response (GetMemberById).

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Aggregate identifier |
| `memberNo` | `String` | Member number |
| `memberName` | `String` | Full name |
| `age` | `Integer` | Computed age |
| `dateOfBirth` | `LocalDate` | Date of birth |
| `genderId` | `String` | Gender |
| `maritalStatusId` | `String` | Marital status |
| `nationalId` | `String` | National ID (nullable) |
| `smartCardId` | `String` | Smart card (nullable) |
| `passportNo` | `String` | Passport (nullable) |
| `drivingLicenseNo` | `String` | Driving licence (nullable) |
| `photoIdNo` | `String` | Photo identity (nullable) |
| `fatherName` | `String` | Father name (nullable) |
| `motherName` | `String` | Mother name (nullable) |
| `spouseName` | `String` | Spouse name (nullable) |
| `contactNo` | `String` | Primary contact |
| `contactNoOptional` | `String` | Secondary contact (nullable) |
| `presentAddress` | `String` | Present address (nullable) |
| `presentThanaName` | `String` | Present thana name (nullable) |
| `permanentAddress` | `String` | Permanent address (nullable) |
| `permanentThanaName` | `String` | Permanent thana name (nullable) |
| `branchName` | `String` | Branch name |
| `projectName` | `String` | Project name |
| `groupName` | `String` | Group name (nullable) |
| `assignedPoName` | `String` | PO name (nullable) |
| `memberCategoryName` | `String` | Category name |
| `memberStatusName` | `String` | Status name |
| `membershipDate` | `LocalDate` | Membership date |
| `applicationDate` | `LocalDate` | Application date |
| `savingsProductName` | `String` | Savings product name |
| `targetAmount` | `BigDecimal` | Target savings amount |
| `passbookNo` | `String` | Passbook number (nullable) |
| `nominees` | `List<NomineeReadView>` | Full nominee list |
| `guardianInfo` | `GuardianReadView` | Guardian (nullable) |
| `guarantorInfo` | `GuarantorReadView` | Guarantor (nullable) |
| `assets` | `List<HouseholdAssetReadView>` | Asset list |
| `signatureReference` | `String` | Signature file reference (nullable) |
| `photoReference` | `String` | Photo file reference (nullable) |
| `tinNumber` | `String` | Tax ID (nullable) |
| `bankId` | `String` | Bank (nullable) |
| `bankBranchId` | `String` | Bank branch (nullable) |
| `bankAccountNumber` | `String` | Bank account (nullable) |
| `isActive` | `Boolean` | Active status |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing"

---

### Q-DDD-REQ-012 — Response DTO: MemberEditResponse [READ-MODEL]

The Member domain system shall define `MemberEditResponse` as an immutable Java `record` in `application/query/dto/`. Used for edit-form loading (EditMember).

| Field | Type | Description |
|-------|------|-------------|
| All fields from `MemberDetailResponse` | — | Full member details |
| `permittedCategories` | `List<MemberCategoryOption>` | Categories permitted for project, excluding excluded-for-office |
| `activePassbook` | `String` | Active passbook number (nullable) |
| `nextPassbookIndex` | `String` | Next passbook index when no active passbook |
| `savingsAccountId` | `String` | Savings account reference |
| `savingsProductId` | `String` | Current savings product ID |
| `computedTargetAmount` | `BigDecimal` | Derived target amount for the member |
| `isTupProject` | `Boolean` | Whether project is TUP |
| `addressesIdentical` | `Boolean` | Whether present and permanent addresses match |

`MemberCategoryOption` record:

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Category ID |
| `categoryName` | `String` | Category name |
| `ageFrom` | `Integer` | Min age |
| `ageTo` | `Integer` | Max age |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Edit Form Loading"

---

### Q-DDD-REQ-013 — Response DTO: MemberListItem [READ-MODEL]

The Member domain system shall define `MemberListItem` as an immutable Java `record` in `application/query/dto/`. Used for list/search results.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Aggregate identifier |
| `memberNo` | `String` | Member number |
| `memberName` | `String` | Full name |
| `branchName` | `String` | Branch name |
| `projectName` | `String` | Project name |
| `groupName` | `String` | Group name (nullable) |
| `memberCategoryName` | `String` | Category name |
| `memberStatusName` | `String` | Status name |
| `memberDomainStatus` | `Integer` | Active (1) / inactive (2) |
| `nationalId` | `String` | National ID (nullable) |
| `genderId` | `String` | Gender |
| `membershipDate` | `LocalDate` | Membership date |
| `applicationDate` | `LocalDate` | Application date |
| `assignedPoName` | `String` | PO name (nullable) |
| `isActive` | `Boolean` | Active status |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing"

---

### Q-DDD-REQ-014 — Response DTO: MemberValidationResponse [READ-MODEL]

The Member domain system shall define `MemberValidationResponse` as an immutable Java `record` in `application/query/dto/`. Used for real-time field validation results.

| Field | Type | Description |
|-------|------|-------------|
| `valid` | `Boolean` | True = no issues found |
| `validationType` | `String` | The type of check performed |
| `message` | `String` | Rejection message (nullable when valid = true) |
| `suspectedMembers` | `List<SuspectedMemberInfo>` | Suspected duplicates (nullable) |

`SuspectedMemberInfo` record (used for de-duplication results):

| Field | Type | Description |
|-------|------|-------------|
| `officeCode` | `String` | Suspected member's office code |
| `officeName` | `String` | Suspected member's office name |
| `groupCode` | `String` | Suspected member's group code (nullable) |
| `groupName` | `String` | Suspected member's group name (nullable) |
| `memberNo` | `String` | Suspected member's number |
| `memberName` | `String` | Suspected member's name |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Validation Rules"
> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Sub-Validators — Identity Uniqueness Check"

---

### Q-DDD-REQ-015 — Query Request DTOs (Presentation) [READ-MODEL]

The Member domain system shall define the following immutable Java `record` Query Request DTOs in `presentation/controller/dto/`:

**MemberGetRequest** — maps HTTP path/request parameters for `GetMemberByIdQuery`:
- `key: String @PathVariable @NotBlank` — branchInfoId scope key
- `id: String @PathVariable @NotBlank` — member ID

**MemberEditRequest** — maps HTTP path/request parameters for `EditMemberQuery`:
- `id: String @PathVariable @NotBlank` — member ID

**MemberSearchRequest** — maps HTTP query parameters for `SearchMembersQuery`:
- `key: String @PathVariable @NotBlank` — branchInfoId scope
- `projectInfoId: String @RequestParam(required=false)` — project filter
- `groupInfoId: String @RequestParam(required=false)` — group filter
- `memberStatusId: String @RequestParam(required=false)` — status filter
- `memberDomainStatus: Integer @RequestParam(required=false)` — 1/2 filter
- `memberName: String @RequestParam(required=false)` — name search
- `memberNo: String @RequestParam(required=false)` — member number search
- `nationalId: String @RequestParam(required=false)` — NID search
- `assignedPoId: String @RequestParam(required=false)` — PO filter
- `memberClassificationId: String @RequestParam(required=false)` — category filter
- `dateCreatedFrom: LocalDate @RequestParam(required=false)` — date range start
- `dateCreatedTo: LocalDate @RequestParam(required=false)` — date range end
- `isAsyncFeed: Boolean @RequestParam(defaultValue="false")` — async data feed flag
- `page: Integer @RequestParam(defaultValue="0")` — page number
- `size: Integer @RequestParam(defaultValue="20")` — page size

**MemberValidationRequest** — maps HTTP query parameters for `ValidateMemberQuery`:
- `validationType: String @RequestParam @NotBlank`
- `fieldValue: String @RequestParam @NotBlank`
- `memberId: String @RequestParam(required=false)` — exclude current member on update
- `memberClassificationId: String @RequestParam(required=false)`
- `dateOfBirth: LocalDate @RequestParam(required=false)`
- `hasCurrentLoan: Boolean @RequestParam(required=false)`
- `hasActiveInsurance: Boolean @RequestParam(required=false)`
- `currentInsuranceAgeGroup: String @RequestParam(required=false)`

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing", "Module: Member Validation Rules"

---

### Q-DDD-REQ-016 — Read Mapper: MemberReadMapper [READ-MODEL]

The Member domain system shall implement `MemberReadMapper` in `application/query/mapper/` as a static utility class. It shall provide:
- `toDetailResponse(MemberReadDocument)` → `MemberDetailResponse`
- `toEditResponse(MemberReadDocument, List<MemberCategoryOption>, String, String, SavingsAccount, Boolean, Boolean)` → `MemberEditResponse`
- `toListItem(MemberReadDocument)` → `MemberListItem`
- `toValidationResponse(boolean, String, List<SuspectedMemberInfo>)` → `MemberValidationResponse`

> 📎 [INFERRED] — required by read-model pattern

---

## Query Presentation Layer

### Q-DDD-REQ-017 — Query Controller: MemberQueryController [READ-MODEL]

The Member domain system shall implement `MemberQueryController` in `presentation/controller/`, extending `BaseApiController`. It shall inject only `QueryBus` (or equivalent read dispatcher).

For each query endpoint:
1. Read `trace_id` from `@RequestAttribute(MdcConstants.TRACE_ID)`.
2. Call `MDC.put("trace_id", trace_id)`.
3. Map the HTTP request to a Query object.
4. Dispatch to the appropriate Query Handler.
5. Return `ResponseEntity<ApiResponse<{ResponseDTO}>>` with `HttpStatus.OK` (200).

**Endpoints:**

| Method | Path | Query | Response DTO |
|--------|------|-------|-------------|
| `GET` | `/api/members/{key}/{id}` | `GetMemberByIdQuery` | `MemberDetailResponse` |
| `GET` | `/api/members/{id}/edit` | `EditMemberQuery` | `MemberEditResponse` |
| `GET` | `/api/members/{key}` | `SearchMembersQuery` | `Page<MemberListItem>` or `MemberListScreenResponse` |
| `GET` | `/api/members/validate` | `ValidateMemberQuery` | `MemberValidationResponse` |
| `GET` | `/api/members/{key}/categories` | (inline handler) | `List<MemberCategoryOption>` — categories for project |
| `GET` | `/api/members/{key}/savings-products` | (inline handler) | `List<SavingsProductOption>` — products for collection frequency |
| `GET` | `/api/members/passbook-check` | (inline handler) | `MemberPassbookAvailabilityResponse` — "Available" or "Not Available" |
| `GET` | `/api/members/min-target` | (inline handler) | `BigDecimal` — minimum target amount for project |

**Additional query operations (from EARS listing):**

When an operator requests the member categories for a project, the system shall return the categories permitted for that project excluding categories excluded for the operator's office, sorted by category name.

When an operator requests the savings products for a member, the system shall return the savings products for the group's collection frequency when a group is selected and otherwise for the monthly collection frequency.

When an operator checks passbook availability, the system shall return "Available" when the passbook number is unused and "Not Available" when it is in use, and shall return nothing when an empty passbook number is supplied.

When an operator requests the minimum target amount, the system shall compute the minimum savings target for the member's project, and where that value is unavailable derive it from the savings-product policy's installment calculation.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Viewing and Listing"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Request Handling"

---

## Read Infrastructure Layer

### Q-DDD-REQ-018 — Read Repository: MemberReadRepository [READ-MODEL]

The Member domain system shall define `MemberReadRepository` in `infrastructure/persistence/read/repository/`, extending `MongoRepository<MemberReadDocument, String>`.

**Required finder methods:**

| Method signature | Query | Used by |
|-----------------|-------|---------|
| `Optional<MemberReadDocument> findByIdAndIsActiveTrue(String id)` | `{ _id: id, is_active: true }` | `GetMemberByIdQueryHandler` |
| `Optional<MemberReadDocument> findById(String id)` | `{ _id: id }` | `EditMemberQueryHandler`, `MemberProjectionHandler` |
| `Page<MemberReadDocument> findAllByBranchInfoId(String branchId, Pageable p)` | `{ branch_info_id: branchId }` | `SearchMembersQueryHandler` |
| `Page<MemberReadDocument> findAllByBranchInfoIdAndProjectInfoId(String, String, Pageable)` | `{ branch_info_id: ?, project_info_id: ? }` | `SearchMembersQueryHandler` |
| `Page<MemberReadDocument> findAllByBranchInfoIdAndGroupInfoId(String, String, Pageable)` | `{ branch_info_id: ?, group_info_id: ? }` | `SearchMembersQueryHandler` |
| `Page<MemberReadDocument> findAllByBranchInfoIdAndMemberDomainStatus(String, Integer, Pageable)` | `{ branch_info_id: ?, member_domain_status: ? }` | `SearchMembersQueryHandler` |
| `Optional<MemberReadDocument> findByBranchInfoIdAndMemberNo(String, String)` | `{ branch_info_id: ?, member_no: ? }` | `SearchMembersQueryHandler` |
| `Optional<MemberReadDocument> findByNationalIdAndIdNot(String nationalId, String excludeId)` | `{ national_id: ?, _id: { $ne: excludeId } }` | `ValidateMemberQueryHandler` |
| `Optional<MemberReadDocument> findBySmartCardIdAndIdNot(String smartCardId, String excludeId)` | `{ smart_card_id: ?, _id: { $ne: excludeId } }` | `ValidateMemberQueryHandler` |
| `Optional<MemberReadDocument> findByBikashWalletNoAndIdNot(String walletNo, String excludeId)` | `{ bikash_wallet_no: ?, _id: { $ne: excludeId } }` | `ValidateMemberQueryHandler` |
| `Optional<MemberReadDocument> findByRocketWalletNoAndIdNot(String walletNo, String excludeId)` | `{ rocket_wallet_no: ?, _id: { $ne: excludeId } }` | `ValidateMemberQueryHandler` |
| `List<MemberReadDocument> findAllByAssignedPoIdAndBranchInfoId(String, String)` | `{ assigned_po_id: ?, branch_info_id: ? }` | `SearchMembersQueryHandler` (PO reassignment listing) |

**Repository query derivation pseudocode (representative):**

```pseudocode
// Spring Data MongoDB derives queries from method names at startup

findByIdAndIsActiveTrue(id):
  RETURN db.member_read.findOne({ _id: id, is_active: true })
  // Returns Optional.empty() if not found

findAllByBranchInfoId(branchId, pageable):
  RETURN db.member_read.find({ branch_info_id: branchId })
           .sort({ date_created: -1 })
           .skip(pageable.offset).limit(pageable.pageSize)
```

> 📎 [INFERRED] — required by read-model pattern

---

### Q-DDD-REQ-019 — Read-Side Bootstrap / Configuration [READ-MODEL]

The Member domain system shall define a read-side Spring Boot application class `MemberQueryApplication` annotated `@SpringBootApplication` in the query-side module, scanning all required beans including `MemberProjectionHandler`, `MemberReadRepository`, and query handlers.

The read-side application shall connect to the same MongoDB instance as the command side, using the same database but separate collections (`member_read` vs. `members`).

> 📎 [INFERRED]

---

## Cross-Cutting Requirements

### Q-DDD-REQ-020 — Audit and Record Lifecycle [UNCHANGED]

*(Identical to DDD-REQ-082 in `Member-Command-DDD-EARS.md`. Reproduced here for standalone query-side reference.)*

The Member domain system shall record, on every record it first persists, the identifier of the acting operator as the creating user and the identifier of the acting operator as the last-updating user, together with the system creation and last-update timestamps.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Cross-Cutting Requirements — Audit and Record Lifecycle" [UNCHANGED]
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Audit and Record Lifecycle" [UNCHANGED]

---

### Q-DDD-REQ-021 — Authentication and Authorisation [UNCHANGED]

*(Identical to DDD-REQ-083 in `Member-Command-DDD-EARS.md`.)*

Where the requesting session is not authenticated, the Member domain system shall deny access to every query operation and redirect the actor to the login flow.

The Member domain system shall enforce database-driven feature-and-action access rules for all query operations.

When a member profile or member detail is opened for viewing or editing by an internal user, the Member domain system shall permit access only when the member's project is among the user's permitted projects (or project + branch office among permitted), or the user holds the microfinance programme-administrator role; otherwise the system shall reject the request with the message "You do not have authority to view this member. For further information please contact with you system administrator."

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Authentication and Authorisation" [UNCHANGED]

---

### Q-DDD-REQ-022 — Error Response Format (Query-Side) [UNCHANGED]

The Member domain system shall return query-side error outcomes as a structured message payload containing a result type (success or error), a message title, and a message body.

| Error condition | Message text |
|----------------|-------------|
| Member not found (view/edit) | "Member not found" |
| Access denied (view/edit) | "You do not have authority to view this member. For further information please contact with you system administrator." |
| Real-time NID duplicate | "Same National ID already exists for another member." |
| Real-time Smart Card duplicate | "Same Smart Card ID already exists for another member." |
| Real-time wallet duplicate | "Same Wallet Number already exists for another member" |
| Real-time guarantor NID duplicate | "Same National Id already exists for another guarantor" |
| Real-time passport duplicate | "Same Passport Number already exists for another member." |
| Real-time birth cert duplicate | "Same Birth Certificate already exists for another member." |
| Real-time driving licence duplicate | "Same Driving License already exists for another member." |
| Member age > 70 with current loan | "Member age can not be greater than 70" |
| DOB change alters insurance age group | "Date of birth can not be changed, member has active insurance" |
| DeDupe API error (real-time) | "DeDupe API Error! \n" + error detail |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Error Response Format" [UNCHANGED]
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Validation Rules"

---

## Out of Scope

| # | Capability | Source | Reason |
|---|-----------|--------|--------|
| 1 | Event Sourcing snapshotting | [INFERRED] | State database used; event replay not required |
| 2 | Passbook availability check against command-side aggregate store | MemberManagement § "Member Viewing and Listing" | Query uses `MemberReadDocument.passbookNo`; availability check uses read model only |
| 3 | Insurance service external integration (for full insurance validation) | MemberManagement § "Module: Cross-Plugin and External Integrations" | External service call; out of read model scope; handled by `ValidateMemberQueryHandler` as a pass-through |
| 4 | Full family composition read view | MemberManagement § "Module: Member Management — Member Edit Form Loading" | FamilyInfo embedded in read document via projection; full family-only view is a sub-view, not a separate query |

---

## Open Questions

| ID | Source | Question | Status |
|----|--------|----------|--------|
| Q-1 | MemberManagement § "Module: Member Management — Member Viewing and Listing" | **Nominee/guarantor/asset projection:** The `MemberFamilySavedEvent` carries only `nomineeCount` and `hasGuarantor` (summary fields). Should the event carry full nominee/guarantor/asset payloads for projection, or should the Projection Handler load from the command store on family events? Recommend: carry full payloads in the event to keep the projection handler self-contained. | OPEN |
| Q-2 | MemberManagement § "Module: Member Management — Member Edit Form Loading" | **Permitted categories "excluded for operator's office":** The EARS mentions excluding categories excluded for the operator's office, but the exclusion rules are owned by the `MemberClassification` domain. The snapshot schema (DDD-REQ-045f in Command file) does not include office-exclusion flags. What field governs this exclusion? | OPEN |
| Q-3 | MemberManagement § "Module: Member Management — Member Viewing and Listing" | **`isAsyncFeed` flag semantics:** The EARS says "return members as a data feed for asynchronous list requests and otherwise present the authorised projects and visible offices." Should this be two separate query endpoints rather than one conditional endpoint? | OPEN |
| Q-4 | MemberManagement § "Module: Member Validation Rules" | **Insurance service integration for query-side:** `ValidateMemberQueryHandler` must call an external insurance service for age validation. The insurance service endpoint and contract are not specified in the EARS. These need to be defined by the insurance domain team. | OPEN |

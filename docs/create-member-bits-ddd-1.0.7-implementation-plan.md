# CreateMember Implementation Plan — bits-ddd 1.0.7

## Decision Summary

This `member-command` codebase targets `bits-ddd-lib` version `1.0.7`, published locally from commit `5563fb6`.

Confirmed choices:

1. Java version: 25
2. Spring Boot version: 4.0.1
3. Root package: `com.bits.member`
4. Dependency style: standalone Gradle project depending on local Maven artifacts from `bits-ddd-lib`
5. Scope: CreateMember vertical-slice skeleton only
6. Commands/events stay inside `member-command` for now
7. Source-data model classes are generated inside this codebase
8. No test files generated yet

---

## bits-ddd 1.0.7 Compatibility Rules

Use the 1.0.7 APIs, not the newer 1.2.0 APIs.

### Use these imports

- `com.bits.ddd.domain.aggregate.AggregateRoot`
- `com.bits.ddd.domain.specification.context.ValidationContext`
- `com.bits.ddd.domain.specification.rules.Specification`
- `com.bits.ddd.shared.localization.LocalizedMessage`
- `com.bits.ddd.shared.messaging.CommandMessage`
- `com.bits.ddd.shared.messaging.DomainEventMessage`
- `com.bits.ddd.shared.messaging.FailureMessage`
- `com.bits.ddd.application.handler.CommandHandler`
- `com.bits.ddd.infra.core.bus.CommandBus`
- `com.bits.ddd.application.service.MessageProcessor`
- `com.bits.ddd.application.service.SourceDataService`
- `com.bits.ddd.application.dto.SourceData`
- `com.bits.ddd.infra.persistence.service.DomainPersistenceService`
- `com.bits.ddd.shared.domain.enums.DomainStatus`
- `com.bits.ddd.annotation.RegisterCommandHandler`
- `com.bits.ddd.annotation.MongoDomainRepo` when generated repositories are needed

### Do not use these newer 1.2.0 APIs

- `com.bits.ddd.shared.domain.value.DomainStatus`
- `com.bits.ddd.shared.domain.value.DefaultDomainStatuses`
- `com.bits.ddd.domain.sourcedata.SourceData`
- `com.bits.ddd.application.service.SourceDataCoordinator`
- `com.bits.ddd.application.service.SourceDataContext`
- `com.bits.ddd.annotation.MongoAggregateRepo`
- `com.bits.ddd.annotation.MongoSourceData`

---

## Generated Skeleton Scope

Generated layers:

```text
presentation/controller
presentation/controller/dto
application/command
application/commandhandler
application/dto
application/dto/sourcedata
application/mapper
application/service
domain/aggregate
domain/entity
domain/valueobject
domain/param
domain/specification/context
domain/specification/rules
domain/event
domain/exception
domain/mapper
domain/enums
infrastructure/persistence/repository
infrastructure/messaging/config
infrastructure/config
```

No local framework primitives were generated. `AggregateRoot`, `Specification`, `LocalizedMessage`, etc. come from bits-ddd.

---

## Current Generated CreateMember Flow

```text
POST /api/members
  -> CreateMemberRequest
  -> MemberCommandMapper.toCreateCommand(...)
  -> CommandBus.handle(command)
  -> CreateMemberCommandHandler.handle(command)
  -> MemberDataMapper.toCreationData(...)
  -> Member.create(creationData)
  -> DomainPersistenceService.persist(member)
  -> MessageProcessor.publish(member.getEvents())
```

The classes are skeletons. Validation/business rules are intentionally TODOs.

---

## Next Implementation Order

1. Confirm exact field list and message keys with lead.
2. Implement `Member.create(...)` properly:
   - field population
   - full name building
   - business date membership date
   - active status setup
   - initial status history
   - nominee share redistribution
   - `MemberCreatedEvent`
3. Implement `MemberValidationContext` fully.
4. Implement the 8 CreateMember specifications:
   - `FieldPresenceAndFormatSpecification`
   - `IdentityDocumentSpecification`
   - `BusinessDayAndBranchSpecification`
   - `MemberCategoryAndGroupPolicySpecification`
   - `SavingsProductSpecification`
   - `DeduplicationSpecification`
   - `NomineeAndGuarantorSpecification`
   - `PersonalDataConsistencySpecification`
5. Wire concrete source-data service.
6. Wire member number generation.
7. Wire lock service.
8. Wire dedupe adapter.
9. Wire persistence/repository generation.
10. Add domain and handler tests.

---

## Important Open Items

The generated code is a compile-oriented skeleton. These are not finalized yet:

- exact member number generation
- real lock failure behavior
- source-data repository implementations
- real dedupe service contract
- exact queue/exchange names
- persistence bean generation strategy for `DomainPersistenceService<Member, String>`
- exact response envelope expected by the team
- full validation message catalogue

---

## Build Notes

`bits-ddd-lib` 1.0.7 was published to Maven local before generating this codebase.

The project uses:

```groovy
implementation 'com.bits.ddd:command-core:1.0.7'
annotationProcessor 'com.bits.ddd:annotation-processor:1.0.7'
```

Build command:

```bash
./gradlew compileJava
```

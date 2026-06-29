package com.bits.member.presentation.controller;

import com.bits.ddd.infra.core.bus.CommandBus;
import com.bits.member.application.command.CreateMemberCommand;
import com.bits.member.application.mapper.MemberCommandMapper;
import com.bits.member.presentation.controller.dto.CreateMemberRequestDto;
import com.bits.member.presentation.controller.dto.SaveMemberFamilyRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberCommandController {

    private final CommandBus commandBus;

    public MemberCommandController(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMember(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @Valid @RequestBody CreateMemberRequestDto createMemberRequestDto) {
        CreateMemberCommand command = MemberCommandMapper.toCreateCommand(tracerId, createMemberRequestDto);
        commandBus.handle(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("status", "ACCEPTED", "traceId", tracerId));
    }

    @PostMapping("/{id}/family")
    public ResponseEntity<Map<String, Object>> saveMemberFamily(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @PathVariable("id") String id,
            @Valid @RequestBody SaveMemberFamilyRequest saveMemberFamilyRequest) {
        commandBus.handle(MemberCommandMapper.toSaveMemberFamilyCommand(tracerId, id, saveMemberFamilyRequest));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("status", "ACCEPTED", "traceId", tracerId));
    }
}

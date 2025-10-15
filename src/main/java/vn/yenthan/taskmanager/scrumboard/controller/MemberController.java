package vn.yenthan.taskmanager.scrumboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yenthan.taskmanager.core.entity.SuccessResponse;
import vn.yenthan.taskmanager.core.util.ResponseUtil;
import vn.yenthan.taskmanager.scrumboard.dto.response.MemberDto;
import vn.yenthan.taskmanager.scrumboard.service.MemberService;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.List;

@RestController
@RequestMapping("/api/scrumboard/member")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Member Controller", description = "API endpoints for member management")
public class MemberController {

    private final MemberService memberService;
    private final TranslateMessage translateMessage;

    @GetMapping("/{boardId}")
    @Operation(summary = "Get board members", description = "Retrieve all members of a specific board")
    public SuccessResponse<List<MemberDto>> getBoardMembers(
            @Parameter(description = "Board ID") @PathVariable Long boardId) {
        log.info("GET /api/scrumboard/member/{} - Fetching members for board", boardId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.MEMBER_GET_SUCCESS),
                memberService.getBoardMembers(boardId));
    }

    @GetMapping("/{boardId}/{userId}")
    @Operation(summary = "Get specific board member", description = "Retrieve a specific member of a board")
    public SuccessResponse<MemberDto> getBoardMember(
            @Parameter(description = "Board ID") @PathVariable Long boardId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("GET /api/scrumboard/member/{}/{} - Fetching member for board", boardId, userId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.MEMBER_GET_SUCCESS),
                memberService.getBoardMember(boardId, userId));
    }

    @PostMapping("/{boardId}/{userId}")
    @Operation(summary = "Add member to board", description = "Add a user as a member to a board")
    public SuccessResponse<String> addMemberToBoard(
            @Parameter(description = "Board ID") @PathVariable Long boardId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("POST /api/scrumboard/member/{}/{} - Adding member to board", boardId, userId);
        memberService.addMemberToBoard(boardId, userId);
        return ResponseUtil.ok(HttpStatus.CREATED.value(),
                translateMessage.translate(MessageKeys.MEMBER_ADD_SUCCESS));
    }

    @DeleteMapping("/{boardId}/{userId}")
    @Operation(summary = "Remove member from board", description = "Remove a user from a board")
    public SuccessResponse<String> removeMemberFromBoard(
            @Parameter(description = "Board ID") @PathVariable Long boardId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("DELETE /api/scrumboard/member/{}/{} - Removing member from board", boardId, userId);
        memberService.removeMemberFromBoard(boardId, userId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.MEMBER_REMOVE_SUCCESS));
    }

    @PutMapping("/{boardId}/{userId}/role")
    @Operation(summary = "Update member role", description = "Update the role of a member in a board")
    public SuccessResponse<String> updateMemberRole(
            @Parameter(description = "Board ID") @PathVariable Long boardId,
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "New role") @RequestParam String role) {
        log.info("PUT /api/scrumboard/member/{}/{}/role - Updating member role to {}", boardId, userId, role);
        memberService.updateMemberRole(boardId, userId, role);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.MEMBER_UPDATE_ROLE_SUCCESS));
    }
}

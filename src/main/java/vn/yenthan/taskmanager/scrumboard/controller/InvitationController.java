package vn.yenthan.taskmanager.scrumboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.core.util.ResponseUtil;
import vn.yenthan.taskmanager.core.entity.SuccessResponse;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;
import vn.yenthan.taskmanager.scrumboard.dto.InviteRequestDto;
import vn.yenthan.taskmanager.scrumboard.service.InvitationService;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/scrumboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Invitation Controller", description = "API endpoints for board invitations")
public class InvitationController {

    private final InvitationService invitationService;
    private final TranslateMessage translateMessage;
    private final UserRepository userRepository;

    @PostMapping("/boards/{boardId}/invite")
    @Operation(summary = "Invite user to board", description = "Invite a user to board via email. User will be assigned MEMBER role automatically.")
    public SuccessResponse<Map<String, Object>> invite(
            @Parameter(description = "Board ID") @PathVariable Long boardId,
            @Valid @RequestBody InviteRequestDto request,
            Principal principal) {
        Long currentUserId = extractUserIdFromPrincipal(principal);
        Map<String, Object> res = invitationService.invite(boardId, request, currentUserId);
        int status = (int) res.getOrDefault("status", HttpStatus.CREATED.value());
        return ResponseUtil.ok(status, translateMessage.translate(MessageKeys.MEMBER_ADD_SUCCESS), res);
    }

    @GetMapping("/invitations/accept")
    public SuccessResponse<Map<String, Object>> accept(@RequestParam("token") String token,
                                                       Principal principal) {
        Long currentUserId = extractUserIdFromPrincipal(principal); // may be null if unauthenticated
        Map<String, Object> res = invitationService.accept(token, currentUserId);
        int status = (int) res.getOrDefault("status", HttpStatus.OK.value());
        return ResponseUtil.ok(status, translateMessage.translate(MessageKeys.MEMBER_ADD_SUCCESS), res);
    }

    @PostMapping("/invitations/complete")
    public SuccessResponse<Map<String, Object>> complete(@RequestBody Map<String, Object> body) {
        String token = String.valueOf(body.get("token"));
        Long userId = Long.parseLong(String.valueOf(body.get("userId")));
        Map<String, Object> res = invitationService.complete(token, userId);
        int status = (int) res.getOrDefault("status", HttpStatus.OK.value());
        return ResponseUtil.ok(status, translateMessage.translate(MessageKeys.MEMBER_ADD_SUCCESS), res);
    }


    private Long extractUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        try {
            String username = principal.getName();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username))
                    .getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid user authentication: " + e.getMessage());
        }
    }
}



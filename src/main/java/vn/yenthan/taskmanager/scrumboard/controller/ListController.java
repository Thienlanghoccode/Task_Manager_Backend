package vn.yenthan.taskmanager.scrumboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;
import vn.yenthan.taskmanager.core.entity.SuccessResponse;
import vn.yenthan.taskmanager.core.util.ResponseUtil;
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateListRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateListRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.CardListDto;
import vn.yenthan.taskmanager.scrumboard.repository.ListRepository;
import vn.yenthan.taskmanager.scrumboard.security.AuthzService;
import vn.yenthan.taskmanager.scrumboard.service.ListService;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.util.MessageKeys;
import vn.yenthan.taskmanager.websocket.service.WebSocketBroadcastService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/scrumboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "List Controller", description = "API endpoints for list management")
public class ListController {

    private final ListService listService;
    private final TranslateMessage translateMessage;
    private final AuthzService authzService;
    private final UserRepository userRepository;
    private final ListRepository listRepository;
    private final WebSocketBroadcastService webSocketBroadcastService;

    @GetMapping("/list/{boardId}")
    @Operation(summary = "Get lists by board ID", description = "Retrieve all lists for a specific board")
    public SuccessResponse<List<CardListDto>> getListsByBoardId(
            @Parameter(description = "Board ID") @PathVariable Long boardId) {
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_GET_SUCCESS),
                listService.getListsByBoardId(boardId));
    }

    @GetMapping("/list/detail/{id}")
    @Operation(summary = "Get list by ID", description = "Retrieve a specific list with all details")
    public SuccessResponse<CardListDto> getListById(
            @Parameter(description = "List ID") @PathVariable Long id) {
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_GET_SUCCESS),
                listService.getListById(id));
    }

    @PostMapping("/add/list")
    @Operation(summary = "Create new list", description = "Create a new list in a board")
    public SuccessResponse<CardListDto> createList(
            @Valid @RequestBody CreateListRequest request,
            Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);
        
        // ✅ Chỉ OWNER mới được tạo list
        if (!authzService.canCreateList(userId, request.getBoardId())) {
            throw new AccessDeniedException("Only board owner can create lists");
        }
        
        CardListDto createdList = listService.createList(request);
        
        // Broadcast WebSocket message
        log.info("About to broadcast list created for board: {}, list: {}", request.getBoardId(), createdList.getId());
        webSocketBroadcastService.broadcastListCreated(request.getBoardId(), createdList.getId(), createdList);
        log.info("Broadcast completed for list created");
        
        return ResponseUtil.ok(HttpStatus.CREATED.value(),
                translateMessage.translate(MessageKeys.LIST_CREATE_SUCCESS),
                createdList);
    }

    @PutMapping("/edit/list")
    @Operation(summary = "Update list", description = "Update an existing list")
    public SuccessResponse<CardListDto> updateList(
            @Valid @RequestBody UpdateListRequest request,
            Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);
        
        // Lấy boardId từ list
        Long boardId = listRepository.findById(request.getId())
                .map(list -> list.getBoard().getId())
                .orElseThrow(() -> new IllegalArgumentException("List not found"));
        
        // ✅ Chỉ OWNER mới được sửa list
        if (!authzService.canEditList(userId, boardId)) {
            throw new AccessDeniedException("Only board owner can edit lists");
        }
        
        CardListDto updatedList = listService.updateList(request);
        
        // Broadcast WebSocket message
        webSocketBroadcastService.broadcastListUpdated(boardId, request.getId(), updatedList);
        
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_UPDATE_SUCCESS),
                updatedList);
    }

    @DeleteMapping("/delete/list")
    @Operation(summary = "Delete list", description = "Delete a list by ID")
    public SuccessResponse<String> deleteList(
            @Parameter(description = "List ID") @RequestParam Long id,
            Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);
        
        // Lấy boardId từ list
        Long boardId = listRepository.findById(id)
                .map(list -> list.getBoard().getId())
                .orElseThrow(() -> new IllegalArgumentException("List not found"));
        
        // ✅ Chỉ OWNER mới được xóa list
        if (!authzService.canDeleteList(userId, boardId)) {
            throw new AccessDeniedException("Only board owner can delete lists");
        }
        
        // Broadcast WebSocket message trước khi xóa
        webSocketBroadcastService.broadcastListDeleted(boardId, id);
        
        listService.deleteList(id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_DELETE_SUCCESS));
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

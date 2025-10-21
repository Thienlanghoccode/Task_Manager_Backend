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
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateCardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateCardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateCardCategoryRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.CardDto;
import vn.yenthan.taskmanager.scrumboard.repository.CardRepository;
import vn.yenthan.taskmanager.scrumboard.repository.ListRepository;
import vn.yenthan.taskmanager.scrumboard.security.AuthzService;
import vn.yenthan.taskmanager.scrumboard.service.CardService;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.util.MessageKeys;
import vn.yenthan.taskmanager.websocket.service.WebSocketBroadcastService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/scrumboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Card Controller", description = "API endpoints for card management")
public class CardController {

    private final CardService cardService;
    private final TranslateMessage translateMessage;
    private final AuthzService authzService;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final WebSocketBroadcastService webSocketBroadcastService;

    @GetMapping("/card/{listId}")
    @Operation(summary = "Get cards by list ID", description = "Retrieve all cards for a specific list")
    public SuccessResponse<List<CardDto>> getCardsByListId(
            @Parameter(description = "List ID") @PathVariable Long listId) {
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_GET_SUCCESS),
                cardService.getCardsByListId(listId));
    }

    @GetMapping("/card/detail/{id}")
    @Operation(summary = "Get card by ID", description = "Retrieve a specific card with all details")
    public SuccessResponse<CardDto> getCardById(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_GET_SUCCESS),
                cardService.getCardById(id));
    }

    @PostMapping("/add/card")
    @Operation(summary = "Create new card", description = "Create a new card in a list")
    public SuccessResponse<CardDto> createCard(
            @Valid @RequestBody CreateCardRequest request,
            Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);
        
        // Lấy boardId từ list
        Long boardId = listRepository.findById(request.getLaneId())
                .map(list -> list.getBoard().getId())
                .orElseThrow(() -> new IllegalArgumentException("List not found"));
        
        // ✅ Chỉ OWNER mới được tạo card
        if (!authzService.canCreateCard(userId, boardId)) {
            throw new AccessDeniedException("Only board owner can create cards");
        }
        
        CardDto createdCard = cardService.createCard(request);
        
        // Broadcast WebSocket message
        webSocketBroadcastService.broadcastCardCreated(boardId, createdCard.getId(), createdCard);
        
        return ResponseUtil.ok(HttpStatus.CREATED.value(),
                translateMessage.translate(MessageKeys.CARD_CREATE_SUCCESS),
                createdCard);
    }

    @PutMapping("/edit/card")
    @Operation(summary = "Update card", description = "Update an existing card")
    public SuccessResponse<CardDto> updateCard(
            @Valid @RequestBody UpdateCardRequest request,
            Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);
        
        // Lấy boardId từ card
        Long boardId = cardRepository.findByIdWithListAndBoard(request.getId())
                .map(card -> card.getList().getBoard().getId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        
        // ✅ MEMBER có thể update card (theo yêu cầu)
        if (!authzService.canUpdateCard(userId, boardId)) {
            throw new AccessDeniedException("Only board members can update cards");
        }
        
        CardDto updatedCard = cardService.updateCard(request);
        
        // Broadcast WebSocket message
        webSocketBroadcastService.broadcastCardUpdated(boardId, request.getId(), updatedCard);
        
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_UPDATE_SUCCESS),
                updatedCard);
    }

    @PutMapping("/cards/update/category")
    @Operation(summary = "Update card category", description = "Move a card to a different list")
    public SuccessResponse<CardDto> updateCardCategory(
            @Valid @RequestBody UpdateCardCategoryRequest request,
            Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);
        
        // Lấy boardId từ card
        Long boardId = cardRepository.findByIdWithListAndBoard(request.getCardId())
                .map(card -> card.getList().getBoard().getId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        
        // ✅ MEMBER có thể category card (theo yêu cầu)
        if (!authzService.canCategoryCard(userId, boardId)) {
            throw new AccessDeniedException("Only board members can move cards");
        }
        
        // Lấy fromListId trước khi update
        Long fromListId = cardRepository.findById(request.getCardId())
                .map(card -> card.getList().getId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        
        CardDto updatedCard = cardService.updateCardCategory(request);
        
        // Broadcast WebSocket message
        webSocketBroadcastService.broadcastCardMoved(boardId, request.getCardId(), fromListId, request.getLaneId(), updatedCard);
        
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_MOVE_SUCCESS),
                updatedCard);
    }

    @DeleteMapping("/delete/card")
    @Operation(summary = "Delete card", description = "Delete a card by ID")
    public SuccessResponse<String> deleteCard(
            @Parameter(description = "Card ID") @RequestParam Long id,
            Principal principal) {
        Long userId = extractUserIdFromPrincipal(principal);
        
        // Lấy boardId từ card
        Long boardId = cardRepository.findByIdWithListAndBoard(id)
                .map(card -> card.getList().getBoard().getId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        
        // ✅ Chỉ OWNER mới được xóa card
        if (!authzService.canDeleteCard(userId, boardId)) {
            throw new AccessDeniedException("Only board owner can delete cards");
        }
        
        // Broadcast WebSocket message trước khi xóa
        webSocketBroadcastService.broadcastCardDeleted(boardId, id);
        
        cardService.deleteCard(id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_DELETE_SUCCESS));
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

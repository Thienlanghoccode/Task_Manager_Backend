package vn.yenthan.taskmanager.scrumboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yenthan.taskmanager.core.entity.SuccessResponse;
import vn.yenthan.taskmanager.core.util.ResponseUtil;
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateCardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateCardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateCardCategoryRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.CardDto;
import vn.yenthan.taskmanager.scrumboard.service.CardService;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.List;

@RestController
@RequestMapping("/api/scrumboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Card Controller", description = "API endpoints for card management")
public class CardController {

    private final CardService cardService;
    private final TranslateMessage translateMessage;

    @GetMapping("/card/{listId}")
    @Operation(summary = "Get cards by list ID", description = "Retrieve all cards for a specific list")
    public SuccessResponse<List<CardDto>> getCardsByListId(
            @Parameter(description = "List ID") @PathVariable Long listId) {
        log.info("GET /api/scrumboard/card/{} - Fetching cards for list", listId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_GET_SUCCESS),
                cardService.getCardsByListId(listId));
    }

    @GetMapping("/card/detail/{id}")
    @Operation(summary = "Get card by ID", description = "Retrieve a specific card with all details")
    public SuccessResponse<CardDto> getCardById(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        log.info("GET /api/scrumboard/card/detail/{} - Fetching card by ID", id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_GET_SUCCESS),
                cardService.getCardById(id));
    }

    @PostMapping("/add/card")
    @Operation(summary = "Create new card", description = "Create a new card in a list")
    public SuccessResponse<CardDto> createCard(
            @Valid @RequestBody CreateCardRequest request) {
        log.info("POST /api/scrumboard/add/card - Creating new card: {}", request.getTitle());
        return ResponseUtil.ok(HttpStatus.CREATED.value(),
                translateMessage.translate(MessageKeys.CARD_CREATE_SUCCESS),
                cardService.createCard(request));
    }

    @PutMapping("/edit/card")
    @Operation(summary = "Update card", description = "Update an existing card")
    public SuccessResponse<CardDto> updateCard(
            @Valid @RequestBody UpdateCardRequest request) {
        log.info("PUT /api/scrumboard/edit/card - Updating card: {}", request.getId());
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_UPDATE_SUCCESS),
                cardService.updateCard(request));
    }

    @PutMapping("/cards/update/category")
    @Operation(summary = "Update card category", description = "Move a card to a different list")
    public SuccessResponse<CardDto> updateCardCategory(
            @Valid @RequestBody UpdateCardCategoryRequest request) {
        log.info("PUT /api/cards/update/category - Moving card {} to list {}", 
                request.getCardId(), request.getLaneId());
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_MOVE_SUCCESS),
                cardService.updateCardCategory(request));
    }

    @DeleteMapping("/delete/card")
    @Operation(summary = "Delete card", description = "Delete a card by ID")
    public SuccessResponse<String> deleteCard(
            @Parameter(description = "Card ID") @RequestParam Long id) {
        log.info("DELETE /api/scrumboard/delete/card - Deleting card: {}", id);
        cardService.deleteCard(id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.CARD_DELETE_SUCCESS));
    }
}

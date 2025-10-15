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
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateListRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateListRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.CardListDto;
import vn.yenthan.taskmanager.scrumboard.service.ListService;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.List;

@RestController
@RequestMapping("/api/scrumboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "List Controller", description = "API endpoints for list management")
public class ListController {

    private final ListService listService;
    private final TranslateMessage translateMessage;

    @GetMapping("/list/{boardId}")
    @Operation(summary = "Get lists by board ID", description = "Retrieve all lists for a specific board")
    public SuccessResponse<List<CardListDto>> getListsByBoardId(
            @Parameter(description = "Board ID") @PathVariable Long boardId) {
        log.info("GET /api/scrumboard/list/{} - Fetching lists for board", boardId);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_GET_SUCCESS),
                listService.getListsByBoardId(boardId));
    }

    @GetMapping("/list/detail/{id}")
    @Operation(summary = "Get list by ID", description = "Retrieve a specific list with all details")
    public SuccessResponse<CardListDto> getListById(
            @Parameter(description = "List ID") @PathVariable Long id) {
        log.info("GET /api/scrumboard/list/detail/{} - Fetching list by ID", id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_GET_SUCCESS),
                listService.getListById(id));
    }

    @PostMapping("/add/list")
    @Operation(summary = "Create new list", description = "Create a new list in a board")
    public SuccessResponse<CardListDto> createList(
            @Valid @RequestBody CreateListRequest request) {
        log.info("POST /api/scrumboard/add/list - Creating new list: {} for board: {}", 
                request.getName(), request.getBoardId());
        return ResponseUtil.ok(HttpStatus.CREATED.value(),
                translateMessage.translate(MessageKeys.LIST_CREATE_SUCCESS),
                listService.createList(request));
    }

    @PutMapping("/edit/list")
    @Operation(summary = "Update list", description = "Update an existing list")
    public SuccessResponse<CardListDto> updateList(
            @Valid @RequestBody UpdateListRequest request) {
        log.info("PUT /api/scrumboard/edit/list - Updating list: {}", request.getId());
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_UPDATE_SUCCESS),
                listService.updateList(request));
    }

    @DeleteMapping("/delete/list")
    @Operation(summary = "Delete list", description = "Delete a list by ID")
    public SuccessResponse<String> deleteList(
            @Parameter(description = "List ID") @RequestParam Long id) {
        log.info("DELETE /api/scrumboard/delete/list - Deleting list: {}", id);
        listService.deleteList(id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.LIST_DELETE_SUCCESS));
    }
}

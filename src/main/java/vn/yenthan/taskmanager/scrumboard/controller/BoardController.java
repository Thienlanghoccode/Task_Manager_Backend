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
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateBoardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateBoardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.BoardDto;
import vn.yenthan.taskmanager.scrumboard.service.BoardService;
import vn.yenthan.taskmanager.scrumboard.repository.BoardRepository;
import vn.yenthan.taskmanager.core.component.TranslateMessage;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.*;

@RestController
@RequestMapping("/api/scrumboard/board")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Board Controller", description = "API endpoints for board management")
public class BoardController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final TranslateMessage translateMessage;

    @GetMapping("/list")
    @Operation(summary = "Get all boards", description = "Retrieve all boards with their lists and cards")
    public SuccessResponse<List<BoardDto>> getAllBoards() {
        log.info("GET /api/scrumboard/board/list - Fetching all boards");
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.BOARD_GET_SUCCESS),
                boardService.getAllBoards());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get board by ID", description = "Retrieve a specific board with all details")
    public SuccessResponse<BoardDto> getBoardById(
            @Parameter(description = "Board ID") @PathVariable Long id) {
        log.info("GET /api/scrumboard/board/{} - Fetching board by ID", id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.BOARD_GET_SUCCESS),
                boardService.getBoardById(id));
    }

    @PostMapping("/add/board")
    @Operation(summary = "Create new board", description = "Create a new board")
    public SuccessResponse<BoardDto> createBoard(
            @Valid @RequestBody CreateBoardRequest request) {
        log.info("POST /api/scrumboard/add/board - Creating new board: {}", request.getName());
        return ResponseUtil.ok(HttpStatus.CREATED.value(),
                translateMessage.translate(MessageKeys.BOARD_CREATE_SUCCESS),
                boardService.createBoard(request));
    }

    @PutMapping("/edit/board")
    @Operation(summary = "Update board", description = "Update an existing board")
    public SuccessResponse<BoardDto> updateBoard(
            @Valid @RequestBody UpdateBoardRequest request) {
        log.info("PUT /api/scrumboard/edit/board - Updating board: {}", request.getId());
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.BOARD_UPDATE_SUCCESS),
                boardService.updateBoard(request));
    }

    @DeleteMapping("/delete/board")
    @Operation(summary = "Delete board", description = "Delete a board by ID")
    public SuccessResponse<String> deleteBoard(
            @Parameter(description = "Board ID") @RequestParam Long id) {
        log.info("DELETE /api/scrumboard/delete/board - Deleting board: {}", id);
        boardService.deleteBoard(id);
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.BOARD_DELETE_SUCCESS));
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "Get board members", description = "Retrieve all members of a specific board")
    public SuccessResponse<List<Object>> getBoardMembers(
            @Parameter(description = "Board ID") @PathVariable Long id) {
        log.info("GET /api/scrumboard/board/{}/members - Fetching board members", id);
        // This would need to be implemented with MemberService
        return ResponseUtil.ok(HttpStatus.OK.value(),
                translateMessage.translate(MessageKeys.MEMBER_GET_SUCCESS),
                List.of());
    }

}

package vn.yenthan.taskmanager.scrumboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.core.util.EntityBase;
import vn.yenthan.taskmanager.scrumboard.dto.request.CreateBoardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.request.UpdateBoardRequest;
import vn.yenthan.taskmanager.scrumboard.dto.response.BoardDto;
import vn.yenthan.taskmanager.scrumboard.entity.BoardEntity;
import vn.yenthan.taskmanager.scrumboard.entity.ListEntity;
import vn.yenthan.taskmanager.scrumboard.mapper.ScrumboardMapper;
import vn.yenthan.taskmanager.scrumboard.repository.BoardRepository;
import vn.yenthan.taskmanager.scrumboard.repository.ListRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final ListRepository listRepository;
    private final ScrumboardMapper scrumboardMapper;

    @Transactional(readOnly = true)
    public List<BoardDto> getAllBoards() {
        log.info("Fetching all boards");
        List<BoardEntity> boards = boardRepository.findAllWithLists();
        
        // Load cards for all lists efficiently
        for (BoardEntity board : boards) {
            if (board.getLists() != null && !board.getLists().isEmpty()) {
                // Get all list IDs for this board
                List<Long> listIds = board.getLists().stream()
                        .map(EntityBase::getId)
                        .toList();
                
                // Fetch all lists with their cards in one query
                List<ListEntity> listsWithCards = listRepository.findByIdsWithCards(listIds);
                
                // Update the board's lists with cards
                for (ListEntity list : board.getLists()) {
                    listsWithCards.stream()
                            .filter(l -> l.getId().equals(list.getId()))
                            .findFirst()
                            .ifPresent(l -> list.setCards(l.getCards()));
                }
            }
        }
        
        return scrumboardMapper.toBoardDtoList(boards);
    }

    @Transactional(readOnly = true)
    public BoardDto getBoardById(Long id) {
        log.info("Fetching board with id: {}", id);
        
        // Fetch board with lists first
        BoardEntity board = boardRepository.findByIdWithLists(id)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + id));
        
        // Load members separately
        boardRepository.findByIdWithMembers(id).ifPresent(b -> {
            board.setMembers(b.getMembers());
        });
        
        // Load cards for all lists
        if (board.getLists() != null && !board.getLists().isEmpty()) {
            List<Long> listIds = board.getLists().stream()
                    .map(EntityBase::getId)
                    .toList();
            
            List<ListEntity> listsWithCards = listRepository.findByIdsWithCards(listIds);
            
            for (ListEntity list : board.getLists()) {
                listsWithCards.stream()
                        .filter(l -> l.getId().equals(list.getId()))
                        .findFirst()
                        .ifPresent(l -> list.setCards(l.getCards()));
            }
        }
        
        return scrumboardMapper.toBoardDto(board);
    }

    public BoardDto createBoard(CreateBoardRequest request) {
        log.info("Creating new board with name: {}", request.getName());
        
        if (boardRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Board with name '" + request.getName() + "' already exists");
        }

        BoardEntity board = new BoardEntity();
        board.setName(request.getName());
        
        BoardEntity savedBoard = boardRepository.save(board);
        log.info("Board created successfully with id: {}", savedBoard.getId());
        
        return scrumboardMapper.toBoardDto(savedBoard);
    }

    public BoardDto updateBoard(UpdateBoardRequest request) {
        log.info("Updating board with id: {}", request.getId());
        
        BoardEntity board = boardRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + request.getId()));

        if (!board.getName().equals(request.getName()) && boardRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Board with name '" + request.getName() + "' already exists");
        }

        board.setName(request.getName());
        BoardEntity updatedBoard = boardRepository.save(board);
        log.info("Board updated successfully with id: {}", updatedBoard.getId());
        
        return scrumboardMapper.toBoardDto(updatedBoard);
    }

    public void deleteBoard(Long id) {
        log.info("Deleting board with id: {}", id);
        
        if (!boardRepository.existsById(id)) {
            throw new NotFoundException("Board not found with id: " + id);
        }
        
        boardRepository.deleteById(id);
        log.info("Board deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<BoardDto> getBoardsByUserId(Long userId) {
        log.info("Fetching boards for user with id: {}", userId);
        List<BoardEntity> boards = boardRepository.findByUserId(userId);
        return scrumboardMapper.toBoardDtoList(boards);
    }
}

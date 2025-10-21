package vn.yenthan.taskmanager.scrumboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
import vn.yenthan.taskmanager.scrumboard.repository.BoardRoleRepository;
import vn.yenthan.taskmanager.scrumboard.repository.ListRepository;
import vn.yenthan.taskmanager.scrumboard.entity.BoardRoleEntity;
import vn.yenthan.taskmanager.scrumboard.security.AuthzService;
import vn.yenthan.taskmanager.scrumboard.service.MemberService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final ListRepository listRepository;
    private final BoardRoleRepository boardRoleRepository;
    private final MemberService memberService;
    private final ScrumboardMapper scrumboardMapper;
    private final AuthzService authzService;

    @Transactional(readOnly = true)
    public List<BoardDto> getAllBoards(Long currentUserId) {
        List<BoardEntity> boards;
        
        // Global ADMIN có thể thấy tất cả board
        if (authzService.hasGlobalAdminRole(currentUserId)) {
            boards = boardRepository.findAllWithLists();
        } else {
            // Chỉ lấy board mà user là member
            boards = boardRepository.findByUserId(currentUserId);
        }
        
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
    public BoardDto getBoardById(Long id, Long currentUserId) {
        // Kiểm tra quyền truy cập board
        if (!authzService.isBoardMember(currentUserId, id)) {
            throw new AccessDeniedException("Access denied to board " + id);
        }
        
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

    @Transactional(rollbackFor = Exception.class)
    public BoardDto createBoard(CreateBoardRequest request, Long currentUserId) {
        log.info("Creating board: {} for user: {}", request.getName(), currentUserId);
        
        if (boardRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Board with name '" + request.getName() + "' already exists");
        }

        // Tạo board
        BoardEntity board = new BoardEntity();
        board.setName(request.getName());
        BoardEntity savedBoard = boardRepository.save(board);
        
        // Tạo default roles cho board
        BoardRoleEntity ownerRole = createDefaultOwnerRole(savedBoard);
        BoardRoleEntity memberRole = createDefaultMemberRole(savedBoard);
        
        // Tự động thêm creator làm OWNER
        memberService.addMemberToBoard(savedBoard.getId(), currentUserId, currentUserId, ownerRole.getId());
        
        log.info("Board created successfully with ID: {} and owner role assigned to user: {}", 
                savedBoard.getId(), currentUserId);
        
        return scrumboardMapper.toBoardDto(savedBoard);
    }

    public BoardDto updateBoard(UpdateBoardRequest request, Long currentUserId) {
        // Kiểm tra quyền chỉnh sửa board
        if (!authzService.canEditBoard(currentUserId, request.getId())) {
            throw new AccessDeniedException("Access denied: Cannot edit board " + request.getId());
        }
        
        BoardEntity board = boardRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + request.getId()));

        if (!board.getName().equals(request.getName()) && boardRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Board with name '" + request.getName() + "' already exists");
        }

        board.setName(request.getName());
        BoardEntity updatedBoard = boardRepository.save(board);
        
        return scrumboardMapper.toBoardDto(updatedBoard);
    }

    public void deleteBoard(Long id, Long currentUserId) {
        // Kiểm tra quyền xóa board
        if (!authzService.canDeleteBoard(currentUserId, id)) {
            throw new AccessDeniedException("Access denied: Cannot delete board " + id);
        }
        
        if (!boardRepository.existsById(id)) {
            throw new NotFoundException("Board not found with id: " + id);
        }
        
        boardRepository.deleteById(id);
    }

    
    /**
     * Tạo OWNER role cho board mới
     */
    private BoardRoleEntity createDefaultOwnerRole(BoardEntity board) {
        BoardRoleEntity ownerRole = new BoardRoleEntity();
        ownerRole.setBoard(board);
        ownerRole.setName("OWNER");
        ownerRole.setDescription("Board Owner - Full access");
        ownerRole.setIsDefault(false);
        ownerRole.setCreatedBy("system");
        ownerRole.setUpdatedBy("system");
        
        return boardRoleRepository.save(ownerRole);
    }
    
    /**
     * Tạo MEMBER role cho board mới
     */
    private BoardRoleEntity createDefaultMemberRole(BoardEntity board) {
        BoardRoleEntity memberRole = new BoardRoleEntity();
        memberRole.setBoard(board);
        memberRole.setName("MEMBER");
        memberRole.setDescription("Board Member - Basic access");
        memberRole.setIsDefault(true);
        memberRole.setCreatedBy("system");
        memberRole.setUpdatedBy("system");
        
        return boardRoleRepository.save(memberRole);
    }
}

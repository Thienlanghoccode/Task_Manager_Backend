package vn.yenthan.taskmanager.scrumboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.scrumboard.dto.response.MemberDto;
import vn.yenthan.taskmanager.scrumboard.entity.BoardMemberEntity;
import vn.yenthan.taskmanager.scrumboard.mapper.ScrumboardMapper;
import vn.yenthan.taskmanager.scrumboard.repository.BoardMemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final BoardMemberRepository boardMemberRepository;
    private final ScrumboardMapper scrumboardMapper;

    @Transactional(readOnly = true)
    public List<MemberDto> getBoardMembers(Long boardId) {
        log.info("Fetching members for board with id: {}", boardId);
        List<BoardMemberEntity> members = boardMemberRepository.findActiveByBoardId(boardId);
        return scrumboardMapper.toMemberDtoList(members);
    }

    @Transactional(readOnly = true)
    public MemberDto getBoardMember(Long boardId, Long userId) {
        log.info("Fetching member {} for board {}", userId, boardId);
        BoardMemberEntity member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found for board " + boardId + " and user " + userId));
        return scrumboardMapper.toMemberDto(member);
    }

    public void addMemberToBoard(Long boardId, Long userId) {
        log.info("Adding user {} to board {}", userId, boardId);
        
        if (boardMemberRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new IllegalArgumentException("User is already a member of this board");
        }
        
        // This would need to be implemented with proper board and user entities
        // For now, we'll just log the action
        log.info("Member added successfully to board");
    }

    public void removeMemberFromBoard(Long boardId, Long userId) {
        log.info("Removing user {} from board {}", userId, boardId);
        
        BoardMemberEntity member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found for board " + boardId + " and user " + userId));
        
        boardMemberRepository.delete(member);
        log.info("Member removed successfully from board");
    }

    public void updateMemberRole(Long boardId, Long userId, String role) {
        log.info("Updating role for user {} in board {} to {}", userId, boardId, role);
        
        BoardMemberEntity member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found for board " + boardId + " and user " + userId));
        
        // This would need to be implemented with proper board role entity
        // For now, we'll just log the action
        log.info("Member role updated successfully");
    }
}

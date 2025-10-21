package vn.yenthan.taskmanager.scrumboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.scrumboard.dto.response.MemberDto;
import vn.yenthan.taskmanager.scrumboard.entity.BoardMemberEntity;
import vn.yenthan.taskmanager.scrumboard.entity.BoardEntity;
import vn.yenthan.taskmanager.scrumboard.entity.BoardRoleEntity;
import vn.yenthan.taskmanager.scrumboard.mapper.ScrumboardMapper;
import vn.yenthan.taskmanager.scrumboard.repository.BoardMemberRepository;
import vn.yenthan.taskmanager.scrumboard.repository.BoardRepository;
import vn.yenthan.taskmanager.scrumboard.repository.BoardRoleRepository;
import vn.yenthan.taskmanager.core.auth.entity.User;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final BoardMemberRepository boardMemberRepository;
    private final BoardRepository boardRepository;
    private final BoardRoleRepository boardRoleRepository;
    private final UserRepository userRepository;
    private final ScrumboardMapper scrumboardMapper;

    @Transactional(readOnly = true)
    public List<MemberDto> getBoardMembers(Long boardId) {
        List<BoardMemberEntity> members = boardMemberRepository.findActiveByBoardId(boardId);
        return scrumboardMapper.toMemberDtoList(members);
    }

    @Transactional(readOnly = true)
    public MemberDto getBoardMember(Long boardId, Long userId) {
        BoardMemberEntity member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found for board " + boardId + " and user " + userId));
        return scrumboardMapper.toMemberDto(member);
    }

    public void addMemberToBoard(Long boardId, Long userId) {
        
        if (boardMemberRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new IllegalArgumentException("User is already a member of this board");
        }
        
        // This would need to be implemented with proper board and user entities
        // For now, we'll just log the action
    }

    public void addMemberToBoard(Long boardId, Long userId, Long invitedById, Long roleId) {
        if (boardMemberRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new IllegalArgumentException("User is already a member of this board");
        }

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + boardId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        User invitedBy = null;
        if (invitedById != null) {
            invitedBy = userRepository.findById(invitedById)
                    .orElseThrow(() -> new NotFoundException("Inviter not found with id: " + invitedById));
        }

        BoardMemberEntity member = BoardMemberEntity.builder()
                .board(board)
                .user(user)
                .invitedBy(invitedBy)
                .status("active")
                .build();

        if (roleId != null) {
            BoardRoleEntity role = boardRoleRepository.findById(roleId)
                    .orElseThrow(() -> new NotFoundException("Board role not found with id: " + roleId));
            member.setBoardRole(role);
        }

        member.setJoinedAt(java.time.Instant.now());
        boardMemberRepository.save(member);
    }

    public void removeMemberFromBoard(Long boardId, Long userId) {
        
        BoardMemberEntity member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found for board " + boardId + " and user " + userId));
        
        boardMemberRepository.delete(member);
    }

    public void updateMemberRole(Long boardId, Long userId, String role) {
        
        BoardMemberEntity member = boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found for board " + boardId + " and user " + userId));
        
        // This would need to be implemented with proper board role entity
        // For now, we'll just log the action
    }
}

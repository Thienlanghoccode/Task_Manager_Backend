package vn.yenthan.taskmanager.scrumboard.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.yenthan.taskmanager.core.auth.entity.Role;
import vn.yenthan.taskmanager.core.auth.enums.RoleType;
import vn.yenthan.taskmanager.core.auth.repository.UserRoleRepository;
import vn.yenthan.taskmanager.scrumboard.entity.BoardMemberEntity;
import vn.yenthan.taskmanager.scrumboard.entity.BoardRoleEntity;
import vn.yenthan.taskmanager.scrumboard.repository.BoardMemberRepository;
import vn.yenthan.taskmanager.scrumboard.security.AuthzService;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthzServiceImpl implements AuthzService {

    private final BoardMemberRepository boardMemberRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public boolean isBoardOwner(Long userId, Long boardId) {
        // 1. Global ADMIN có quyền OWNER trên tất cả board
        if (hasGlobalAdminRole(userId)) {
            return true;
        }
        
        // 2. Kiểm tra board OWNER
        return boardMemberRepository.findByBoardIdAndUserIdWithRole(boardId, userId)
                .map(BoardMemberEntity::getBoardRole)
                .map(role -> role != null && "OWNER".equalsIgnoreCase(role.getName()))
                .orElse(false);
    }
    
    @Override
    public boolean isBoardMember(Long userId, Long boardId) {
        // 1. Global ADMIN luôn là member
        if (hasGlobalAdminRole(userId)) {
            return true;
        }
        
        // 2. Kiểm tra board membership
        return boardMemberRepository.existsByBoardIdAndUserId(boardId, userId);
    }
    
    @Override
    public boolean canInviteMembers(Long userId, Long boardId) {
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean canEditBoard(Long userId, Long boardId) {
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean canDeleteBoard(Long userId, Long boardId) {
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean hasGlobalAdminRole(Long userId) {
        Set<Role> roles = userRoleRepository.findRolesByUserId(userId);
        return roles.stream()
                .anyMatch(role -> role.getName() == RoleType.ADMIN);
    }
    
    // ========== LIST PERMISSIONS ==========
    
    @Override
    public boolean canCreateList(Long userId, Long boardId) {
        // OWNER: có thể tạo list
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean canEditList(Long userId, Long boardId) {
        // OWNER: có thể sửa list
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean canDeleteList(Long userId, Long boardId) {
        // OWNER: có thể xóa list
        return isBoardOwner(userId, boardId);
    }
    
    // ========== CARD PERMISSIONS ==========
    
    @Override
    public boolean canCreateCard(Long userId, Long boardId) {
        // OWNER: có thể tạo card
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean canEditCard(Long userId, Long boardId) {
        // OWNER: có thể sửa card
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean canDeleteCard(Long userId, Long boardId) {
        // OWNER: có thể xóa card
        return isBoardOwner(userId, boardId);
    }
    
    @Override
    public boolean canUpdateCard(Long userId, Long boardId) {
        // MEMBER: có thể update card (title, description, etc.)
        return isBoardMember(userId, boardId);
    }
    
    @Override
    public boolean canCategoryCard(Long userId, Long boardId) {
        // MEMBER: có thể category card (move between lists, assign labels, etc.)
        return isBoardMember(userId, boardId);
    }
}



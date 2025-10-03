package vn.yenthan.taskmanager.core.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.yenthan.taskmanager.core.auth.entity.Role;
import vn.yenthan.taskmanager.core.auth.entity.User;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;
import vn.yenthan.taskmanager.core.auth.repository.UserRoleRepository;
import vn.yenthan.taskmanager.core.auth.service.UserDetailService;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageKeys.AUTH_INVALID_CREDENTIALS));
        Set<Role> roles = userRoleRepository.findRolesByUserId(user.getId());
        user.setRoles(roles);
        return user;
    }
}

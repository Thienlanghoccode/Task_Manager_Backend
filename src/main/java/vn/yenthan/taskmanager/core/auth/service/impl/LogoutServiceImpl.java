package vn.yenthan.taskmanager.core.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;
import vn.yenthan.taskmanager.core.auth.entity.Token;
import vn.yenthan.taskmanager.core.auth.entity.User;
import vn.yenthan.taskmanager.core.auth.enums.TokenType;
import vn.yenthan.taskmanager.core.auth.repository.TokenRepository;
import vn.yenthan.taskmanager.core.auth.repository.UserRepository;
import vn.yenthan.taskmanager.core.auth.service.JwtService;
import vn.yenthan.taskmanager.core.auth.service.LogoutService;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutServiceImpl implements LogoutService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public void logoutDevice(HttpServletRequest request) throws AccessDeniedException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException(MessageKeys.AUTH_UNAUTHORIZED);
        }
        String accessToken = authHeader.substring(7);

        String username = jwtService.extractUsername(accessToken, TokenType.ACCESS_TOKEN);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_NOT_FOUND));


        String deviceId = request.getHeader("x-device-id");
        if (deviceId == null || deviceId.isBlank()) {
            throw new NotFoundException(MessageKeys.AUTH_INVALID_DEVICE_ID);
        }

        Token token = tokenRepository.findByUserAndDeviceIdAndRevokedFalseAndExpiredFalse(user, deviceId)
                .orElseThrow(() -> new NotFoundException(MessageKeys.AUTH_INVALID_TOKEN));

        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);

        SecurityContextHolder.clearContext();
        log.info("Logged out device {} for user {}", deviceId, username);
    }

    @Override
    public void logoutAllDevices(HttpServletRequest request) throws AccessDeniedException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException(MessageKeys.AUTH_UNAUTHORIZED);
        }
        String accessToken = authHeader.substring(7);
        String username = jwtService.extractUsername(accessToken, TokenType.ACCESS_TOKEN);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_NOT_FOUND));

        List<Token> activeTokens = tokenRepository.findByUserAndRevokedFalseAndExpiredFalse(user);
        if (activeTokens.isEmpty()) {
            throw new NotFoundException(MessageKeys.AUTH_INVALID_TOKEN);
        }

        for (Token token : activeTokens) {
            token.setRevoked(true);
            token.setExpired(true);
        }
        tokenRepository.saveAll(activeTokens);
        SecurityContextHolder.clearContext();
        log.info("Logged out all devices for user {}", username);
    }
}

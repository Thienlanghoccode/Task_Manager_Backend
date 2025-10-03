package vn.yenthan.taskmanager.core.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import vn.yenthan.taskmanager.core.auth.entity.Token;
import vn.yenthan.taskmanager.core.auth.entity.User;
import vn.yenthan.taskmanager.core.auth.repository.TokenRepository;
import vn.yenthan.taskmanager.core.auth.service.UserDeviceTokenService;
import vn.yenthan.taskmanager.core.exception.payload.NotFoundException;
import vn.yenthan.taskmanager.util.MessageKeys;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeviceTokenServiceImpl implements UserDeviceTokenService {

    private final TokenRepository tokenRepository;

    @Override
    public void validateAndLimitDeviceIp(User user, String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            throw new NotFoundException(MessageKeys.AUTH_INVALID_DEVICE_ID);
        }

        tokenRepository.findByUserAndDeviceIdAndRevokedFalseAndExpiredFalse(user, deviceId)
                .ifPresent(tokenRepository::delete);

        List<Token> activeTokens = tokenRepository.findByUserAndRevokedFalseAndExpiredFalse(user);
        int MAX_TOKEN = 3;
        if (activeTokens.size() >= MAX_TOKEN) {
            activeTokens.sort(Comparator.comparing(Token::getCreatedAt));
            Token toDelete = activeTokens.get(0);
            tokenRepository.delete(toDelete);
        }
    }
}

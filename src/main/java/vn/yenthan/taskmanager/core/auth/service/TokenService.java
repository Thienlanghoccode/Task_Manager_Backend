package vn.yenthan.taskmanager.core.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import vn.yenthan.taskmanager.core.auth.dto.response.TokenResponse;
import vn.yenthan.taskmanager.core.auth.entity.Token;

public interface TokenService {
    TokenResponse refresh(HttpServletRequest request);
    void saveToken(Token token);
}

package vn.yenthan.taskmanager.core.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import vn.yenthan.taskmanager.core.auth.dto.request.LogInRequest;
import vn.yenthan.taskmanager.core.auth.dto.request.RegisterRequest;
import vn.yenthan.taskmanager.core.auth.dto.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse login(LogInRequest request, HttpServletRequest webRequest);
    void register(RegisterRequest request, HttpServletRequest webRequest);
    TokenResponse refresh(HttpServletRequest request);
}

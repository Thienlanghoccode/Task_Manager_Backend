package vn.yenthan.taskmanager.core.auth.service;

import vn.yenthan.taskmanager.core.auth.entity.User;

public interface UserDeviceTokenService {
    void validateAndLimitDeviceIp(User user, String deviceId);
}

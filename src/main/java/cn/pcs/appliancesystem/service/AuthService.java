package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.LoginRequest;
import cn.pcs.appliancesystem.entity.LoginResponse;
import cn.pcs.appliancesystem.entity.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void register(RegisterRequest request);
}


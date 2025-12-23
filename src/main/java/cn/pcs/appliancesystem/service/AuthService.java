package cn.pcs.appliancesystem.service;

import cn.pcs.appliancesystem.entity.LoginRequest;
import cn.pcs.appliancesystem.entity.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}


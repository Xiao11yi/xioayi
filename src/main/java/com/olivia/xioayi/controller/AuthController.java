package com.olivia.xioayi.controller;


import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.common.ApiResponse;
import com.olivia.xioayi.dto.LoginRequest;
import com.olivia.xioayi.dto.LoginResponse;
import com.olivia.xioayi.service.TokenService;
import com.olivia.xioayi.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "认证管理", description = "登录、登出、会话检查")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Operation(summary = "会话检查", description = "返回当前登录用户的问候信息，需要 Bearer Token")
    @GetMapping("/api/hello")
    public ApiResponse<String> hello(Principal principal) {
        return ApiResponse.success("Hello, " + principal.getName());
    }

    @Operation(summary = "用户登录", description = "使用用户名和密码登录，成功后返回 JWT Token")
    @Log(title = "认证管理", businessType = BusinessType.LOGIN)
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword())
        );
        String username = authentication.getName();
        String token = jwtUtil.generateToken(username);
        tokenService.storeToken(token, username);
        return ApiResponse.success(new LoginResponse(token, username));
    }

    @Operation(summary = "用户登出", description = "使当前 JWT Token 失效，后续请求需重新登录")
    @Log(title = "认证管理", businessType = BusinessType.LOGOUT)
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.removeToken(token);
        }
        return ApiResponse.success();
    }
}
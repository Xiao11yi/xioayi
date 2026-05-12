package com.olivia.xioayi.controller;


import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.common.ApiResponse;
import com.olivia.xioayi.dto.LoginRequest;
import com.olivia.xioayi.dto.LoginResponse;
import com.olivia.xioayi.service.TokenService;
import com.olivia.xioayi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @GetMapping("/api/hello")
    public ApiResponse<String> hello(Principal principal) {
        return ApiResponse.success("Hello, " + principal.getName());
    }

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
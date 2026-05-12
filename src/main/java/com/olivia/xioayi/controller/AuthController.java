package com.olivia.xioayi.controller;


import com.olivia.xioayi.dto.LoginRequest;
import com.olivia.xioayi.dto.LoginResponse;
import com.olivia.xioayi.service.TokenService;
import com.olivia.xioayi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @GetMapping("/api/hello")
    public String hello(Principal principal) {
        return "Hello, " + principal.getName();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword())
        );
        String username = authentication.getName();
        String token = jwtUtil.generateToken(username);
        tokenService.storeToken(token, username);
        return ResponseEntity.ok(new LoginResponse(token, username));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.removeToken(token);
        }
        return ResponseEntity.ok(Map.of("message", "注销成功"));
    }
}
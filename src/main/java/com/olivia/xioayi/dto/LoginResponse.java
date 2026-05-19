package com.olivia.xioayi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginResponse {
    @Schema(description = "JWT Token，后续请求需在 Authorization 头携带", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    @Schema(description = "用户名", example = "admin")
    private String username;

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
package com.olivia.xioayi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.olivia.xioayi.common.ApiResponse;
import com.olivia.xioayi.dao.User;
import com.olivia.xioayi.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "用户查询与状态管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @Operation(summary = "用户列表")
    @GetMapping
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userMapper.selectList(null));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody User user) {
        User u = new User();
        u.setId(id);
        u.setEnabled(user.getEnabled());
        userMapper.updateById(u);
        return ApiResponse.success();
    }
}

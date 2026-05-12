package com.olivia.xioayi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.olivia.xioayi.dao.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
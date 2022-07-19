package com.example.reggie.service.front.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.front.User;
import com.example.reggie.mapper.front.UserMapper;
import com.example.reggie.service.front.UserService;
import org.springframework.stereotype.Service;

/**
 * @Date: 2022/6/25 20:08
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

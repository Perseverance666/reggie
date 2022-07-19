package com.example.reggie.mapper.front;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.entity.front.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Date: 2022/6/25 20:06
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

package com.example.reggie.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.entity.backend.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Date: 2022/6/11 17:36
 */

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}

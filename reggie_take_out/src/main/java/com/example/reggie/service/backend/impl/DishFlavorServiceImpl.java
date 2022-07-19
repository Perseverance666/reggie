package com.example.reggie.service.backend.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.mapper.backend.DishFlavorMapper;
import com.example.reggie.entity.backend.DishFlavor;
import com.example.reggie.service.backend.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @Date: 2022/6/17 21:53
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}

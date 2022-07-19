package com.example.reggie.service.front.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.front.ShoppingCart;
import com.example.reggie.mapper.front.ShoppingCartMapper;
import com.example.reggie.service.front.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @Date: 2022/7/3 12:42
 */

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}

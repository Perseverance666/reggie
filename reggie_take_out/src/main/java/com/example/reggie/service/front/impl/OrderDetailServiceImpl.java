package com.example.reggie.service.front.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.front.OrderDetail;
import com.example.reggie.mapper.front.OrderDetailMapper;
import com.example.reggie.service.front.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @Date: 2022/7/3 23:18
 */

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}

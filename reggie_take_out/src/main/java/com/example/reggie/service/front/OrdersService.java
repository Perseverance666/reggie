package com.example.reggie.service.front;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.front.Orders;

/**
 * @Date: 2022/7/3 23:17
 */

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单,同时控制orders和order_detail两张表
     * @param order
     * @return
     */
    void submit(Orders order);
}

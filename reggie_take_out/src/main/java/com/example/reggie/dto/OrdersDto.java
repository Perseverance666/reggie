package com.example.reggie.dto;

import com.example.reggie.entity.front.OrderDetail;
import com.example.reggie.entity.front.Orders;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Date: 2022/7/5 15:18
 */
@Data
public class OrdersDto extends Orders {

    //该订单的所有菜品
    private List<OrderDetail> orderDetails;

    //该订单总商品数
    private BigDecimal sumNum;

}
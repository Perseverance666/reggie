package com.example.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.dto.OrdersDto;
import com.example.reggie.entity.front.OrderDetail;
import com.example.reggie.entity.front.Orders;
import com.example.reggie.service.front.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date: 2022/7/3 23:15
 */

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单,同时控制orders和order_detail两张表
     * @param order
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order){
        if (order == null){
            throw new CustomException("添加订单失败");
        }
        orderService.submit(order);
        return R.success("添加订单成功");
    }

    /**
     * 订单明细展示
     * 管理端
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(number != null,Orders::getNumber,number);
        lqw.between(beginTime != null && endTime != null,Orders::getOrderTime,beginTime,endTime);
        lqw.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     * @param order
     * @return
     */
    @PutMapping
    public R<String> changeStatus(@RequestBody Orders order){
        if(order == null){
            throw new CustomException("修改订单状态失败");
        }
        LambdaUpdateWrapper<Orders> luw = new LambdaUpdateWrapper<>();
        luw.eq(order.getId() != null,Orders::getId,order.getId());
        luw.set(order.getStatus() != null,Orders::getStatus,order.getStatus());
        orderService.update(luw);
        return R.success("修改订单状态成功");
    }

    /**
     * 订单查询
     * 移动端
     * 由于页面显示，需要OrdersDto
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage,lqw);

        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> ordersRecords = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = ordersRecords.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            //获取订单id
            Long id = item.getId();
            LambdaQueryWrapper<OrderDetail> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(id != null, OrderDetail::getOrderId, id);
            List<OrderDetail> orderDetailList = orderDetailService.list(lqw2);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }

    /**
     * 再来一单
     * 已完成的订单可以再来一单
     * @param order
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders order){
        if(order == null){
            throw new CustomException("再来一单失败");
        }

        LambdaUpdateWrapper<Orders> luw = new LambdaUpdateWrapper<>();
        luw.eq(order.getId() != null,Orders::getId,order.getId());
        //修改status由4变为2，即由已完成改为待派送
        luw.set(Orders::getStatus,2);
        //修改下单时间
        luw.set(Orders::getOrderTime, LocalDateTime.now());
        orderService.update(luw);
        return R.success("再来一单成功");
    }

}

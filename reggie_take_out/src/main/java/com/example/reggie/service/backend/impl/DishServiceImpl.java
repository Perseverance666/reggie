package com.example.reggie.service.backend.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.dto.DishDto;
import com.example.reggie.mapper.backend.DishMapper;
import com.example.reggie.entity.backend.Dish;
import com.example.reggie.entity.backend.DishFlavor;
import com.example.reggie.service.backend.DishFlavorService;
import com.example.reggie.service.backend.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date: 2022/6/11 17:37
 */

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * 由于Dish类中没有flavor属性无法自动封装，用自己写的方法saveWithFlavor以及DishDto类来添加菜品
     * @param dishDto
     */
    @Transactional    //要改变两张表，设置事务，springboot默认开启事务
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //先添加dish部分
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        //获取菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //由于DishFlavor中dishId目前没有值，为每一个口味设置dishId，并返回给flavors
        flavors = flavors.stream().map((item) -> {
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        //添加flavor数据到dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品信息和口味信息，用于修改功能的回显
     * @param dishId
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long dishId) {
        //查询dish表
        Dish dish = this.getById(dishId);

        //将dish拷贝到dishDto中
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询dish_flavor表
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> dishFlavors = dishFlavorService.list(lqw);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 修改菜品，同时修改对应的口味数据
     * 由于Dish类中没有flavor属性无法自动封装，用自己写的方法updateWithFlavor以及DishDto类来修改菜品
     * @param dishDto
     * @return
     */
    @Transactional    //要改变两张表，设置事务，springboot默认开启事务
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //修改dish表
        this.updateById(dishDto);
        Long dishId = dishDto.getId();

        //修改dish_flavor表
        //先删除dish_flavor表中原来的口味数据
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(lqw);
        //获取修改之后的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //由于DishFlavor中dishId目前没有值，为每一个口味设置dishId，并返回给flavors（与添加功能类似）
        flavors = flavors.stream().map((item) -> {
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());
        //修改dish_flavor表中的口味数据
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品，同时删除对应的口味数据，只能删除停售状态的菜品
     * 由于Dish类中没有flavor属性无法自动封装，用自己写的方法removeWithFlavor以及DishDto类来删除菜品
     * @param ids
     *
     * @return
     */
    @Transactional    //要改变两张表，设置事务，springboot默认开启事务
    @Override
    public void removeWithFlavor(List<Long> ids) {
        //检查所选菜品是否停售
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId,ids);
        lqw.eq(Dish::getStatus,1);
        if(this.count(lqw) > 0){
            throw new CustomException("菜品正在售卖中，不能删除！");
        }

        //停售状态下。可以删除
        //删除dish表
        this.removeByIds(ids);
        //删除dish_flavor表
        LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lqw2);

    }
}

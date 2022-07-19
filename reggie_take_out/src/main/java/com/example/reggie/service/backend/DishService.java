package com.example.reggie.service.backend;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.backend.Dish;

import java.util.List;

/**
 * @Date: 2022/6/11 17:37
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同时保存对应的口味数据
     * 由于Dish类中没有flavor属性无法自动封装，用自己写的方法saveWithFlavor以及DishDto类来添加菜品
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和口味信息，用于修改功能的回显
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品，同时修改对应的口味数据
     * 由于Dish类中没有flavor属性无法自动封装，用自己写的方法updateWithFlavor以及DishDto类来修改菜品
     * @param dishDto
     * @return
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 删除菜品，同时删除对应的口味数据
     * 由于Dish类中没有flavor属性无法自动封装，用自己写的方法removeWithFlavor以及DishDto类来删除菜品
     * @param ids
     * @return
     */
    void removeWithFlavor(List<Long> ids);
}

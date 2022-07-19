package com.example.reggie.service.backend;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.backend.Setmeal;

import java.util.List;

/**
 * @Date: 2022/6/11 17:40
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时添加套餐菜品关系
     * 由于Setmeal类中没有setmealDishes属性无法自动封装，用自己写的方法saveWithDsh以及setmealDto类来新增套餐
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 根据id查询套餐信息和套餐菜品关系，用于修改功能的回显
     * @param id
     * @return
     */
    SetmealDto getByIdWithDish(Long id);

    /**
     * 修改套餐，同时修改套餐菜品关系
     * 由于Setmeal类中没有setmealDishes属性无法自动封装，用自己写的方法saveWithDsh以及setmealDto类来新增套餐
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐关系，只能删除停售状态的套餐
     * 由于Setmeal类中没有setmealDishes属性无法自动封装，用自己写的方法saveWithDsh以及setmealDto类来新增套餐
     * @param ids
     */
    void removeWithDish(List<Long> ids);
}

package com.example.reggie.service.backend.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.mapper.backend.CategoryMapper;
import com.example.reggie.entity.backend.Category;
import com.example.reggie.entity.backend.Dish;
import com.example.reggie.entity.backend.Setmeal;
import com.example.reggie.service.backend.CategoryService;
import com.example.reggie.service.backend.DishService;
import com.example.reggie.service.backend.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Date: 2022/6/6 21:49
 */

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 自定义分类信息删除方法
     * @param id
     */
    @Override
    public void remove(Long id) {
        //设置条件构造器,判断是否有Dish关联这个id的Category
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishWrapper);
        if(dishCount > 0){
            //Dish与该Category有关联,抛出业务异常
            throw new CustomException("该分类下目前已有菜品，不可删除");

        }

        //设置条件构造器,判断是否有Setmeal关联这个id的Category
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealWrapper);
        if(setmealCount > 0){
            //Setmeal与该Category有关联,抛出业务异常
            throw new CustomException("该分类下目前已有套餐，不可删除");
        }

        //若Dish和Setmeal都没有与这个id的Category关联，执行mp删除操作
        super.removeById(id);
    }
}

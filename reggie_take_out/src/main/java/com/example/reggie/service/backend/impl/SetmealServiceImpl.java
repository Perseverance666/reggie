package com.example.reggie.service.backend.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.backend.SetmealDish;
import com.example.reggie.mapper.backend.SetmealMapper;
import com.example.reggie.entity.backend.Setmeal;
import com.example.reggie.service.backend.SetmealDishService;
import com.example.reggie.service.backend.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date: 2022/6/11 17:40
 */

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时添加套餐菜品关系
     * 由于Setmeal类中没有setmealDishes属性无法自动封装，用自己写的方法saveWithDsh以及setmealDto类来新增套餐
     * @param setmealDto
     */
    @Transactional    //控制setmeal和setmeal_dish两张表
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //添加数据到setmeal表中
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //由于SetmealDish中setmealId目前没有值，需要遍历赋值
        setmealDishes = setmealDishes.stream().map((item) -> {
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());

        //添加数据到setmeal_dish表中
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据id查询套餐信息和套餐菜品关系，用于修改功能的回显
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //获取setmeal信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //拷贝shuju
        BeanUtils.copyProperties(setmeal,setmealDto);

        //获取setmeal_dish信息
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(id != null,SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(lqw);
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    /**
     * 修改套餐，同时修改套餐菜品关系
     * 由于Setmeal类中没有setmealDishes属性无法自动封装，用自己写的方法saveWithDsh以及setmealDto类来新增套餐
     * @param setmealDto
     */
    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //修改setmeal表
        this.updateById(setmealDto);

        //先删除原来的套餐菜品关系
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lqw);

        //添加新的套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //为每一个setmealDish设置setmealId
        setmealDishes = setmealDishes.stream().map((item) -> {
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());
        //修改setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);

    }


    /**
     * 删除套餐，同时删除套餐关系，只能删除停售状态的套餐
     * 由于Setmeal类中没有setmealDishes属性无法自动封装，用自己写的方法saveWithDsh以及setmealDto类来新增套餐
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //先判断该套餐是否停售，只能删除停售状态的套餐
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        lqw.eq(Setmeal::getStatus,1);
        if(this.count(lqw) > 0){
            throw new CustomException("套餐正在售卖中，不能删除！");
        }

        //停售状态下，删除setmeal表中数据
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw2);
    }
}

package com.example.reggie.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.backend.Category;
import com.example.reggie.entity.backend.Dish;
import com.example.reggie.entity.backend.Setmeal;
import com.example.reggie.entity.backend.SetmealDish;
import com.example.reggie.service.backend.CategoryService;
import com.example.reggie.service.backend.DishService;
import com.example.reggie.service.backend.SetmealDishService;
import com.example.reggie.service.backend.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date: 2022/6/20 17:22
 */

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    /**
     * 新增套餐，同时添加套餐菜品关系
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        if(setmealDto == null){
            throw new CustomException("添加套餐失败！");
        }
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功！");
    }

    /**
     * 套餐分页展示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(name != null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,lqw);

        //处理套餐分类一栏
        Page<SetmealDto> setmealDtoPage = new Page<>(page,pageSize);
        //拷贝数据,除去records属性
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        //处理records属性,在其中加入categoryName
        List<SetmealDto> dtoRecords = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //Setmeal中属性拷贝到SetmealDto中
            BeanUtils.copyProperties(item, setmealDto);

            //为setmealDto设置categoryName
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(dtoRecords);

        return R.success(setmealDtoPage);
    }

    /**
     * 根据id查询套餐信息和套餐菜品关系，用于修改功能的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        if(setmealDto == null){
            throw new CustomException("所选套餐不存在！");
        }
        return R.success(setmealDto);
    }

    /**
     * 修改套餐,同时修改套餐关系
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        if (setmealDto == null){
            throw new CustomException("修改失败！");
        }
        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功！");
    }

    /**
     * 修改套餐售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        if(ids == null || ids.size() == 0){
            throw new CustomException("所选套餐不存在！");
        }

        LambdaUpdateWrapper<Setmeal> luw = new LambdaUpdateWrapper<>();
        luw.in(Setmeal::getId,ids);
        luw.set(Setmeal::getStatus,status);
        setmealService.update(new Setmeal(),luw);  //setmealService.update(luw) 不调用自动更新字段
        return R.success("售卖状态修改成功！");
    }

    /**
     * 删除套餐，同时删除套餐关系，只能删除停售状态的套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        if(ids == null || ids.size() == 0){
            throw new CustomException("获取id失败");
        }
        if(setmealService.listByIds(ids) == null){
            throw new CustomException("所选套餐不存在！");
        }
        setmealService.removeWithDish(ids);
        return R.success("删除成功！");
    }

    /**
     * 展示套餐信息
     * 移动端首页展示
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lqw);
        return R.success(list);
    }

    /**
     * 显示套餐具体信息
     * 用于移动端展示套餐信息
     * @param setmealId
     * @return
     */
    @GetMapping("/dish/{setmealId}")
    public R<List<Dish>> dish(@PathVariable Long setmealId){
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmealId != null,SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> setmealDishList = setmealDishService.list(lqw);
        if(setmealDishList == null){
            throw new CustomException("没有查到该套餐");
        }

        List<Dish> dishList = setmealDishList.stream().map((item) -> {
            LambdaQueryWrapper<Dish> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(Dish::getId, item.getDishId());
            Dish dish = dishService.getOne(lqw2);
            return dish;
        }).collect(Collectors.toList());

        return R.success(dishList);
    }
}

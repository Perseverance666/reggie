package com.example.reggie.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.backend.Category;
import com.example.reggie.entity.backend.Dish;
import com.example.reggie.entity.backend.DishFlavor;
import com.example.reggie.service.backend.CategoryService;
import com.example.reggie.service.backend.DishFlavorService;
import com.example.reggie.service.backend.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date: 2022/6/17 22:13
 */

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        if (dishDto == null) {
            throw new CustomException("添加菜品失败！");
        }
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功！");
    }

    /**
     * 菜品信息分页查询
     * 由于页面的菜品分类名字dish类中没有，无法封装，故使用DishDto
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<Dish>();
        lqw.like(name != null, Dish::getName, name);
        lqw.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, lqw);

        //--处理categoryName属性--

        Page<DishDto> dishDtoPage = new Page<>();
        //将dishPage中除去records外的其他属性全部拷贝到dishDtoPage中
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        //处理records属性
        List<Dish> dishRecords = dishPage.getRecords();
        List<DishDto> dishDtoRecords = dishRecords.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将dishPage中的records值拷贝到dishDto中
            BeanUtils.copyProperties(item, dishDto);

            //查询dishPage中records的每一个categoryId
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {  //自己在库中导入的数据有可能没有对应的category
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoRecords);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息，用于修改功能的回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if (dishDto == null) {
            throw new CustomException("所选菜品不存在！");
        }
        return R.success(dishDto);
    }

    /**
     * 修改菜品，同时修改对应的口味数据
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        if (dishDto == null) {
            throw new CustomException("所选菜品不存在！");
        }
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 删除菜品，同时删除对应的口味数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            throw new CustomException("获取id失败");
        }
        if(dishService.listByIds(ids) == null){
            throw new CustomException("所选菜品不存在！");
        }
        dishService.removeWithFlavor(ids);
        return R.success("删除成功！");
    }

    /**
     * 修改售卖状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            throw new CustomException("所选菜品不存在！");
        }

        //改变售卖状态
        LambdaUpdateWrapper<Dish> luw = new LambdaUpdateWrapper<>();
        luw.in(Dish::getId, ids);
        luw.set(Dish::getStatus, status);
        dishService.update(new Dish(), luw);  //dishService.update(luw) 不调用自动更新字段

        return R.success("售卖状态修改成功");
    }

    /**
     * 根据分类id或菜品名称展示该分类的所有菜品
     * 移动端显示菜品及口味，所以返回DishDto
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //添加categoryId条件
        lqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加name条件
        lqw.like(dish.getName() != null, Dish::getName, dish.getName());
        //添加在售条件
        lqw.eq(Dish::getStatus, 1);
        //添加排序
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);

        //-----------------------------------------------------------------------------------

        //对于移动端展示的修改，将返回值Dish改为DishDto
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将dish数据拷贝到dishDto中
            BeanUtils.copyProperties(item, dishDto);

            //查询口味数据
            LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(DishFlavor::getDishId, dishDto.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw2);

            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}

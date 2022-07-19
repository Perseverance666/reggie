package com.example.reggie.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.entity.backend.Category;
import com.example.reggie.service.backend.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Date: 2022/6/10 23:03
 */

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        if (category == null){
            throw new CustomException("添加失败");
        }
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 分类管理分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //根据sort字段进行排序
        lqw.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param id  //注：此处id名字必须与前端发送的id名字保持一致，否则取不到id值  http://localhost/category?id=1397844263642378242
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除分类，id为：{}",id);
        if(id == null){
            throw new CustomException("获取id失败");
        }
        if(categoryService.getById(id) == null){
            System.out.println("所选分类信息不存在");
        }
        //用自定义remove方法
        categoryService.remove(id);
        return R.success("分类信息删除成功");

    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);

        if(category == null){
            throw new CustomException("该分类信息不存在");
        }
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 根据type查询菜品分类or套餐分类有哪些，并将数据返回页面
     * 添加菜品时用到此功能
     * @param type
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(String type){
        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(type != null,Category::getType,type);
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }

}

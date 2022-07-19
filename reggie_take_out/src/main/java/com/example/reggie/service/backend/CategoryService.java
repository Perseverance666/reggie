package com.example.reggie.service.backend;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.backend.Category;

/**
 * @Date: 2022/6/6 21:50
 */
public interface CategoryService extends IService<Category> {
    /**
     * 根据id删除分类，确保当前分类不包含任何菜品和套餐才可删除
     * @param id
     */
    public void remove(Long id);
}

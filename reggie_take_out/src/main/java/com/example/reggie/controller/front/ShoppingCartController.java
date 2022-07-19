package com.example.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.entity.front.ShoppingCart;
import com.example.reggie.service.front.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Date: 2022/7/3 12:40
 */

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 展示购物车信息
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        lqw.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        if (shoppingCart == null){
            throw new CustomException("添加失败");
        }
        shoppingCart.setUserId(BaseContext.getCurrentId());
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        //设置sql语句条件
        if(dishId != null){
            //dish
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            //setmeal
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart sc = shoppingCartService.getOne(lqw);
        if(sc == null){   //shopping_cart表中没有该菜品或套餐，添加数据
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            sc = shoppingCart;

        }else{   //shopping_cart表中有该菜品或套餐,number+1
            sc.setNumber(sc.getNumber() + 1);
            shoppingCartService.updateById(sc);
        }
        return R.success(sc);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lqw);
        return R.success("清空购物车成功");
    }

    /**
     * 页面及购物车数据减一
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        if(shoppingCart == null){
            throw new CustomException("修改失败");
        }
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if(shoppingCart.getDishId() != null){   //dish
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {                                 //setmeal
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart sc = shoppingCartService.getOne(lqw);
        if(sc == null){
            return R.error("购物车中没有数据，修改失败");
        }
        if(sc.getNumber() <= 0){
            shoppingCartService.removeById(sc);
            return R.error("购物车中暂无该菜品或套餐");
        }

        //number--
        sc.setNumber(sc.getNumber() - 1);
        if(sc.getNumber() == 0){
            shoppingCartService.removeById(sc);
            return R.success(sc);
        }
        shoppingCartService.updateById(sc);
        return R.success(sc);
    }


}

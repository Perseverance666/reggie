package com.example.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.entity.front.AddressBook;
import com.example.reggie.service.front.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Date: 2022/6/26 21:30
 */

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增售货地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        if(addressBook == null){
            throw new CustomException("添加失败");
        }
        //设置用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        //向address_book表中添加数据
        addressBookService.save(addressBook);
        return R.success("添加成功");
    }

    /**
     * 展示地址信息
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        //获取userId
        Long userId = BaseContext.getCurrentId();

        //条件构造器
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(userId != null,AddressBook::getUserId,userId);
        lqw.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(lqw);

        return R.success(list);
    }

    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        addressBook = addressBookService.getById(addressBook.getId());
        if(addressBook == null){
            throw new CustomException("没有查到该地址信息");
        }
        //获取用户id
        Long userId = addressBook.getUserId();
        //先将所有addressBook的id_default全改为0
        LambdaUpdateWrapper<AddressBook> luw = new LambdaUpdateWrapper<>();
        luw.eq(AddressBook::getUserId,userId);
        luw.set(AddressBook::getIsDefault,0);
        addressBookService.update(luw);

        //将指定addressBook的isDefault设置为1，即设置为默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success("设置默认地址成功");
    }

    /**
     * 根据id查询addressBook，用于修改功能的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook == null){
            throw new CustomException("该地址信息不存在");
        }
        return R.success(addressBook);
    }

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        if(addressBook == null){
           throw new CustomException("修改信息失败");
        }
        addressBookService.updateById(addressBook);
        return R.success("修改信息成功");
    }

    /**
     * 删除地址信息
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook == null){
            throw new CustomException("该地址信息不存在");
        }
        addressBookService.removeById(addressBook.getId());
        return R.success("删除成功");
    }

    /**
     * 结算页面，显示默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> showDefault(){
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lqw.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(lqw);
        if(addressBook == null){
            throw new CustomException("没有设置默认地址");
        }
        return R.success(addressBook);
    }
}

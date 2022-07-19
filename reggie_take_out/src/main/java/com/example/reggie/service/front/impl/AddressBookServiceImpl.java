package com.example.reggie.service.front.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.entity.front.AddressBook;
import com.example.reggie.mapper.front.AddressBookMapper;
import com.example.reggie.service.front.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @Date: 2022/6/26 21:29
 */

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}

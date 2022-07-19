package com.example.reggie.service.backend.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.mapper.backend.EmployeeMapper;
import com.example.reggie.entity.backend.Employee;
import com.example.reggie.service.backend.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @Date: 2022/6/6 21:49
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}

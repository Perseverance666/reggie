package com.example.reggie.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.entity.backend.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Date: 2022/6/6 21:45
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>{
}

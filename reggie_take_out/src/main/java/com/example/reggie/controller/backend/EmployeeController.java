package com.example.reggie.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.entity.backend.Employee;
import com.example.reggie.service.backend.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @Date: 2022/6/6 21:49
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工的登录
     * 注：未登录状态，用拦截器拦截
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        if(employee == null){
            throw new CustomException("登录失败");
        }
        //1.对客户输入的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 根据提交的username查询数据库
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<Employee>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        //由于表中username是unique的，用getOne方法即可
        Employee emp = employeeService.getOne(lqw);
        //没有查到该用户
        if(emp == null){
            return R.error("用户名不存在！");
        }
        //3。检查密码是否正确
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误！");
        }
        //4。检查该用户是否状态可用
        if(emp.getStatus() == 0){
            return R.error("该用户已禁用！");
        }
        //5.数据存入session
        request.getSession().setAttribute("employee",emp.getId());
        //6.响应json数据
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1.清除session数据
        request.getSession().removeAttribute("employee");
        //2.响应退出信息
        return R.success("退出成功");
    }

    /**
     * 添加员工
     * 注：添加相同用户名时，要进行异常处理，使用全局异常处理
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        if(employee == null){
            throw new CustomException("添加失败");
        }
        //1.设置其他属性
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //2.执行mp的方法,其中要进行异常处理
        employeeService.save(employee);
        return R.success("添加成功");

    }

    /**
     * 员工信息分页查询
     * 注：mp要配置分页拦截器
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //模糊查询
        lqw.like(name !=null,Employee::getName,name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    /**
     * 修改员工信息
     * 注：由于Long数据精度问题，要扩展springmvc的消息转换器将Long转为String
     * 编辑，禁用按钮用到此功能
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        if(employee == null){
            throw new CustomException("该员工信息不存在！");
        }
        employeeService.updateById(employee);
        return R.success("修改成功！");
    }

    /**
     * 根据id查询员工信息
     * 编辑按钮用到此功能
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee == null){
            throw new CustomException("没有查询到对应员工信息");
        }
        return R.success(employee);
    }
}

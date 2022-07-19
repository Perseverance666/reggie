package com.example.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.entity.front.User;
import com.example.reggie.service.front.UserService;
import com.example.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Date: 2022/6/25 20:08
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isEmpty(phone)){
            throw new CustomException("短信发送失败");
        }
        //生成随机的6位验证码
        String code = ValidateCodeUtils.generateValidateCode(6).toString();
        log.info("code = {}",code);

//        //调用阿里云提供的短信服务API完成发送短信
//        SMSUtils.sendMessage("签名","模板",phone,code);
        //将生成验证码保存到session中
        session.setAttribute(phone,code);
        return R.success("手机验证码短信发送成功");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        if(map == null){
            throw new CustomException("登录失败");
        }
        //获取用户输入的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //获取session中存放的验证码
        Object codeInSession = session.getAttribute(phone);
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(!code.equals(codeInSession)){
            return R.error("验证码错误");
        }
        if(codeInSession != null && code.equals(codeInSession)){  //登录成功
            //判断是否是新用户
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User user = userService.getOne(lqw);
            if(user == null){  //新用户，自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            if (user.getStatus() == 0){   //不是新用户判断是否处于禁用状态
                return R.error("该用户已禁用！");
            }
            //登录成功，数据存入session，确保不被拦截器拦截
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");

    }

    /**
     * 移动端用户退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

    /**
     * 个人中心显示用户信息
     * @return
     */
    @GetMapping
    public R<User> getUser(){
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        if(user == null){
            throw new CustomException("没有查到该用户");
        }
        return R.success(user);
    }

}

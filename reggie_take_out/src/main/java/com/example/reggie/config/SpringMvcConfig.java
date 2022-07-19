package com.example.reggie.config;

import com.example.reggie.common.JacksonObjectMapper;
import com.example.reggie.interceptor.LoginCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Date: 2022/6/7 21:38
 */

//extends WebMvcConfigurationSupport 会拦截静态资源，用implements WebMvcConfigurer
@Slf4j
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginCheckInterceptor interceptor;

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**")
                .excludePathPatterns(
                        "/employee/login",
                        "/backend/**",
                        "/front/**",
                        "/common/**",
                        "/user/sendMsg",  //移动端发送短信
                        "/user/login");  //移动端登录
    }

    /**
     * 扩展springmvc框架的消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器。。。");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter mjhmc = new MappingJackson2HttpMessageConverter();
        //设置消息转换器，底层使用Jackson将Java对象转为Json
        mjhmc.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到springmvc框架的转换器集合中
        //index = 0 代表设置在第一个
        converters.add(0,mjhmc);
    }
}

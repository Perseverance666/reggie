package com.example.reggie.config;

import com.example.reggie.common.JacksonObjectMapper;
import com.example.reggie.interceptor.LoginCheckInterceptor;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * @Date: 2022/6/7 21:38
 */

//extends WebMvcConfigurationSupport 会拦截静态资源，用implements WebMvcConfigurer
// public class SpringMvcConfig extends WebMvcConfigurationSupport {
@Slf4j
@Configuration
@EnableSwagger2
@EnableKnife4j
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
                        "/user/sendMsg",    //移动端发送短信
                        "/user/login",      //移动端登录
                        "/doc.html",        //4个Swagger相关配置
                        "/webjars/**",
                        "/swagger-resources",
                        "/v2/api-docs");
    }

//    /**
//     * 设置静态资源映射
//     * @param registry
//     */
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        log.info("开始进行静态资源映射...");
//        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
//        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
//    }

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

    @Bean
    public Docket createRestApi() {
        // 文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.reggie.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("瑞吉外卖")
                .version("1.0")
                .description("瑞吉外卖接口文档")
                .build();
    }

}

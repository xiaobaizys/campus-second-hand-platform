package com.campus.secondhandplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        // 修复Windows系统下的路径分隔符问题
        String resourcePath = "file:" + uploadPath.replace("\\", "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourcePath);
    }
}
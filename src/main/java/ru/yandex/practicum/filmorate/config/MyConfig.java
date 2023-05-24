package ru.yandex.practicum.filmorate.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.filmorate.interceptor.LoggerInterceptor;

public class MyConfig implements WebMvcConfigurer {
    // Регистрируем interceptor
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor());
    }
}
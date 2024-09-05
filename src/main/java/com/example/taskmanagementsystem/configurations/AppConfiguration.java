package com.example.taskmanagementsystem.configurations;

import com.example.taskmanagementsystem.configurations.properties.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties({AppProperties.class, AppProperties.JwtProperties.class})
@EnableAspectJAutoProxy
public class AppConfiguration {
}

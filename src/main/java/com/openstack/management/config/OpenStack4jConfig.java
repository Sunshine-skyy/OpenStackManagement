package com.openstack.management.config;

import org.openstack4j.core.transport.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * OpenStack4j 配置类
 * 用于配置 HTTP 连接器和请求头
 */
@Configuration
public class OpenStack4jConfig {

    @PostConstruct
    public void init() {
        System.out.println("=== 初始化 OpenStack4j 配置 ===");
        System.out.println("OpenStack4j 配置完成");
    }
    
    /**
     * 创建 OpenStack4j 配置 Bean
     * 在创建 OSClient 时使用此配置
     */
    @Bean
    public Config openstack4jConfig() {
        return Config.newConfig()
                .withSSLVerificationDisabled()  // 如果使用自签名证书
                .withConnectionTimeout(30000)    // 30秒超时
                .withReadTimeout(30000);
    }
}


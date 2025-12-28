package com.openstack.management.config;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenStackConfig {

    @Value("${openstack.endpoint}")
    private String endpoint;

    @Value("${openstack.username}")
    private String username;

    @Value("${openstack.password}")
    private String password;

    @Value("${openstack.domain}")
    private String domain;

    @Value("${openstack.project}")
    private String project;

    /**
     * 创建OpenStack客户端（每次调用都创建新的，因为认证信息可能不同）
     */
    public OSClient.OSClientV3 createOSClient(String username, String password) {
        return OSFactory.builderV3()
                .endpoint(endpoint)
                .credentials(username, password)
                .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                .authenticate();
    }

    /**
     * 使用配置的默认用户创建客户端
     */
    public OSClient.OSClientV3 createDefaultOSClient() {
        return createOSClient(username, password);
    }
}



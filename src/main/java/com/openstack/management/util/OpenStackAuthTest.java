package com.openstack.management.util;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

/**
 * OpenStack 认证测试工具
 * 用于测试不同的认证配置
 */
public class OpenStackAuthTest {

    public static void main(String[] args) {
        String endpoint = "http://192.168.10.10:5000/v3";
        String username = "admin";
        String password = "admin";
        String domain = "default";
        String project = "admin";

        System.out.println("=== OpenStack 认证测试 ===");
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Username: " + username);
        System.out.println("Domain: " + domain);
        System.out.println("Project: " + project);
        System.out.println();

        // 测试1: 使用 domain 参数的认证
        System.out.println("测试1: 使用 credentials(username, password, domain)");
        try {
            OSClient.OSClientV3 osClient = OSFactory.builderV3()
                    .endpoint(endpoint)
                    .credentials(username, password, Identifier.byName(domain))
                    .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                    .authenticate();
            System.out.println("✓ 认证成功！");
            System.out.println("Token ID: " + osClient.getToken().getId());
        } catch (Exception e) {
            System.out.println("✗ 认证失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();

        // 测试2: 不使用 domain 参数的认证
        System.out.println("测试2: 使用 credentials(username, password)");
        try {
            OSClient.OSClientV3 osClient = OSFactory.builderV3()
                    .endpoint(endpoint)
                    .credentials(username, password)
                    .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                    .authenticate();
            System.out.println("✓ 认证成功！");
            System.out.println("Token ID: " + osClient.getToken().getId());
        } catch (Exception e) {
            System.out.println("✗ 认证失败: " + e.getMessage());
        }
        System.out.println();

        // 测试3: 使用 Default (大写) domain
        System.out.println("测试3: 使用 'Default' (大写) domain");
        try {
            OSClient.OSClientV3 osClient = OSFactory.builderV3()
                    .endpoint(endpoint)
                    .credentials(username, password, Identifier.byName("Default"))
                    .scopeToProject(Identifier.byName(project), Identifier.byName("Default"))
                    .authenticate();
            System.out.println("✓ 认证成功！");
            System.out.println("Token ID: " + osClient.getToken().getId());
        } catch (Exception e) {
            System.out.println("✗ 认证失败: " + e.getMessage());
        }
        System.out.println();

        // 测试4: 不指定 project scope
        System.out.println("测试4: 不指定 project scope");
        try {
            OSClient.OSClientV3 osClient = OSFactory.builderV3()
                    .endpoint(endpoint)
                    .credentials(username, password, Identifier.byName(domain))
                    .authenticate();
            System.out.println("✓ 认证成功！");
            System.out.println("Token ID: " + osClient.getToken().getId());
        } catch (Exception e) {
            System.out.println("✗ 认证失败: " + e.getMessage());
        }
    }
}


package com.openstack.management.util;

import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

/**
 * 简化的 Nova 测试 - 使用最基本的配置
 */
public class SimpleNovaTest {

    public static void main(String[] args) {
        String endpoint = "http://192.168.10.10:5000/v3";
        String username = "admin";
        String password = "admin";
        String domain = "Default";
        String project = "admin";

        System.out.println("=== 简化 Nova 测试 ===");
        System.out.println("Endpoint: " + endpoint);
        System.out.println();

        try {
            // 方式1: 使用默认配置
            System.out.println("测试1: 使用默认配置");
            try {
                OSClient.OSClientV3 osClient = OSFactory.builderV3()
                        .endpoint(endpoint)
                        .credentials(username, password, Identifier.byName(domain))
                        .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                        .authenticate();
                
                System.out.println("认证成功");
                
                // 尝试获取服务器列表
                try {
                    java.util.List<?> servers = osClient.compute().servers().list();
                    System.out.println("✓ 成功！实例数量: " + servers.size());
                } catch (Exception e) {
                    System.out.println("✗ 失败: " + e.getMessage());
                    if (e.getCause() != null) {
                        System.out.println("  原因: " + e.getCause().getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("✗ 认证失败: " + e.getMessage());
            }
            System.out.println();

            // 方式2: 禁用 SSL 验证
            System.out.println("测试2: 禁用 SSL 验证");
            try {
                Config config = Config.newConfig()
                        .withSSLVerificationDisabled();
                
                OSClient.OSClientV3 osClient = OSFactory.builderV3()
                        .endpoint(endpoint)
                        .credentials(username, password, Identifier.byName(domain))
                        .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                        .withConfig(config)
                        .authenticate();
                
                System.out.println("认证成功");
                
                try {
                    java.util.List<?> servers = osClient.compute().servers().list();
                    System.out.println("✓ 成功！实例数量: " + servers.size());
                } catch (Exception e) {
                    System.out.println("✗ 失败: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("✗ 认证失败: " + e.getMessage());
            }
            System.out.println();

            // 方式3: 使用 unscoped token 然后 re-scope
            System.out.println("测试3: 两步认证");
            try {
                // 第一步：获取 unscoped token
                OSClient.OSClientV3 unscopedClient = OSFactory.builderV3()
                        .endpoint(endpoint)
                        .credentials(username, password, Identifier.byName(domain))
                        .authenticate();
                
                System.out.println("Unscoped 认证成功");
                
                // 第二步：使用 token 进行 project scope
                OSClient.OSClientV3 scopedClient = OSFactory.builderV3()
                        .endpoint(endpoint)
                        .token(unscopedClient.getToken().getId())
                        .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                        .authenticate();
                
                System.out.println("Scoped 认证成功");
                
                try {
                    java.util.List<?> servers = scopedClient.compute().servers().list();
                    System.out.println("✓ 成功！实例数量: " + servers.size());
                } catch (Exception e) {
                    System.out.println("✗ 失败: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("✗ 认证失败: " + e.getMessage());
            }
            System.out.println();

            // 方式4: 使用 project ID 而不是 name
            System.out.println("测试4: 使用 Project ID");
            try {
                OSClient.OSClientV3 osClient = OSFactory.builderV3()
                        .endpoint(endpoint)
                        .credentials(username, password, Identifier.byName(domain))
                        .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                        .authenticate();
                
                String projectId = osClient.getToken().getProject().getId();
                System.out.println("Project ID: " + projectId);
                
                // 使用 project ID 重新认证
                OSClient.OSClientV3 osClient2 = OSFactory.builderV3()
                        .endpoint(endpoint)
                        .credentials(username, password, Identifier.byName(domain))
                        .scopeToProject(Identifier.byId(projectId), Identifier.byName(domain))
                        .authenticate();
                
                try {
                    java.util.List<?> servers = osClient2.compute().servers().list();
                    System.out.println("✓ 成功！实例数量: " + servers.size());
                } catch (Exception e) {
                    System.out.println("✗ 失败: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("总体失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}






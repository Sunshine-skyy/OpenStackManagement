package com.openstack.management.util;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.Server;
import org.openstack4j.openstack.OSFactory;

import java.util.List;

/**
 * Nova 服务专项测试工具
 */
public class NovaServiceTest {

    public static void main(String[] args) {
        String endpoint = "http://192.168.10.10:5000/v3";
        String username = "admin";
        String password = "admin";
        String domain = "Default";
        String project = "admin";

        System.out.println("=== Nova 服务专项测试 ===");
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Username: " + username);
        System.out.println("Domain: " + domain);
        System.out.println("Project: " + project);
        System.out.println();

        try {
            // 认证
            System.out.println("1. 正在认证...");
            OSClient.OSClientV3 osClient = OSFactory.builderV3()
                    .endpoint(endpoint)
                    .credentials(username, password, Identifier.byName(domain))
                    .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                    .authenticate();
            System.out.println("✓ 认证成功");
            System.out.println("Token ID: " + osClient.getToken().getId());
            System.out.println("User ID: " + osClient.getToken().getUser().getId());
            System.out.println("Project ID: " + osClient.getToken().getProject().getId());
            System.out.println();

            // 测试 Compute 服务是否存在
            System.out.println("2. 检查 Compute 服务...");
            try {
                org.openstack4j.api.compute.ComputeService compute = osClient.compute();
                System.out.println("✓ Compute 服务对象创建成功");
            } catch (Exception e) {
                System.out.println("✗ 无法创建 Compute 服务对象: " + e.getMessage());
                return;
            }
            System.out.println();

            // 测试方式1: 简单 list()
            System.out.println("3. 测试方式1: servers().list()");
            try {
                List<? extends Server> servers = osClient.compute().servers().list();
                System.out.println("✓ 成功！实例数量: " + servers.size());
                if (!servers.isEmpty()) {
                    Server server = servers.get(0);
                    System.out.println("  第一个实例:");
                    System.out.println("    名称: " + server.getName());
                    System.out.println("    ID: " + server.getId());
                    System.out.println("    状态: " + server.getStatus());
                }
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
                System.out.println("  错误类型: " + e.getClass().getName());
                e.printStackTrace();
            }
            System.out.println();

            // 测试方式2: list(false)
            System.out.println("4. 测试方式2: servers().list(false)");
            try {
                List<? extends Server> servers = osClient.compute().servers().list(false);
                System.out.println("✓ 成功！实例数量: " + servers.size());
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
            }
            System.out.println();

            // 测试方式3: list(true)
            System.out.println("5. 测试方式3: servers().list(true)");
            try {
                List<? extends Server> servers = osClient.compute().servers().list(true);
                System.out.println("✓ 成功！实例数量: " + servers.size());
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
            }
            System.out.println();

            // 测试方式4: 使用过滤参数
            System.out.println("6. 测试方式4: servers().list(filteringParams)");
            try {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                List<? extends Server> servers = osClient.compute().servers().list(params);
                System.out.println("✓ 成功！实例数量: " + servers.size());
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
            }
            System.out.println();

            // 测试 Flavors
            System.out.println("7. 测试 Flavors 列表...");
            try {
                java.util.List<? extends org.openstack4j.model.compute.Flavor> flavors = osClient.compute().flavors().list();
                System.out.println("✓ 成功！Flavor 数量: " + flavors.size());
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
            }
            System.out.println();

            // 测试 Images
            System.out.println("8. 测试 Images 列表...");
            try {
                java.util.List<? extends org.openstack4j.model.image.Image> images = osClient.images().list();
                System.out.println("✓ 成功！镜像数量: " + images.size());
            } catch (Exception e) {
                System.out.println("✗ 失败: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("✗ 认证失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


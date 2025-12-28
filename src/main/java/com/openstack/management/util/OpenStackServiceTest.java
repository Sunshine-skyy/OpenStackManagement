package com.openstack.management.util;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

/**
 * OpenStack 服务测试工具
 * 用于测试各个服务是否可用
 */
public class OpenStackServiceTest {

    public static void main(String[] args) {
        String endpoint = "http://192.168.10.10:5000/v3";
        String username = "admin";
        String password = "admin";
        String domain = "Default";
        String project = "admin";

        System.out.println("=== OpenStack 服务测试 ===");
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
            System.out.println();

            // 测试 Compute 服务
            System.out.println("2. 测试 Compute (Nova) 服务...");
            try {
                java.util.List<? extends org.openstack4j.model.compute.Server> servers = osClient.compute().servers().list();
                System.out.println("✓ Compute 服务可用");
                System.out.println("实例数量: " + servers.size());
                if (!servers.isEmpty()) {
                    System.out.println("第一个实例: " + servers.get(0).getName());
                }
            } catch (Exception e) {
                System.out.println("✗ Compute 服务失败: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();

            // 测试 Image 服务
            System.out.println("3. 测试 Image (Glance) 服务...");
            try {
                java.util.List<? extends org.openstack4j.model.image.Image> images = osClient.images().list();
                System.out.println("✓ Image 服务可用");
                System.out.println("镜像数量: " + images.size());
            } catch (Exception e) {
                System.out.println("✗ Image 服务失败: " + e.getMessage());
            }
            System.out.println();

            // 测试 Network 服务
            System.out.println("4. 测试 Network (Neutron) 服务...");
            try {
                java.util.List<? extends org.openstack4j.model.network.Network> networks = osClient.networking().network().list();
                System.out.println("✓ Network 服务可用");
                System.out.println("网络数量: " + networks.size());
            } catch (Exception e) {
                System.out.println("✗ Network 服务失败: " + e.getMessage());
            }
            System.out.println();

            // 测试 Object Storage 服务
            System.out.println("5. 测试 Object Storage (Swift) 服务...");
            try {
                java.util.List<? extends org.openstack4j.model.storage.object.SwiftContainer> containers = osClient.objectStorage().containers().list();
                System.out.println("✓ Object Storage 服务可用");
                System.out.println("容器数量: " + containers.size());
            } catch (Exception e) {
                System.out.println("✗ Object Storage 服务失败: " + e.getMessage());
            }
            System.out.println();

            // 测试 Identity 服务
            System.out.println("6. 测试 Identity (Keystone) 服务...");
            try {
                java.util.List<? extends org.openstack4j.model.identity.v3.Project> projects = osClient.identity().projects().list();
                System.out.println("✓ Identity 服务可用");
                System.out.println("项目数量: " + projects.size());
            } catch (Exception e) {
                System.out.println("✗ Identity 服务失败: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("✗ 认证失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


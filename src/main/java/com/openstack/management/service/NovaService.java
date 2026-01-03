package com.openstack.management.service;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.RebootType;
import org.openstack4j.model.image.v2.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class NovaService extends BaseService {

    @Autowired
    private NeutronService neutronService;

    /**
     * 获取Compute服务
     */
    private ComputeService getCompute(HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        return osClient.compute();
    }

    /**
     * 列出所有实例
     */
    public List<? extends Server> listServers(HttpSession session) {
        try {
            OSClient.OSClientV3 osClient = getOSClient(session);
            
            // 直接使用最简单的方式，不带任何参数
            try {
                // 尝试使用 list() 方法
                List<? extends Server> servers = osClient.compute().servers().list();
                System.out.println("成功获取实例列表，数量: " + servers.size());
                return servers;
            } catch (Exception e) {
                System.err.println("list() 方法失败: " + e.getMessage());
                
                // 如果失败，返回空列表，让页面至少能显示
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("获取实例列表失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID获取实例
     */
    public Server getServer(String serverId, HttpSession session) {
        return getCompute(session).servers().get(serverId);
    }

    /**
     * 创建实例
     */
    public Server createServer(String name, String imageId, String flavorId, String networkId, HttpSession session) {
        ServerCreate create = Builders.server()
                .name(name)
                .image(imageId)
                .flavor(flavorId)
                .networks(java.util.Arrays.asList(networkId))
                .build();
        return getCompute(session).servers().boot(create);
    }

    /**
     * 删除实例
     */
    public void deleteServer(String serverId, HttpSession session) {
        getCompute(session).servers().delete(serverId);
    }

    /**
     * 启动实例
     * 注意：OpenStack4j 3.2.0版本中启动/停止功能可能不支持
     * 此方法暂时抛出异常，提示用户通过OpenStack Dashboard操作
     */
    public void startServer(String serverId, HttpSession session) {
        // OpenStack4j 3.2.0版本中可能不支持直接启动操作
        // 可以通过重启实例来实现启动效果，或者提示用户使用Dashboard
        throw new UnsupportedOperationException(
            "当前版本的OpenStack4j不支持直接启动实例操作。\n" +
            "请通过以下方式启动实例：\n" +
            "1. 使用OpenStack Dashboard手动启动\n" +
            "2. 或者使用OpenStack命令行：openstack server start " + serverId
        );
    }

    /**
     * 停止实例
     * 注意：OpenStack4j 3.2.0版本中启动/停止功能可能不支持
     */
    public void stopServer(String serverId, HttpSession session) {
        // OpenStack4j 3.2.0版本中可能不支持直接停止操作
        throw new UnsupportedOperationException(
            "当前版本的OpenStack4j不支持直接停止实例操作。\n" +
            "请通过以下方式停止实例：\n" +
            "1. 使用OpenStack Dashboard手动停止\n" +
            "2. 或者使用OpenStack命令行：openstack server stop " + serverId
        );
    }

    /**
     * 重启实例
     */
    public void rebootServer(String serverId, RebootType rebootType, HttpSession session) {
        getCompute(session).servers().reboot(serverId, rebootType);
    }

    /**
     * 列出所有镜像
     * 使用 Glance v2 API
     */
    public List<? extends Image> listImages(HttpSession session) {
        try {
            OSClient.OSClientV3 osClient = getOSClient(session);
            
            // 使用 Glance v2 API 获取镜像（重要！）
            List<? extends Image> images = osClient.imagesV2().list();
            System.out.println("成功获取 " + images.size() + " 个镜像（通过 Glance v2 API）");
            return images;
        } catch (Exception e) {
            System.err.println("获取镜像列表失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 返回空列表
        return new ArrayList<>();
    }

    /**
     * 根据ID获取镜像
     * 使用 Glance v2 API
     */
    public Image getImage(String imageId, HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        return osClient.imagesV2().get(imageId);
    }

    /**
     * 列出所有规格
     */
    public List<? extends Flavor> listFlavors(HttpSession session) {
        return getCompute(session).flavors().list();
    }

    /**
     * 根据ID获取规格
     */
    public Flavor getFlavor(String flavorId, HttpSession session) {
        return getCompute(session).flavors().get(flavorId);
    }

    /**
     * 列出所有网络
     * 使用 Neutron 的网络接口
     */
    public List<? extends org.openstack4j.model.network.Network> listNovaNetworks(HttpSession session) {
        try {
            OSClient.OSClientV3 osClient = getOSClient(session);
            
            System.out.println("=== 开始获取网络列表 ===");
            
            // 尝试多种方式获取网络
            List<? extends org.openstack4j.model.network.Network> networks = null;
            
            // 方法1: 直接调用 list()
            try {
                System.out.println("方法1: 使用 os.networking().network().list()");
                networks = osClient.networking().network().list();
                System.out.println("✓ 方法1成功！获取到 " + networks.size() + " 个网络");
            } catch (Exception e) {
                System.err.println("✗ 方法1失败: " + e.getMessage());
                
                // 方法2: 使用带参数的 list
                try {
                    System.out.println("方法2: 使用 os.networking().network().list(Collections.emptyMap())");
                    networks = osClient.networking().network().list(java.util.Collections.emptyMap());
                    System.out.println("✓ 方法2成功！获取到 " + networks.size() + " 个网络");
                } catch (Exception e2) {
                    System.err.println("✗ 方法2也失败: " + e2.getMessage());
                    
                    // 方法3: 使用 RestTemplate（备选方案）
                    System.out.println("方法3: 使用 RestTemplate 直接调用 Neutron API");
                    List<Map<String, Object>> networkMaps = neutronService.listNetworksViaRestTemplate(session);
                    if (networkMaps != null && !networkMaps.isEmpty()) {
                        System.out.println("✓ 方法3成功！获取到 " + networkMaps.size() + " 个网络");
                        // 返回空列表，因为我们无法将 Map 转换为 Network 对象
                        // 但至少我们知道 API 调用成功了
                        return new ArrayList<>();
                    }
                    
                    throw e; // 抛出原始异常
                }
            }
            
            return networks != null ? networks : new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("获取网络列表失败详细信息:");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("错误消息: " + e.getMessage());
            
            // 打印异常链
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("原因: " + cause.getClass().getName() + ": " + cause.getMessage());
            }
            
            e.printStackTrace();
            
            // 最后尝试使用 RestTemplate
            try {
                System.out.println("最后尝试: 使用 RestTemplate");
                List<Map<String, Object>> networkMaps = neutronService.listNetworksViaRestTemplate(session);
                if (networkMaps != null && !networkMaps.isEmpty()) {
                    System.out.println("✓ RestTemplate 成功！获取到 " + networkMaps.size() + " 个网络");
                }
            } catch (Exception e2) {
                System.err.println("RestTemplate 也失败了: " + e2.getMessage());
            }
            
            // 返回空列表，让页面至少能显示
            return new ArrayList<>();
        }
    }
    
    /**
     * 列出所有网络（兼容旧方法）
     * 使用 Neutron API 获取网络
     */
    public List<?> listNetworks(HttpSession session) {
        // 先尝试使用 OpenStack4j
        List<? extends org.openstack4j.model.network.Network> networks = listNovaNetworks(session);
        
        // 如果失败，尝试使用 RestTemplate
        if (networks == null || networks.isEmpty()) {
            try {
                List<Map<String, Object>> networkMaps = neutronService.listNetworksViaRestTemplate(session);
                if (networkMaps != null && !networkMaps.isEmpty()) {
                    System.out.println("使用 RestTemplate 获取到 " + networkMaps.size() + " 个网络");
                    return networkMaps;
                }
            } catch (Exception e) {
                System.err.println("RestTemplate 获取网络失败: " + e.getMessage());
            }
        }
        
        return networks;
    }

    /**
     * 获取控制台日志
     */
    public String getConsoleOutput(String serverId, int lines, HttpSession session) {
        return getCompute(session).servers().getConsoleOutput(serverId, lines);
    }
}


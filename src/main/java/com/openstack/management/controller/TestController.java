package com.openstack.management.controller;

import com.openstack.management.model.UserSession;
import com.openstack.management.util.SessionUtil;
import com.openstack.management.service.NeutronService;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Token;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.network.Network;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private NeutronService neutronService;

    /**
     * 测试 Glance API
     */
    @GetMapping("/glance")
    public Map<String, Object> testGlance(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        UserSession userSession = SessionUtil.getUserSession(session);
        
        if (userSession == null) {
            result.put("success", false);
            result.put("error", "用户未登录");
            return result;
        }

        try {
            System.out.println("=== 开始测试 Glance API ===");
            
            // 创建 OSClient
            OSClient.OSClientV3 os = OSFactory.builderV3()
                    .endpoint(userSession.getEndpoint())
                    .credentials(userSession.getUsername(), userSession.getPassword(), 
                               Identifier.byName(userSession.getDomain()))
                    .scopeToProject(Identifier.byName(userSession.getProject()), 
                                  Identifier.byName(userSession.getDomain()))
                    .authenticate();
            
            System.out.println("认证成功");
            
            // 获取 Token 信息
            Token token = os.getToken();
            System.out.println("Token ID: " + token.getId());
            
            // 打印 Service Catalog 中的 Glance endpoint
            System.out.println("\n=== Service Catalog - Glance ===");
            token.getCatalog().forEach(service -> {
                if ("image".equals(service.getType())) {
                    System.out.println("Service Type: " + service.getType());
                    System.out.println("Service Name: " + service.getName());
                    service.getEndpoints().forEach(endpoint -> {
                        System.out.println("  Interface: " + endpoint.getIface());
                        System.out.println("  URL: " + endpoint.getUrl());
                        System.out.println("  Region: " + endpoint.getRegion());
                    });
                }
            });
            
            // 尝试获取镜像列表 - 使用 Glance v2 API
            System.out.println("\n=== 尝试获取镜像列表 (使用 Glance v2 API) ===");
            List<? extends Image> images = os.imagesV2().list();
            
            System.out.println("成功获取 " + images.size() + " 个镜像");
            
            List<Map<String, Object>> imageList = new ArrayList<>();
            for (Image image : images) {
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("id", image.getId());
                imageInfo.put("name", image.getName());
                imageInfo.put("status", image.getStatus());
                imageInfo.put("size", image.getSize());
                imageList.add(imageInfo);
                System.out.println("镜像: " + image.getName() + " (ID: " + image.getId() + ")");
            }
            
            result.put("success", true);
            result.put("count", images.size());
            result.put("images", imageList);
            
        } catch (Exception e) {
            System.err.println("测试 Glance API 失败:");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("错误消息: " + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("exceptionType", e.getClass().getName());
            
            // 获取详细的堆栈信息
            List<String> stackTrace = new ArrayList<>();
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace.add(element.toString());
                if (stackTrace.size() >= 10) break; // 只取前10行
            }
            result.put("stackTrace", stackTrace);
        }
        
        return result;
    }

    /**
     * 测试 Neutron API
     */
    @GetMapping("/neutron")
    public Map<String, Object> testNeutron(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        UserSession userSession = SessionUtil.getUserSession(session);
        
        if (userSession == null) {
            result.put("success", false);
            result.put("error", "用户未登录");
            return result;
        }

        try {
            System.out.println("=== 开始测试 Neutron API ===");
            
            // 创建 OSClient
            OSClient.OSClientV3 os = OSFactory.builderV3()
                    .endpoint(userSession.getEndpoint())
                    .credentials(userSession.getUsername(), userSession.getPassword(), 
                               Identifier.byName(userSession.getDomain()))
                    .scopeToProject(Identifier.byName(userSession.getProject()), 
                                  Identifier.byName(userSession.getDomain()))
                    .authenticate();
            
            System.out.println("认证成功");
            
            // 获取 Token 信息
            Token token = os.getToken();
            System.out.println("Token ID: " + token.getId());
            System.out.println("Project ID: " + token.getProject().getId());
            System.out.println("User ID: " + token.getUser().getId());
            
            // 打印 Service Catalog 中的 Neutron endpoint
            System.out.println("\n=== Service Catalog - Neutron ===");
            token.getCatalog().forEach(service -> {
                if ("network".equals(service.getType())) {
                    System.out.println("Service Type: " + service.getType());
                    System.out.println("Service Name: " + service.getName());
                    service.getEndpoints().forEach(endpoint -> {
                        System.out.println("  Interface: " + endpoint.getIface());
                        System.out.println("  URL: " + endpoint.getUrl());
                        System.out.println("  Region: " + endpoint.getRegion());
                    });
                }
            });
            
            // 尝试多种方式获取网络列表
            System.out.println("\n=== 尝试获取网络列表 ===");
            
            List<? extends Network> networks = null;
            Exception lastException = null;
            
            // 方法1: 直接调用 list()
            try {
                System.out.println("方法1: 使用 os.networking().network().list()");
                networks = os.networking().network().list();
                System.out.println("✓ 方法1成功！获取到 " + networks.size() + " 个网络");
            } catch (Exception e) {
                System.err.println("✗ 方法1失败: " + e.getMessage());
                lastException = e;
                
                // 方法2: 使用带参数的 list
                try {
                    System.out.println("方法2: 使用 os.networking().network().list(Collections.emptyMap())");
                    networks = os.networking().network().list(Collections.emptyMap());
                    System.out.println("✓ 方法2成功！获取到 " + networks.size() + " 个网络");
                } catch (Exception e2) {
                    System.err.println("✗ 方法2失败: " + e2.getMessage());
                    lastException = e2;
                }
            }
            
            if (networks != null && !networks.isEmpty()) {
                System.out.println("成功获取 " + networks.size() + " 个网络");
                
                List<Map<String, Object>> networkList = new ArrayList<>();
                for (Network network : networks) {
                    Map<String, Object> networkInfo = new HashMap<>();
                    networkInfo.put("id", network.getId());
                    networkInfo.put("name", network.getName());
                    networkInfo.put("status", network.getStatus());
                    networkInfo.put("adminStateUp", network.isAdminStateUp());
                    networkInfo.put("shared", network.isShared());
                    networkInfo.put("tenantId", network.getTenantId());
                    networkList.add(networkInfo);
                    System.out.println("网络: " + network.getName() + " (ID: " + network.getId() + ")");
                }
                
                result.put("success", true);
                result.put("count", networks.size());
                result.put("networks", networkList);
            } else if (lastException != null) {
                throw lastException;
            } else {
                result.put("success", true);
                result.put("count", 0);
                result.put("networks", new ArrayList<>());
                result.put("message", "没有找到任何网络");
            }
            
        } catch (Exception e) {
            System.err.println("测试 Neutron API 失败:");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("错误消息: " + e.getMessage());
            
            // 打印完整的异常链
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("原因: " + cause.getClass().getName() + ": " + cause.getMessage());
            }
            
            e.printStackTrace();
            
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("exceptionType", e.getClass().getName());
            
            if (cause != null) {
                result.put("causeType", cause.getClass().getName());
                result.put("causeMessage", cause.getMessage());
            }
            
            // 获取详细的堆栈信息
            List<String> stackTrace = new ArrayList<>();
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace.add(element.toString());
                if (stackTrace.size() >= 10) break;
            }
            result.put("stackTrace", stackTrace);
        }
        
        return result;
    }

    /**
     * 测试 Neutron API（使用 RestTemplate）
     */
    @GetMapping("/neutron-rest")
    public Map<String, Object> testNeutronRest(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        UserSession userSession = SessionUtil.getUserSession(session);
        
        if (userSession == null) {
            result.put("success", false);
            result.put("error", "用户未登录");
            return result;
        }

        try {
            System.out.println("=== 开始测试 Neutron API (RestTemplate) ===");
            
            List<Map<String, Object>> networks = neutronService.listNetworksViaRestTemplate(session);
            
            if (networks != null && !networks.isEmpty()) {
                System.out.println("成功获取 " + networks.size() + " 个网络");
                
                result.put("success", true);
                result.put("count", networks.size());
                result.put("networks", networks);
            } else {
                result.put("success", true);
                result.put("count", 0);
                result.put("networks", new ArrayList<>());
                result.put("message", "没有找到任何网络");
            }
            
        } catch (Exception e) {
            System.err.println("测试 Neutron API (RestTemplate) 失败:");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("错误消息: " + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("exceptionType", e.getClass().getName());
        }
        
        return result;
    }

    /**
     * 测试所有 Endpoints
     */
    @GetMapping("/endpoints")
    public Map<String, Object> testEndpoints(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        UserSession userSession = SessionUtil.getUserSession(session);
        
        if (userSession == null) {
            result.put("success", false);
            result.put("error", "用户未登录");
            return result;
        }

        try {
            System.out.println("=== 开始测试所有 Endpoints ===");
            
            OSClient.OSClientV3 os = OSFactory.builderV3()
                    .endpoint(userSession.getEndpoint())
                    .credentials(userSession.getUsername(), userSession.getPassword(), 
                               Identifier.byName(userSession.getDomain()))
                    .scopeToProject(Identifier.byName(userSession.getProject()), 
                                  Identifier.byName(userSession.getDomain()))
                    .authenticate();
            
            Token token = os.getToken();
            
            List<Map<String, Object>> services = new ArrayList<>();
            
            token.getCatalog().forEach(service -> {
                Map<String, Object> serviceInfo = new HashMap<>();
                serviceInfo.put("type", service.getType());
                serviceInfo.put("name", service.getName());
                
                List<Map<String, String>> endpoints = new ArrayList<>();
                service.getEndpoints().forEach(endpoint -> {
                    Map<String, String> endpointInfo = new HashMap<>();
                    endpointInfo.put("interface", endpoint.getIface().toString());
                    endpointInfo.put("url", endpoint.getUrl().toString());
                    endpointInfo.put("region", endpoint.getRegion());
                    endpoints.add(endpointInfo);
                });
                
                serviceInfo.put("endpoints", endpoints);
                services.add(serviceInfo);
                
                System.out.println("Service: " + service.getType() + " (" + service.getName() + ")");
                service.getEndpoints().forEach(endpoint -> {
                    System.out.println("  " + endpoint.getIface() + ": " + endpoint.getUrl());
                });
            });
            
            result.put("success", true);
            result.put("services", services);
            
        } catch (Exception e) {
            System.err.println("测试 Endpoints 失败: " + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}


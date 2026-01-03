package com.openstack.management.service;

import com.openstack.management.model.UserSession;
import com.openstack.management.util.SessionUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Neutron 网络服务
 * 直接调用 Neutron REST API
 */
@Service
public class NeutronService extends BaseService {

    /**
     * 使用 RestTemplate 直接调用 Neutron API
     */
    public List<Map<String, Object>> listNetworksViaRestTemplate(HttpSession session) {
        UserSession userSession = SessionUtil.getUserSession(session);
        if (userSession == null) {
            throw new RuntimeException("用户未登录");
        }

        try {
            // 获取 token
            org.openstack4j.api.OSClient.OSClientV3 osClient = getOSClient(session);
            String token = osClient.getToken().getId();
            
            // 构建请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", token);
            headers.set("Accept", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 调用 Neutron API
            String neutronUrl = "http://controller:9696/v2.0/networks";
            System.out.println("调用 Neutron API: " + neutronUrl);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                neutronUrl,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> networks = (List<Map<String, Object>>) body.get("networks");
                System.out.println("成功获取 " + (networks != null ? networks.size() : 0) + " 个网络（通过 RestTemplate）");
                return networks != null ? networks : new ArrayList<>();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("通过 RestTemplate 获取网络失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}






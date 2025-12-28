package com.openstack.management.service;

import com.openstack.management.model.UserSession;
import com.openstack.management.util.SessionUtil;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

import javax.servlet.http.HttpSession;

public abstract class BaseService {
    
    protected OSClient.OSClientV3 getOSClient(HttpSession session) {
        UserSession userSession = SessionUtil.getUserSession(session);
        if (userSession == null) {
            throw new RuntimeException("用户未登录");
        }
        
        try {
            // 每次使用认证信息重新创建 OSClient
            return OSFactory.builderV3()
                    .endpoint(userSession.getEndpoint())
                    .credentials(userSession.getUsername(), userSession.getPassword(), 
                               Identifier.byName(userSession.getDomain()))
                    .scopeToProject(Identifier.byName(userSession.getProject()), 
                                  Identifier.byName(userSession.getDomain()))
                    .authenticate();
        } catch (Exception e) {
            System.err.println("认证失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("OpenStack 认证失败: " + e.getMessage(), e);
        }
    }
}



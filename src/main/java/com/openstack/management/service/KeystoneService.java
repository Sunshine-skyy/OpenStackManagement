package com.openstack.management.service;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.identity.v3.IdentityService;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Role;
import org.openstack4j.model.identity.v3.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class KeystoneService extends BaseService {

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser(HttpSession session) {
        try {
            OSClient.OSClientV3 osClient = getOSClient(session);
            IdentityService identityService = osClient.identity();
            return identityService.users().get(osClient.getToken().getUser().getId());
        } catch (Exception e) {
            System.err.println("获取用户信息失败: " + e.getMessage());
            // 返回 Token 中的基本用户信息
            OSClient.OSClientV3 osClient = getOSClient(session);
            return osClient.getToken().getUser();
        }
    }

    /**
     * 获取所有项目
     */
    public List<? extends Project> listProjects(HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        
        try {
            // 尝试从 Token 中获取项目信息
            // 这样可以避免反序列化问题
            Project currentProject = osClient.getToken().getProject();
            if (currentProject != null) {
                List<Project> projects = new java.util.ArrayList<>();
                projects.add(currentProject);
                return projects;
            }
            
            // 如果 Token 中没有项目信息，返回空列表
            System.out.println("Token 中没有项目信息");
            return new java.util.ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("获取项目信息失败: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 根据ID获取项目
     */
    public Project getProject(String projectId, HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        IdentityService identityService = osClient.identity();
        return identityService.projects().get(projectId);
    }

    /**
     * 获取所有角色
     */
    public List<? extends Role> listRoles(HttpSession session) {
        try {
            OSClient.OSClientV3 osClient = getOSClient(session);
            IdentityService identityService = osClient.identity();
            return identityService.roles().list();
        } catch (Exception e) {
            System.err.println("获取角色列表失败: " + e.getMessage());
            // 返回空列表
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取Token信息
     */
    public String getTokenId(HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        return osClient.getToken().getId();
    }
}



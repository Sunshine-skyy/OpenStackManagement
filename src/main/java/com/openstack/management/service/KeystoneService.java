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
        OSClient.OSClientV3 osClient = getOSClient(session);
        IdentityService identityService = osClient.identity();
        return identityService.users().get(osClient.getToken().getUser().getId());
    }

    /**
     * 获取所有项目
     */
    public List<? extends Project> listProjects(HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        IdentityService identityService = osClient.identity();
        return identityService.projects().list();
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
        OSClient.OSClientV3 osClient = getOSClient(session);
        IdentityService identityService = osClient.identity();
        return identityService.roles().list();
    }

    /**
     * 获取Token信息
     */
    public String getTokenId(HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        return osClient.getToken().getId();
    }
}



package com.openstack.management.controller;

import com.openstack.management.model.ApiResponse;
import com.openstack.management.model.UserSession;
import com.openstack.management.service.KeystoneService;
import com.openstack.management.util.SessionUtil;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Role;
import org.openstack4j.model.identity.v3.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息控制器
 * 用于查询和展示 Keystone 身份信息
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private KeystoneService keystoneService;

    /**
     * 用户信息页面
     */
    @GetMapping("/info")
    public String userInfoPage(HttpSession session, Model model) {
        UserSession userSession = SessionUtil.getUserSession(session);
        if (userSession == null) {
            return "redirect:/login";
        }

        try {
            // 获取当前用户信息
            User currentUser = keystoneService.getCurrentUser(session);
            
            // 获取项目列表
            List<? extends Project> projects = keystoneService.listProjects(session);
            
            // 获取角色列表
            List<? extends Role> roles = keystoneService.listRoles(session);
            
            // 获取 Token ID
            String tokenId = keystoneService.getTokenId(session);

            model.addAttribute("currentUser", currentUser);
            model.addAttribute("projects", projects);
            model.addAttribute("roles", roles);
            model.addAttribute("tokenId", tokenId);
            model.addAttribute("username", userSession.getUsername());

            return "user-info";
        } catch (Exception e) {
            model.addAttribute("error", "获取用户信息失败: " + e.getMessage());
            model.addAttribute("username", userSession.getUsername());
            return "user-info";
        }
    }

    /**
     * API: 获取当前用户信息
     */
    @GetMapping("/api/current")
    @ResponseBody
    public ApiResponse getCurrentUser(HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            User user = keystoneService.getCurrentUser(session);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * API: 获取项目列表
     */
    @GetMapping("/api/projects")
    @ResponseBody
    public ApiResponse listProjects(HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            List<? extends Project> projects = keystoneService.listProjects(session);
            return ApiResponse.success(projects);
        } catch (Exception e) {
            return ApiResponse.error("获取项目列表失败: " + e.getMessage());
        }
    }

    /**
     * API: 获取角色列表
     */
    @GetMapping("/api/roles")
    @ResponseBody
    public ApiResponse listRoles(HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            List<? extends Role> roles = keystoneService.listRoles(session);
            return ApiResponse.success(roles);
        } catch (Exception e) {
            return ApiResponse.error("获取角色列表失败: " + e.getMessage());
        }
    }

    /**
     * API: 获取所有身份信息（综合接口）
     */
    @GetMapping("/api/identity-info")
    @ResponseBody
    public ApiResponse getIdentityInfo(HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            Map<String, Object> identityInfo = new HashMap<>();
            
            // 获取当前用户
            User currentUser = keystoneService.getCurrentUser(session);
            identityInfo.put("currentUser", currentUser);
            
            // 获取项目列表
            List<? extends Project> projects = keystoneService.listProjects(session);
            identityInfo.put("projects", projects);
            identityInfo.put("projectCount", projects.size());
            
            // 获取角色列表
            List<? extends Role> roles = keystoneService.listRoles(session);
            identityInfo.put("roles", roles);
            identityInfo.put("roleCount", roles.size());
            
            // 获取 Token
            String tokenId = keystoneService.getTokenId(session);
            identityInfo.put("tokenId", tokenId);

            return ApiResponse.success(identityInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取身份信息失败: " + e.getMessage());
        }
    }
}


package com.openstack.management.controller;

import com.openstack.management.config.OpenStackConfig;
import com.openstack.management.model.ApiResponse;
import com.openstack.management.model.UserSession;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Value("${openstack.endpoint}")
    private String endpoint;

    @Value("${openstack.domain}")
    private String domain;

    @Value("${openstack.project}")
    private String project;

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 处理登录
     */
    @PostMapping("/login")
    @ResponseBody
    public ApiResponse login(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session) {
        try {
            // 使用更明确的认证方式，指定用户的 Domain
            OSClient.OSClientV3 osClient = OSFactory.builderV3()
                    .endpoint(endpoint)
                    .credentials(username, password, Identifier.byName(domain))
                    .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                    .authenticate();

            // 保存认证信息到Session（不保存 OSClient 对象）
            UserSession userSession = new UserSession();
            userSession.setUsername(username);
            userSession.setPassword(password);
            userSession.setToken(osClient.getToken().getId());
            userSession.setEndpoint(endpoint);
            userSession.setDomain(domain);
            userSession.setProject(project);
            session.setAttribute("userSession", userSession);

            return ApiResponse.success("登录成功");
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息到控制台
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = e.getClass().getSimpleName();
            }
            return ApiResponse.error("登录失败: " + errorMsg);
        }
    }

    /**
     * 登出
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}



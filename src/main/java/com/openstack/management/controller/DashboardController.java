package com.openstack.management.controller;

import com.openstack.management.model.UserSession;
import com.openstack.management.service.KeystoneService;
import com.openstack.management.service.NovaService;
import com.openstack.management.service.SwiftService;
import com.openstack.management.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    private KeystoneService keystoneService;

    @Autowired
    private NovaService novaService;

    @Autowired
    private SwiftService swiftService;

    /**
     * 主页Dashboard
     */
    @GetMapping("/")
    public String dashboard(HttpSession session, Model model) {
        UserSession userSession = SessionUtil.getUserSession(session);
        if (userSession == null) {
            return "redirect:/login";
        }

        try {
            // 统计数据
            int instanceCount = novaService.listServers(session).size();
            int containerCount = swiftService.listContainers(session).size();
            int imageCount = novaService.listImages(session).size();

            model.addAttribute("instanceCount", instanceCount);
            model.addAttribute("containerCount", containerCount);
            model.addAttribute("imageCount", imageCount);
            model.addAttribute("username", userSession.getUsername());
            
            // 添加项目信息
            model.addAttribute("projectName", userSession.getProject());

            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "获取数据失败: " + e.getMessage());
            model.addAttribute("username", userSession.getUsername());
            model.addAttribute("projectName", userSession.getProject());
            return "dashboard";
        }
    }
}



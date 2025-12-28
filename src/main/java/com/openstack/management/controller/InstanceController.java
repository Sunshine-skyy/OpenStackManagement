package com.openstack.management.controller;

import com.openstack.management.model.ApiResponse;
import com.openstack.management.model.UserSession;
import com.openstack.management.service.NovaService;
import com.openstack.management.util.SessionUtil;
import org.openstack4j.model.compute.RebootType;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/instances")
public class InstanceController {

    @Autowired
    private NovaService novaService;

    /**
     * 实例列表页面
     */
    @GetMapping
    public String listInstances(HttpSession session, Model model) {
        UserSession userSession = SessionUtil.getUserSession(session);
        if (userSession == null) {
            return "redirect:/login";
        }

        try {
            // 分别获取各个资源，即使某个失败也不影响其他
            List<? extends Server> servers = new ArrayList<>();
            List<? extends Image> images = new ArrayList<>();
            List<? extends Flavor> flavors = new ArrayList<>();
            List<? extends Network> networks = new ArrayList<>();
            
            StringBuilder errorMessages = new StringBuilder();
            
            // 获取实例列表
            try {
                servers = novaService.listServers(session);
                System.out.println("成功获取 " + servers.size() + " 个实例");
            } catch (Exception e) {
                errorMessages.append("获取实例列表失败: ").append(e.getMessage()).append("; ");
                System.err.println("获取实例失败: " + e.getMessage());
            }
            
            // 获取镜像列表
            try {
                images = novaService.listImages(session);
                System.out.println("成功获取 " + images.size() + " 个镜像");
            } catch (Exception e) {
                errorMessages.append("获取镜像列表失败: ").append(e.getMessage()).append("; ");
                System.err.println("获取镜像失败: " + e.getMessage());
            }
            
            // 获取规格列表
            try {
                flavors = novaService.listFlavors(session);
                System.out.println("成功获取 " + flavors.size() + " 个规格");
            } catch (Exception e) {
                errorMessages.append("获取规格列表失败: ").append(e.getMessage()).append("; ");
                System.err.println("获取规格失败: " + e.getMessage());
            }
            
            // 获取网络列表
            try {
                List<?> networkList = novaService.listNetworks(session);
                networks = (List<? extends Network>) networkList;
                System.out.println("成功获取 " + networks.size() + " 个网络");
            } catch (Exception e) {
                errorMessages.append("获取网络列表失败: ").append(e.getMessage()).append("; ");
                System.err.println("获取网络失败: " + e.getMessage());
            }

            model.addAttribute("servers", servers);
            model.addAttribute("images", images);
            model.addAttribute("flavors", flavors);
            model.addAttribute("networks", networks);
            model.addAttribute("username", userSession.getUsername());
            
            if (errorMessages.length() > 0) {
                model.addAttribute("error", errorMessages.toString());
            }

            return "instances";
        } catch (Exception e) {
            model.addAttribute("error", "获取数据失败: " + e.getMessage());
            model.addAttribute("username", userSession.getUsername());
            model.addAttribute("servers", new ArrayList<>());
            model.addAttribute("images", new ArrayList<>());
            model.addAttribute("flavors", new ArrayList<>());
            model.addAttribute("networks", new ArrayList<>());
            return "instances";
        }
    }

    /**
     * 创建实例
     */
    @PostMapping("/create")
    @ResponseBody
    public ApiResponse createInstance(@RequestParam String name,
                                      @RequestParam String imageId,
                                      @RequestParam String flavorId,
                                      @RequestParam String networkId,
                                      HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            Server server = novaService.createServer(name, imageId, flavorId, networkId, session);
            return ApiResponse.success("实例创建成功", server);
        } catch (Exception e) {
            return ApiResponse.error("创建实例失败: " + e.getMessage());
        }
    }

    /**
     * 删除实例
     */
    @DeleteMapping("/{serverId}")
    @ResponseBody
    public ApiResponse deleteInstance(@PathVariable String serverId, HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            novaService.deleteServer(serverId, session);
            return ApiResponse.success("实例删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除实例失败: " + e.getMessage());
        }
    }

    /**
     * 启动实例
     */
    @PostMapping("/{serverId}/start")
    @ResponseBody
    public ApiResponse startInstance(@PathVariable String serverId, HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            novaService.startServer(serverId, session);
            return ApiResponse.success("实例启动成功");
        } catch (Exception e) {
            return ApiResponse.error("启动实例失败: " + e.getMessage());
        }
    }

    /**
     * 停止实例
     */
    @PostMapping("/{serverId}/stop")
    @ResponseBody
    public ApiResponse stopInstance(@PathVariable String serverId, HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            novaService.stopServer(serverId, session);
            return ApiResponse.success("实例停止成功");
        } catch (Exception e) {
            return ApiResponse.error("停止实例失败: " + e.getMessage());
        }
    }

    /**
     * 重启实例
     */
    @PostMapping("/{serverId}/reboot")
    @ResponseBody
    public ApiResponse rebootInstance(@PathVariable String serverId,
                                     @RequestParam(defaultValue = "SOFT") String type,
                                     HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            RebootType rebootType = "HARD".equals(type) ? RebootType.HARD : RebootType.SOFT;
            novaService.rebootServer(serverId, rebootType, session);
            return ApiResponse.success("实例重启成功");
        } catch (Exception e) {
            return ApiResponse.error("重启实例失败: " + e.getMessage());
        }
    }

    /**
     * 获取实例详情
     */
    @GetMapping("/{serverId}/detail")
    @ResponseBody
    public ApiResponse getInstanceDetail(@PathVariable String serverId, HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            Server server = novaService.getServer(serverId, session);
            return ApiResponse.success(server);
        } catch (Exception e) {
            return ApiResponse.error("获取实例详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取控制台日志
     */
    @GetMapping("/{serverId}/console")
    @ResponseBody
    public ApiResponse getConsoleOutput(@PathVariable String serverId,
                                       @RequestParam(defaultValue = "50") int lines,
                                       HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            String output = novaService.getConsoleOutput(serverId, lines, session);
            return ApiResponse.success(output);
        } catch (Exception e) {
            return ApiResponse.error("获取控制台日志失败: " + e.getMessage());
        }
    }
}



package com.openstack.management.controller;

import com.openstack.management.model.ApiResponse;
import com.openstack.management.model.UserSession;
import com.openstack.management.service.SwiftService;
import com.openstack.management.util.SessionUtil;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private SwiftService swiftService;

    /**
     * 存储管理页面
     */
    @GetMapping
    public String storagePage(HttpSession session, Model model) {
        UserSession userSession = SessionUtil.getUserSession(session);
        if (userSession == null) {
            return "redirect:/login";
        }

        try {
            List<? extends SwiftContainer> containers = swiftService.listContainers(session);
            model.addAttribute("containers", containers);
            model.addAttribute("username", userSession.getUsername());
            return "storage";
        } catch (Exception e) {
            model.addAttribute("error", "获取容器列表失败: " + e.getMessage());
            model.addAttribute("username", userSession.getUsername());
            return "storage";
        }
    }

    /**
     * 创建容器
     */
    @PostMapping("/containers")
    @ResponseBody
    public ApiResponse createContainer(@RequestParam String containerName, HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            boolean success = swiftService.createContainer(containerName, session);
            if (success) {
                return ApiResponse.success("容器创建成功");
            } else {
                return ApiResponse.error("容器创建失败：无法验证容器是否创建成功");
            }
        } catch (Exception e) {
            return ApiResponse.error("创建容器失败: " + e.getMessage());
        }
    }

    /**
     * 删除容器
     */
    @DeleteMapping("/containers/{containerName}")
    @ResponseBody
    public ApiResponse deleteContainer(@PathVariable String containerName, HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            swiftService.deleteContainer(containerName, session);
            return ApiResponse.success("容器删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除容器失败: " + e.getMessage());
        }
    }

    /**
     * 列出容器内的对象
     */
    @GetMapping("/containers/{containerName}/objects")
    @ResponseBody
    public ApiResponse listObjects(@PathVariable String containerName, HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            List<? extends SwiftObject> objects = swiftService.listObjects(containerName, session);
            return ApiResponse.success(objects);
        } catch (Exception e) {
            return ApiResponse.error("获取对象列表失败: " + e.getMessage());
        }
    }

    /**
     * 上传对象
     */
    @PostMapping("/containers/{containerName}/objects")
    @ResponseBody
    public ApiResponse uploadObject(@PathVariable String containerName,
                                   @RequestParam("file") MultipartFile file,
                                   HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            swiftService.uploadObject(containerName, file.getOriginalFilename(), file, session);
            return ApiResponse.success("文件上传成功");
        } catch (Exception e) {
            return ApiResponse.error("上传文件失败: " + e.getMessage());
        }
    }

    /**
     * 下载对象
     */
    @GetMapping("/containers/{containerName}/objects/{objectName}/download")
    public ResponseEntity<InputStreamResource> downloadObject(@PathVariable String containerName,
                                                              @PathVariable String objectName,
                                                              HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            InputStream inputStream = swiftService.downloadObject(containerName, objectName, session);
            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除对象
     */
    @DeleteMapping("/containers/{containerName}/objects/{objectName}")
    @ResponseBody
    public ApiResponse deleteObject(@PathVariable String containerName,
                                   @PathVariable String objectName,
                                   HttpSession session) {
        if (SessionUtil.getUserSession(session) == null) {
            return ApiResponse.error("请先登录");
        }

        try {
            swiftService.deleteObject(containerName, objectName, session);
            return ApiResponse.success("文件删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除文件失败: " + e.getMessage());
        }
    }
}



package com.openstack.management.service;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.ObjectStorageService;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class SwiftService extends BaseService {

    /**
     * 获取ObjectStorage服务
     */
    private ObjectStorageService getObjectStorage(HttpSession session) {
        OSClient.OSClientV3 osClient = getOSClient(session);
        return osClient.objectStorage();
    }

    /**
     * 列出所有容器
     */
    public List<? extends SwiftContainer> listContainers(HttpSession session) {
        try {
            List<? extends SwiftContainer> containers = getObjectStorage(session).containers().list();
            System.out.println("成功获取 " + containers.size() + " 个容器");
            return containers;
        } catch (Exception e) {
            System.err.println("获取容器列表失败: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 创建容器
     */
    public boolean createContainer(String containerName, HttpSession session) {
        try {
            System.out.println("开始创建容器: " + containerName);
            
            // 创建容器
            getObjectStorage(session).containers().create(containerName);
            
            // 验证容器是否创建成功
            List<? extends SwiftContainer> containers = getObjectStorage(session).containers().list();
            boolean exists = containers.stream().anyMatch(c -> c.getName().equals(containerName));
            
            if (exists) {
                System.out.println("容器创建成功: " + containerName);
                return true;
            } else {
                System.err.println("容器创建失败: 在列表中未找到容器 " + containerName);
                return false;
            }
        } catch (Exception e) {
            System.err.println("创建容器失败: " + containerName);
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("错误消息: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除容器
     */
    public void deleteContainer(String containerName, HttpSession session) {
        getObjectStorage(session).containers().delete(containerName);
    }

    /**
     * 列出容器内的对象
     */
    public List<? extends SwiftObject> listObjects(String containerName, HttpSession session) {
        return getObjectStorage(session).objects().list(containerName);
    }

    /**
     * 上传对象
     */
    public void uploadObject(String containerName, String objectName, MultipartFile file, HttpSession session) throws Exception {
        InputStream inputStream = file.getInputStream();
        // 使用 Payloads 工具类将 InputStream 转换为 Payload
        getObjectStorage(session).objects().put(containerName, objectName, Payloads.create(inputStream));
    }

    /**
     * 下载对象
     */
    public InputStream downloadObject(String containerName, String objectName, HttpSession session) {
        return getObjectStorage(session).objects().download(containerName, objectName).getInputStream();
    }

    /**
     * 删除对象
     */
    public void deleteObject(String containerName, String objectName, HttpSession session) {
        getObjectStorage(session).objects().delete(containerName, objectName);
    }

    /**
     * 获取容器元数据
     */
    public Map<String, String> getContainerMetadata(String containerName, HttpSession session) {
        // 使用 list() 方法获取所有容器，然后找到指定的容器
        List<? extends SwiftContainer> containers = getObjectStorage(session).containers().list();
        for (SwiftContainer container : containers) {
            if (container.getName().equals(containerName)) {
                return container.getMetadata();
            }
        }
        return null;
    }
}



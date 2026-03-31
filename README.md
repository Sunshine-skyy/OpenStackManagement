# OpenStack 管理系统

基于 Spring Boot 和 OpenStack4j 的 OpenStack 资源管理系统。

## 📋 项目简介

这是一个用于管理 OpenStack 云平台资源的 Web 应用程序，提供了友好的用户界面来管理虚拟机实例、存储等资源。

## ✨ 主要功能

- ✅ 用户认证与登录
- ✅ 实例管理
  - 查看实例列表
  - 创建新实例
  - 启动/停止/重启实例
  - 删除实例
  - 查看实例详情
- ✅ 存储管理
  - 查看容器列表
  - 上传文件
  - 下载文件
  - 删除文件
- ✅ 仪表板概览

## 🛠️ 技术栈

- **后端框架**: Spring Boot 2.7.18
- **OpenStack SDK**: OpenStack4j 3.2.0
- **模板引擎**: Thymeleaf
- **前端框架**: Bootstrap 5.3.0
- **构建工具**: Maven
- **Java 版本**: 17

## 📦 项目结构

```
OpenStackManagement/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/openstack/management/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── model/           # 数据模型
│   │   │       ├── service/         # 服务层
│   │   │       └── util/            # 工具类
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf 模板
│   │       ├── static/              # 静态资源
│   │       └── application.properties
│   └── test/                        # 测试代码
├── pom.xml                          # Maven 配置
├── README.md                        # 项目说明
├── Git使用指南.md                   # Git 使用文档
├── 修复说明.md                      # 问题修复记录
└── 问题分析.md                      # 问题分析文档
```

## 🚀 快速开始

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+
- OpenStack 环境（支持 Keystone V3）

### 配置

1. 克隆项目
```bash
git clone <your-repository-url>
cd OpenStackManagement
```

2. 修改配置文件 `src/main/resources/application.properties`
```properties
server.port=8080

# OpenStack 配置
openstack.auth.url=http://your-openstack-controller:5000/v3
openstack.auth.domain=default
openstack.auth.project=admin
```

### 运行

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

访问 http://localhost:8080

### 默认登录

- 用户名: admin
- 密码: admin
- 项目: admin

## 📝 最近更新

### v1.0.0 (2025-12-28)

#### 修复
- ✅ 修复日期格式化错误（使用 `#dates.format()` 替代 `#temporals.format()`）
- ✅ 改进网络获取错误处理，添加详细日志输出
- ✅ 优化错误提示，提升用户体验

#### 功能
- ✅ 完成实例管理基础功能
- ✅ 完成存储管理基础功能
- ✅ 添加仪表板概览

## 🐛 已知问题

1. **网络获取失败**: OpenStack4j 3.2.0 与某些 OpenStack 版本的 Neutron API 可能不完全兼容
   - 影响: 创建实例时网络下拉框可能为空
   - 临时方案: 可以手动输入网络 ID
   - 详见: `问题分析.md`

2. **启动/停止功能**: OpenStack4j 3.2.0 可能不支持直接启动/停止实例
   - 临时方案: 使用 OpenStack Dashboard 或命令行操作

## 📚 文档

- [Git 使用指南](Git使用指南.md) - Git 版本管理完整指南
- [修复说明](修复说明.md) - 问题修复详细记录
- [问题分析](问题分析.md) - 问题排查和分析文档

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 👥 作者

- Sunshine-skyy

## 🙏 致谢

- [OpenStack4j](https://github.com/ContainX/openstack4j) - OpenStack Java SDK
- [Spring Boot](https://spring.io/projects/spring-boot) - 应用框架
- [Bootstrap](https://getbootstrap.com/) - 前端框架

# Git 版本管理指南

## ✅ 已完成：Git 仓库初始化

Git 仓库已成功初始化！

---

## 📝 接下来的步骤

### 步骤1：创建 .gitignore 文件

首先需要创建 `.gitignore` 文件，排除不需要版本管理的文件。

**在项目根目录创建 `.gitignore` 文件，内容如下：**

```gitignore
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
.settings/
.project
.classpath

# 编译文件
*.class
*.jar
*.war
*.ear

# 日志文件
*.log
logs/

# 临时文件
*.tmp
*.bak
*.swp
*~

# 操作系统
.DS_Store
Thumbs.db

# 应用配置（可能包含敏感信息）
application-local.properties
application-dev.properties
```

### 步骤2：配置 Git 用户信息

```bash
# 设置你的用户名
git config user.name "你的名字"

# 设置你的邮箱
git config user.email "your.email@example.com"
```

### 步骤3：添加文件到暂存区

```bash
# 添加所有文件
git add .

# 或者选择性添加
git add src/
git add pom.xml
git add README.md
```

### 步骤4：提交到本地仓库

```bash
# 提交修改
git commit -m "初始提交：OpenStack管理系统基础功能"

# 或者更详细的提交信息
git commit -m "feat: 完成实例管理功能

- 修复日期格式化问题
- 改进网络获取错误处理
- 添加实例列表展示
- 添加实例操作功能（启动、停止、重启、删除）"
```

---

## 🌐 推送到远程仓库（GitHub/Gitee）

### 方案1：使用 GitHub

#### 1. 在 GitHub 创建仓库
1. 访问 https://github.com
2. 点击右上角 "+" -> "New repository"
3. 填写仓库名称：`OpenStackManagement`
4. 选择 Public 或 Private
5. **不要**勾选 "Initialize this repository with a README"
6. 点击 "Create repository"

#### 2. 关联远程仓库并推送
```bash
# 添加远程仓库
git remote add origin https://github.com/你的用户名/OpenStackManagement.git

# 推送到远程仓库
git push -u origin master

# 或者如果默认分支是 main
git branch -M main
git push -u origin main
```

---

### 方案2：使用 Gitee（国内访问更快）

#### 1. 在 Gitee 创建仓库
1. 访问 https://gitee.com
2. 点击右上角 "+" -> "新建仓库"
3. 填写仓库名称：`OpenStackManagement`
4. 选择公开或私有
5. **不要**勾选 "使用 Readme 文件初始化这个仓库"
6. 点击 "创建"

#### 2. 关联远程仓库并推送
```bash
# 添加远程仓库
git remote add origin https://gitee.com/你的用户名/OpenStackManagement.git

# 推送到远程仓库
git push -u origin master
```

---

## 📋 完整操作流程（复制粘贴版）

### 在项目目录打开命令行，依次执行：

```bash
# 1. 配置用户信息（首次使用需要）
git config user.name "你的名字"
git config user.email "your.email@example.com"

# 2. 查看当前状态
git status

# 3. 添加所有文件
git add .

# 4. 提交到本地仓库
git commit -m "初始提交：OpenStack管理系统 - 修复日期格式化和网络获取问题"

# 5. 添加远程仓库（选择 GitHub 或 Gitee）
# GitHub:
git remote add origin https://github.com/你的用户名/OpenStackManagement.git
# 或 Gitee:
git remote add origin https://gitee.com/你的用户名/OpenStackManagement.git

# 6. 推送到远程仓库
git push -u origin master
```

---

## 🔄 日常使用命令

### 查看状态
```bash
git status
```

### 查看修改内容
```bash
git diff
```

### 添加修改
```bash
# 添加所有修改
git add .

# 添加特定文件
git add src/main/java/com/openstack/management/service/NovaService.java
```

### 提交修改
```bash
git commit -m "fix: 修复网络获取失败问题"
```

### 推送到远程
```bash
git push
```

### 拉取远程更新
```bash
git pull
```

### 查看提交历史
```bash
git log

# 简洁版
git log --oneline

# 图形化显示
git log --graph --oneline --all
```

---

## 🔖 创建版本标签

```bash
# 创建标签
git tag -a v1.0.0 -m "版本 1.0.0 - 基础功能完成"

# 推送标签到远程
git push origin v1.0.0

# 推送所有标签
git push origin --tags

# 查看所有标签
git tag
```

---

## ⏮️ 版本恢复

### 恢复到某个提交
```bash
# 查看提交历史
git log --oneline

# 恢复到指定提交（保留工作区修改）
git reset --soft <commit-id>

# 恢复到指定提交（丢弃工作区修改）
git reset --hard <commit-id>
```

### 恢复单个文件
```bash
# 恢复文件到最后一次提交的状态
git checkout -- <文件名>

# 恢复文件到指定提交的状态
git checkout <commit-id> -- <文件名>
```

---

## 🌿 分支管理

### 创建分支
```bash
# 创建并切换到新分支
git checkout -b feature/network-fix

# 或者分两步
git branch feature/network-fix
git checkout feature/network-fix
```

### 切换分支
```bash
git checkout master
```

### 合并分支
```bash
# 切换到主分支
git checkout master

# 合并其他分支
git merge feature/network-fix
```

### 删除分支
```bash
# 删除本地分支
git branch -d feature/network-fix

# 强制删除
git branch -D feature/network-fix
```

---

## 💡 提交信息规范

建议使用以下格式：

```
<type>: <subject>

<body>

<footer>
```

**Type 类型：**
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

**示例：**
```bash
git commit -m "feat: 添加实例管理功能"
git commit -m "fix: 修复日期格式化错误"
git commit -m "docs: 更新 README 文档"
```

---

## 🆘 常见问题

### 1. 忘记添加 .gitignore
```bash
# 删除已跟踪的文件
git rm -r --cached target/
git rm -r --cached .idea/

# 提交删除
git commit -m "chore: 移除不需要跟踪的文件"
```

### 2. 提交了错误的内容
```bash
# 撤销最后一次提交（保留修改）
git reset --soft HEAD~1

# 重新提交
git add .
git commit -m "正确的提交信息"
```

### 3. 修改最后一次提交信息
```bash
git commit --amend -m "新的提交信息"
```

### 4. 推送失败
```bash
# 先拉取远程更新
git pull origin master

# 解决冲突后再推送
git push origin master
```

---

## 📚 推荐的 Git 工具

### 图形化工具
- **SourceTree** - 免费，功能强大
- **GitKraken** - 界面美观
- **GitHub Desktop** - 简单易用
- **TortoiseGit** - Windows 右键菜单集成

### VS Code 插件
- **GitLens** - 强大的 Git 增强插件
- **Git Graph** - 可视化提交历史
- **Git History** - 查看文件历史

---

## ✅ 检查清单

在推送到远程仓库前，请确认：

- [ ] 已创建 `.gitignore` 文件
- [ ] 已配置 Git 用户名和邮箱
- [ ] 已排除敏感信息（密码、密钥等）
- [ ] 已排除编译文件和依赖（target/, node_modules/ 等）
- [ ] 提交信息清晰明确
- [ ] 代码可以正常编译运行

---

## 🎯 下一步建议

1. **立即备份**：尽快推送到远程仓库
2. **定期提交**：每完成一个功能就提交一次
3. **使用分支**：开发新功能时创建新分支
4. **写好注释**：提交信息要清晰描述修改内容
5. **定期推送**：每天结束工作前推送到远程

---

## 📞 需要帮助？

如果在使用 Git 过程中遇到问题，可以：
1. 查看 Git 官方文档：https://git-scm.com/doc
2. 搜索 Stack Overflow
3. 查看这个指南的相关章节






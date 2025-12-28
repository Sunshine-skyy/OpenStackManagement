@echo off
chcp 65001
echo 正在配置 Git...
git config user.name "郑珊珊"
git config user.email "zhengshanshan@example.com"

echo.
echo 正在添加文件到 Git...
git add .

echo.
echo 正在创建提交...
git commit -m "初始提交：OpenStack管理系统"

echo.
echo Git 本地仓库已创建！
echo.
echo 接下来请执行以下步骤将代码推送到远程仓库：
echo 1. 在 GitHub/Gitee 等平台创建一个新的远程仓库
echo 2. 复制远程仓库的 URL
echo 3. 执行以下命令（将 URL 替换为你的仓库地址）：
echo    git remote add origin [你的仓库URL]
echo    git branch -M main
echo    git push -u origin main
echo.
pause


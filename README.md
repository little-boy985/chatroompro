# chatroompro
# 🚀 Java Web 现代化在线聊天室 (v2.0 增强版)

这是一个经过全面重构和升级的 Web 聊天室项目。项目迁移到了最新的 **Jakarta EE** 标准，适配 **Tomcat 10**，并引入了“玻璃拟态”风格的现代化 UI，增加了注册、私聊、系统广播和管理员权限等高级功能。

## 🛠 技术栈

- **服务器**: Apache Tomcat 10+ (支持 Servlet 6.0 / Jakarta EE)
- **核心框架**: Jakarta EE (`jakarta.servlet.*`)
- **标签库**: JSTL 3.0
- **工具库**: Gson (Google JSON 处理库)
- **前端**: 
    - 现代化 CSS3 (Glassmorphism 玻璃拟态设计, 动画效果)
    - 原生 JavaScript (ES6+, Fetch API, 异步轮询)
    - 内嵌 SVG 图标
- **构建工具**: Maven

## ✨ 新增与增强功能

### 1. 核心业务
- **用户注册/登录**: 增加了注册页面，支持账号密码校验（内存存储，重启重置）。
- **点对点私聊**: 点击用户列表中的名字，即可切换到“悄悄话”模式，只有收发双方可见。
- **系统广播**: 用户上线/下线时，聊天窗口会自动显示灰色的系统通知。

### 2. 界面与交互 (UI/UX)
- **高颜值界面**: 动态渐变背景 + 毛玻璃卡片效果。
- **无刷新体验**: 使用 AJAX 轮询机制，发送消息和接收消息均不刷新页面。
- **自动滚动**: 新消息到来时自动定位到底部。
- **视觉反馈**: 区分“我的消息”（绿色/蓝色气泡）、“他人消息”（白色气泡）和“系统消息”。

### 3. 管理员权限
- **预设账号**: 用户名 `admin`，密码 `123`。
- **特权功能**:
    - **踢人**: 管理员可将违规用户踢出聊天室。
    - **删帖**: 管理员可直接删除任意聊天记录。

## 📂 项目结构

```text
src/main
├── java/com/chatroom
│   ├── AppInitListener.java   // 初始化全局容器
│   ├── ChatApiServlet.java    // 核心 API (收发消息/管理)
│   ├── User.java & Message.java // 实体类
│   └── ... (Login/Register/Logout Servlet)
└── webapp
    ├── WEB-INF/views/chat.jsp // 聊天主页 (内嵌核心 JS/CSS)
    ├── index.jsp              // 登录页
    └── register.jsp           // 注册页

# 校园二手交易平台 - 开发文档

## 1. 项目概述

校园二手交易平台是一个基于前后端分离架构的Web应用，旨在为校园内的师生提供一个安全、便捷的二手商品交易环境。平台支持商品发布、浏览、搜索、购买、评价等核心功能，同时提供社区交流、收藏管理、订单跟踪等增值服务。

### 1.1 项目目标
- 提供便捷的二手商品交易服务
- 建立安全可靠的交易环境
- 促进校园内资源的合理利用
- 增强校园社区交流

### 1.2 主要功能
- 用户认证与授权
- 商品管理（发布、编辑、下架）
- 商品浏览与搜索
- 购物车与订单管理
- 社区交流（讨论、评论、点赞）
- 收藏管理
- 个人中心
- 管理员后台

## 2. 技术栈

### 2.1 前端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| TypeScript | 4.x | 类型安全 |
| Element Plus | 2.x | UI组件库 |
| Vite | 4.x | 构建工具 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.x | HTTP客户端 |

### 2.2 后端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 后端框架 |
| Spring Security | 5.x | 安全框架 |
| JWT | - | 身份认证 |
| Spring Data JPA | - | ORM框架 |
| MySQL | 8.x | 数据库 |
| Maven | 3.6+ | 构建工具 |
| Java | 19+ | 开发语言 |

## 3. 项目结构

### 3.1 整体结构
```
campus-second-hand-platform/
├── backend/           # 后端Spring Boot项目
├── frontend/          # 前端Vue 3项目
├── README.md          # 项目说明文档
├── DEVELOPMENT.md     # 开发文档（本文档）
└── .gitignore         # Git忽略配置
```

### 3.2 后端结构
```
backend/
├── src/main/java/com/campus/secondhandplatform/
│   ├── config/         # 配置类（CORS、数据库、安全等）
│   ├── controller/     # REST API控制器
│   ├── dto/            # 数据传输对象
│   ├── entity/         # JPA实体类
│   ├── repository/     # 数据访问层
│   ├── security/       # 安全相关（JWT、自定义用户服务）
│   ├── service/        # 业务逻辑层
│   └── util/           # 工具类
├── src/main/resources/ # 资源文件
│   ├── application.properties  # 应用配置
│   ├── data.sql         # 数据库初始化脚本
│   └── import.sql       # 初始数据脚本
└── pom.xml             # Maven配置
```

### 3.3 前端结构
```
frontend/
├── src/
│   ├── api/            # API请求封装
│   ├── assets/         # 静态资源
│   ├── components/     # 通用组件
│   ├── router/         # 路由配置
│   ├── store/          # Pinia状态管理
│   ├── styles/         # 全局样式
│   ├── types/          # TypeScript类型定义
│   ├── utils/          # 工具函数
│   ├── views/          # 页面组件
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
├── public/             # 公共资源
├── index.html          # HTML模板
├── package.json        # 依赖配置
├── tsconfig.json       # TypeScript配置
└── vite.config.ts      # Vite配置
```

## 4. 环境搭建

### 4.1 开发环境要求
| 环境 | 版本 |
|------|------|
| Java | 19+ |
| Maven | 3.6+ |
| Node.js | 16+ |
| npm | 8+ |
| MySQL | 8.x |

### 4.2 后端环境搭建

1. **安装Java 19+**
   - 下载地址：https://www.oracle.com/java/technologies/downloads/
   - 配置JAVA_HOME环境变量

2. **安装Maven**
   - 下载地址：https://maven.apache.org/download.cgi
   - 配置MAVEN_HOME环境变量

3. **安装MySQL**
   - 下载地址：https://dev.mysql.com/downloads/mysql/
   - 创建数据库：`campus_second_hand`
   - 配置数据库用户和密码

4. **配置后端应用**
   - 编辑 `backend/src/main/resources/application.properties`
   - 修改数据库连接信息

5. **启动后端服务**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

### 4.3 前端环境搭建

1. **安装Node.js和npm**
   - 下载地址：https://nodejs.org/

2. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

3. **启动前端服务**
   ```bash
   npm run dev
   ```

4. **构建生产版本**
   ```bash
   npm run build
   ```

## 5. 核心功能模块

### 5.1 用户认证模块

#### 5.1.1 功能说明
- 用户注册、登录、注销
- JWT令牌生成与验证
- 角色权限管理

#### 5.1.2 关键文件
- 后端：
  - `AuthController.java` - 认证API
  - `JwtUtil.java` - JWT工具类
  - `SecurityConfig.java` - 安全配置
- 前端：
  - `src/api/auth.ts` - 认证API请求
  - `src/views/LoginView.vue` - 登录页面
  - `src/views/RegisterView.vue` - 注册页面

### 5.2 商品管理模块

#### 5.2.1 功能说明
- 商品发布、编辑、下架
- 商品分类管理
- 商品搜索与筛选
- 商品详情展示

#### 5.2.2 关键文件
- 后端：
  - `ProductController.java` - 商品API
  - `CategoryController.java` - 分类API
  - `ProductService.java` - 商品业务逻辑
- 前端：
  - `src/api/product.ts` - 商品API请求
  - `src/views/PublishView.vue` - 商品发布页面
  - `src/views/ProductDetailView.vue` - 商品详情页面
  - `src/views/ProductsView.vue` - 商品列表页面

### 5.3 订单管理模块

#### 5.3.1 功能说明
- 购物车管理
- 订单创建、支付、取消
- 订单状态跟踪

#### 5.3.2 关键文件
- 后端：
  - `CartController.java` - 购物车API
  - `OrderController.java` - 订单API
- 前端：
  - `src/api/cartApi.ts` - 购物车API请求
  - `src/api/order.ts` - 订单API请求
  - `src/views/CartView.vue` - 购物车页面
  - `src/views/OrderCreateView.vue` - 订单创建页面
  - `src/views/OrdersView.vue` - 订单列表页面

### 5.4 社区交流模块

#### 5.4.1 功能说明
- 讨论发布、评论、点赞
- 热门讨论展示
- 讨论详情查看

#### 5.4.2 关键文件
- 后端：
  - `DiscussionController.java` - 讨论API
  - `CommentController.java` - 评论API
- 前端：
  - `src/api/community.ts` - 社区API请求
  - `src/views/CommunityView.vue` - 社区页面
  - `src/views/DiscussionDetailView.vue` - 讨论详情页面

### 5.5 个人中心模块

#### 5.5.1 功能说明
- 个人信息管理
- 我的发布、我的订单
- 收藏管理
- 消息通知

#### 5.5.2 关键文件
- 后端：
  - `UserController.java` - 用户API
  - `FavoriteController.java` - 收藏API
  - `MessageController.java` - 消息API
- 前端：
  - `src/api/user.ts` - 用户API请求
  - `src/api/favorite.ts` - 收藏API请求
  - `src/views/ProfileView.vue` - 个人中心页面
  - `src/views/FavoritesView.vue` - 收藏页面

## 6. API文档

### 6.1 Swagger文档
项目使用SpringDoc OpenAPI自动生成API文档：
- 访问地址：`http://localhost:8081/swagger-ui.html`
- 支持在线API测试
- 包含所有RESTful API的详细说明

### 6.2 API命名规范
- 采用RESTful风格
- 资源名使用复数形式
- HTTP方法使用规范：
  - GET：获取资源
  - POST：创建资源
  - PUT：更新资源
  - DELETE：删除资源
- 路径示例：
  - `GET /api/products` - 获取商品列表
  - `GET /api/products/{id}` - 获取商品详情
  - `POST /api/products` - 创建商品
  - `PUT /api/products/{id}` - 更新商品
  - `DELETE /api/products/{id}` - 删除商品

## 7. 数据库设计

### 7.1 核心实体关系

```
用户 (User) ────┬─── 订单 (Order) ──── 订单项 (OrderItem)
                │
                ├─── 商品 (Product) ──── 商品分类 (Category)
                │
                ├─── 收藏 (Favorite)
                │
                ├─── 讨论 (Discussion) ──── 评论 (Comment)
                │
                └─── 消息 (Message)
```

### 7.2 主要实体类
- `User` - 用户信息
- `Product` - 商品信息
- `Order` - 订单信息
- `Category` - 商品分类
- `Discussion` - 社区讨论
- `Comment` - 评论信息
- `Favorite` - 收藏信息
- `Message` - 消息信息

## 8. 开发规范

### 8.1 代码风格
- 后端：遵循Java编码规范，使用4空格缩进
- 前端：遵循TypeScript编码规范，使用2空格缩进
- 统一使用UTF-8编码
- 文件名使用驼峰命名

### 8.2 提交规范
- 提交信息清晰明了
- 遵循Conventional Commits规范
- 示例：
  - `feat: 添加商品搜索功能`
  - `fix: 修复登录页表单验证问题`
  - `docs: 更新开发文档`
  - `refactor: 重构订单管理模块`

### 8.3 分支管理
- `master` - 主分支，用于生产环境
- `develop` - 开发分支，用于集成测试
- `feature/*` - 功能分支，用于开发新功能
- `bugfix/*` - 修复分支，用于修复bug

## 9. 部署指南

### 9.1 后端部署

1. **编译打包**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

2. **运行Jar包**
   ```bash
   java -jar target/second-hand-platform-1.0.0.jar
   ```

3. **Docker部署（可选）**
   - 创建Dockerfile
   - 构建Docker镜像
   - 运行Docker容器

### 9.2 前端部署

1. **构建生产版本**
   ```bash
   cd frontend
   npm run build
   ```

2. **部署到Nginx**
   - 复制`dist`目录到Nginx的`html`目录
   - 配置Nginx反向代理到后端服务

## 10. 常见问题

### 10.1 后端启动失败
- 检查数据库连接配置
- 检查Java版本是否符合要求
- 查看日志文件获取详细错误信息

### 10.2 前端无法连接后端
- 检查后端服务是否正常运行
- 检查CORS配置
- 检查API请求地址是否正确

### 10.3 数据库连接问题
- 确保MySQL服务已启动
- 确保数据库名称、用户名、密码正确
- 确保数据库用户有足够的权限

## 11. 开发工具推荐

### 11.1 后端开发工具
- IntelliJ IDEA - Java开发IDE
- MySQL Workbench - 数据库管理工具
- Postman - API测试工具

### 11.2 前端开发工具
- VS Code - 前端开发IDE
- Chrome DevTools - 浏览器调试工具
- Vue DevTools - Vue调试插件

## 12. 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: 添加AmazingFeature功能'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 13. 联系方式

- 项目维护者：[your-name]
- 邮箱：[your-email]
- 项目地址：[https://gitee.com/xbzys/campus-second-hand-platform](https://gitee.com/xbzys/campus-second-hand-platform)

## 14. 更新日志

### v1.0.0 (2023-xx-xx)
- 初始版本发布
- 实现用户认证功能
- 实现商品发布、浏览、搜索功能
- 实现购物车和订单管理
- 实现社区交流功能
- 实现个人中心功能

---

感谢您阅读本开发文档！希望本文档能帮助您快速了解和参与校园二手交易平台的开发。
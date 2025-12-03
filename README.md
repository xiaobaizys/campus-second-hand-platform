# 校园二手交易平台

## 项目简介
这是一个基于前后端分离技术的校园二手交易平台，旨在为校园内的师生提供一个安全、便捷的二手商品交易环境。平台支持商品发布、浏览、搜索、购买、评价等核心功能，同时提供社区交流、收藏管理、订单跟踪等增值服务。

## 技术栈

### 前端
- **框架**: Vue 3
- **UI组件库**: Element Plus
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **语言**: TypeScript
- **状态管理**: Pinia
- **路由**: Vue Router

### 后端
- **框架**: Spring Boot 3
- **安全框架**: Spring Security + JWT
- **ORM框架**: Spring Data JPA
- **数据库**: MySQL 8.0
- **语言**: Java 19+
- **构建工具**: Maven

## 项目结构

```
campus-second-hand-platform/
├── backend/           # Spring Boot后端项目
│   ├── src/main/java/com/campus/secondhandplatform/
│   │   ├── config/         # 配置类
│   │   ├── controller/     # 控制器
│   │   ├── dto/            # 数据传输对象
│   │   ├── entity/         # 实体类
│   │   ├── repository/     # 数据访问层
│   │   ├── security/       # 安全相关
│   │   ├── service/        # 业务逻辑层
│   │   └── util/           # 工具类
│   ├── src/main/resources/ # 资源文件
│   └── pom.xml             # Maven配置
├── frontend/          # Vue3前端项目
│   ├── src/
│   │   ├── api/            # API请求
│   │   ├── assets/         # 静态资源
│   │   ├── components/     # 组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   ├── styles/         # 样式文件
│   │   ├── types/          # TypeScript类型定义
│   │   ├── utils/          # 工具函数
│   │   ├── views/          # 页面组件
│   │   ├── App.vue         # 根组件
│   │   └── main.ts         # 入口文件
│   ├── index.html          # HTML模板
│   ├── package.json        # 依赖配置
│   └── vite.config.ts      # Vite配置
└── README.md             # 项目说明文档
```

## 功能特性

### 用户功能
- **用户认证**: 注册、登录、忘记密码
- **个人中心**: 个人信息管理、头像上传、密码修改
- **商品管理**: 发布商品、编辑商品、下架商品、商品列表
- **商品浏览**: 首页推荐、分类浏览、搜索功能、筛选排序
- **商品详情**: 商品信息、图片展示、评价查看、收藏功能
- **购物车**: 添加商品、修改数量、删除商品、结算功能
- **订单管理**: 下单、支付、订单列表、订单详情、物流跟踪
- **收藏管理**: 收藏商品、收藏列表、取消收藏
- **社区交流**: 发布讨论、评论、点赞、查看热门讨论
- **消息通知**: 系统通知、订单状态更新、消息中心

### 管理员功能
- **商品管理**: 审核商品、下架商品、批量操作
- **用户管理**: 用户列表、角色分配、禁用/启用用户
- **订单管理**: 订单列表、订单详情、订单状态更新
- **分类管理**: 添加分类、编辑分类、删除分类
- **数据统计**: 销售统计、用户统计、商品统计
- **系统设置**: 平台参数配置、公告管理、取货点管理

## 数据库配置

### 环境要求
- MySQL 8.0+

### 配置说明
后端配置文件位于 `backend/src/main/resources/application.properties`，主要配置项：

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/campus_secondhand?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root123

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB

# JWT配置
jwt.secret=your-secret-key
jwt.expiration=3600000
```

## 开发环境

### 前端环境
- Node.js 16+
- npm 8+

### 后端环境
- JDK 19+
- Maven 3.6+

## 安装与运行

### 后端运行

1. **克隆项目**
   ```bash
   git clone https://github.com/your-repo/campus-second-hand-platform.git
   cd campus-second-hand-platform
   ```

2. **配置数据库**
   - 创建MySQL数据库：`campus_secondhand`
   - 修改`backend/src/main/resources/application.properties`中的数据库配置

3. **编译运行**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **访问后端API**
   - 后端服务默认运行在 `http://localhost:8080`
   - 健康检查：`http://localhost:8080/api/health`

### 前端运行

1. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

2. **开发模式运行**
   ```bash
   npm run dev
   ```

3. **构建生产版本**
   ```bash
   npm run build
   ```

4. **访问前端页面**
   - 开发模式：`http://localhost:5173`
   - 生产模式：需部署到Web服务器

## API文档

项目使用SpringDoc OpenAPI自动生成API文档，访问地址：
-  Swagger UI: `http://localhost:8080/swagger-ui.html`
-  OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目维护者: [your-name]
- 邮箱: [your-email]
- 项目地址: [https://github.com/your-repo/campus-second-hand-platform](https://github.com/your-repo/campus-second-hand-platform)

## 更新日志

### v1.0.0 (2023-xx-xx)
- 初始版本发布
- 实现用户认证功能
- 实现商品发布、浏览、搜索功能
- 实现购物车和订单管理
- 实现社区交流功能
- 实现管理员后台管理

---

感谢您使用校园二手交易平台！如果您有任何问题或建议，欢迎提出Issue或Pull Request。
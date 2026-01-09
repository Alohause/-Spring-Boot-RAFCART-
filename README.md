# RAFCART - 基于 Spring Boot 的电商系统（用户端）

一个基于 Spring Boot + MyBatis-Plus + MySQL + Bootstrap 的前后端分离商城项目，实现了用户购物流程与账户中心功能。

> **注意**：本项目仅实现用户端功能，未实现商家后台与管理员后台。

---

## 项目概述

**RAFCART** 商城系统是一个面向普通用户的在线购物平台，涵盖账户管理、商品浏览、搜索、购物车、下单、支付模拟、订单管理、评价、收藏等完整业务流程。

前端基于现成 Bootstrap 模板进行二次开发，后端采用 Spring Boot 构建 REST API，实现清晰的分层结构与规范的数据建模。

---

## 项目角色

**角色**：后端开发 / 前端功能实现（独立完成）

---

## 功能模块

### 用户与账户
- 用户注册 / 登录（手机号 & 邮箱）
- 个人信息查看与修改
- 地址管理（新增 / 编辑 / 删除 / 设置默认地址）

### 商品与浏览
- 商品列表 / 商品详情
- 模糊搜索（商品名、品牌名）
- 商品规格（SKU）展示
- 商品评价展示

### 购物流程
- 购物车（增删改查、数量变更）
- 结算页下单
- 地址快照保存
- 模拟支付流程

### 订单系统
- 订单创建
- 订单列表（历史订单）
- 订单详情
- 订单取消（未完成订单）
- 订单追踪（Track Order）

### 用户行为
- 商品收藏（Wishlist）
- 我的评价（分页）
- 提问

---

## 技术栈

### 后端
- Spring Boot
- MyBatis-Plus
- MySQL
- Jackson
- RESTful API 设计
- 拦截器实现用户上下文（UserContext）

### 前端
- HTML5 / CSS3
- JavaScript / jQuery
- Bootstrap 4/5（模板来源：bootstrapmb.com）
- Ajax（fetch + JSON）

---

## 数据库设计

**数据库名**：`store_db`

包含以下核心表（节选）：
- `user`：用户信息
- `user_address`：收货地址
- `category` / `brand`：分类与品牌
- `product` / `product_sku`：商品与规格
- `cart` / `cart_item`：购物车
- `orders` / `order_item`：订单与快照
- `payment`：支付记录
- `wishlist`：收藏
- `review`：商品评价
- `order_return`：退货申请
- `sms_code`：验证码

> **注**：完整建表 SQL 已包含在本仓库中

---

## 项目运行方式（后端）

### 1️ 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.x
- Node.js（仅用于前端调试，可选）

### 2️ 创建数据库
```sql
CREATE DATABASE store_db
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

导入项目中提供的**完整 SQL 文件**（包含表结构与测试数据）。

### 3 修改数据库配置
编辑 `application.yml` 或 `application.properties`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/store_db?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码
```

### 4️ 启动后端服务
```bash
mvn clean spring-boot:run
```

或在 IDE 中直接运行 `StoreApplication.java`

**默认端口**：`http://localhost:8080`

---

## 前端运行方式

### 方法一
直接使用浏览器打开 HTML 文件，例如：
- `index.html`
- `shop-grid.html`
- `shop-list.html`
- `product-view.html`
- `checkout.html`

前端通过 Ajax 调用本地 `http://localhost:8080` 接口。

### 方法二
使用本地服务器（如 VS Code Live Server）运行前端。

---

## 项目特点
- 模拟真实电商用户流程
- 订单快照设计，避免数据被修改影响历史订单
- 清晰的分层结构（Controller / Service / Mapper）
- 使用 MyBatis-Plus 提高开发效率
- 前后端解耦，接口清晰，易扩展

---

## 可扩展方向（未实现）
- Spring Security / JWT 登录认证
- Redis（购物车 / 验证码 / 热数据）
- 管理员后台（商品、订单、库存）
- 商家后台（商品、订单、库存、物流、客服）
- 支付宝 / 微信支付真实接入
- 搜索优化（ES）

---

## 项目展示

[![项目演示视频](https://img.youtube.com/vi/5FYXyBOWHgI/0.jpg)](https://youtu.be/5FYXyBOWHgI)

---

## License
本项目仅用于学习与展示用途。

---

## 项目结构示例
```
src/
├── main/
│   ├── java/com/rafcart/store/
│   │   ├── controller/     # 控制层
│   │   ├── service/        # 业务层
|   |   |   ├──Impl/
│   │   ├── mapper/         # 数据访问层
│   │   ├── entity/         # 实体类
│   │   ├── dto/            # 数据传输对象
│   │   ├── common/         # 通用组件
│   │   └── StoreApplication.java
│   └── resources/
│       ├── static/         # 静态资源
│       ├── templates/      # 模板文件
│       ├── mapper/         # XML映射文件
│       └── application.yml
└── test/                   # 测试代码
```

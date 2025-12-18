/*
 Navicat Premium Data Transfer

 Source Server         : big-data-test
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : campus_second_hand

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 17/12/2025 21:57:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `publisher_id` bigint NOT NULL,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_top` bit(1) NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcements
-- ----------------------------

-- ----------------------------
-- Table structure for cart_items
-- ----------------------------
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_items
-- ----------------------------
INSERT INTO `cart_items` VALUES (6, 4, 20, 1, '2025-12-02 13:33:16', '2025-12-02 13:33:16');
INSERT INTO `cart_items` VALUES (8, 3, 20, 1, '2025-12-02 14:25:48', '2025-12-02 14:25:48');
INSERT INTO `cart_items` VALUES (9, 3, 1, 1, '2025-12-02 14:28:03', '2025-12-02 14:28:03');

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '数码产品', '手机、电脑、相机等数码设备', 'Iphone', 1, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (2, '图书教材', '教材、小说、参考书等', 'Reading', 2, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (3, '生活用品', '日用品、装饰品等', 'Notebook', 3, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (4, '运动户外', '运动器材、户外装备等', 'Basketball', 4, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (5, '服装鞋包', '衣服、鞋子、包包等', 'ShoppingBag', 5, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (6, '美妆护肤', '化妆品、护肤品等', 'Camera', 6, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (7, '食品零食', '零食、饮料等', 'Food', 7, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (8, '其他', '其他类别商品', 'More', 8, 1, '2025-11-23 00:11:45', '2025-11-23 00:11:45');
INSERT INTO `categories` VALUES (9, '12', '', NULL, 0, 0, '2025-12-02 09:20:25', '2025-12-03 05:21:12');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `rating` int NULL DEFAULT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `discussion_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_comments_product_id`(`product_id` ASC) USING BTREE,
  INDEX `fk_comments_user_id`(`user_id` ASC) USING BTREE,
  INDEX `FK40i4dao9h6hd08g5wmmw87lnj`(`discussion_id` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK40i4dao9h6hd08g5wmmw87lnj` FOREIGN KEY (`discussion_id`) REFERENCES `discussions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comments_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comments_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 7, 3, '充电宝很不错，容量大，充电快，卖家描述准确，推荐购买！', 5, NULL, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (2, 7, 2, '感谢购买，交易愉快！', 5, 1, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (3, 3, 1, '教材很新，笔记也很清晰，对我很有帮助，谢谢卖家！', 5, NULL, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (4, 3, 4, '不客气，希望能帮到你！', 5, 3, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (5, 5, 5, '瑜伽垫质量很好，防滑效果不错，送的小礼物也很实用，五星好评！', 5, NULL, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (6, 1, 3, '手机成色很好，功能正常，电池健康度确实如描述，就是价格有点小贵，不过整体满意！', 4, NULL, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (7, 10, 1, '零食种类很丰富，都是我喜欢的品牌，包装也很精美，下次还会再来买！', 5, NULL, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (8, 2, 5, 'MacBook真的很新，几乎看不出使用痕迹，性能也很强，卖家人很好，耐心解答了我的问题，感谢！', 5, NULL, 0, '2025-11-23 21:33:12', '2025-11-23 21:33:12', NULL);
INSERT INTO `comments` VALUES (9, 18, 3, '123', NULL, NULL, 0, '2025-12-01 15:22:42', '2025-12-01 15:22:42', NULL);
INSERT INTO `comments` VALUES (10, NULL, 4, '1', NULL, NULL, 0, '2025-12-02 13:27:07', '2025-12-02 13:27:07', 6);
INSERT INTO `comments` VALUES (11, NULL, 4, '123123', NULL, NULL, 0, '2025-12-02 13:27:11', '2025-12-02 13:27:11', 6);
INSERT INTO `comments` VALUES (12, NULL, 4, '123321', NULL, NULL, 0, '2025-12-02 13:27:18', '2025-12-02 13:27:18', 5);
INSERT INTO `comments` VALUES (13, NULL, 3, '123', NULL, NULL, 0, '2025-12-03 05:12:35', '2025-12-03 05:12:35', 3);
INSERT INTO `comments` VALUES (14, 1, 4, '123', NULL, NULL, 0, '2025-12-03 05:29:37', '2025-12-03 05:29:37', NULL);
INSERT INTO `comments` VALUES (15, 1, 4, '123333', NULL, NULL, 0, '2025-12-03 05:29:39', '2025-12-03 05:29:39', NULL);

-- ----------------------------
-- Table structure for discussion_images
-- ----------------------------
DROP TABLE IF EXISTS `discussion_images`;
CREATE TABLE `discussion_images`  (
  `discussion_id` bigint NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  INDEX `FK2c7ghq2ucmolgy3b9xr5i5dn`(`discussion_id` ASC) USING BTREE,
  CONSTRAINT `FK2c7ghq2ucmolgy3b9xr5i5dn` FOREIGN KEY (`discussion_id`) REFERENCES `discussions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of discussion_images
-- ----------------------------
INSERT INTO `discussion_images` VALUES (1, 'uploads/products/c6eadd88-6bfd-41a8-b5c6-f1f13f13da4c.jpg');
INSERT INTO `discussion_images` VALUES (2, 'uploads/products/593e0e0a-92c1-485d-93ec-b8cdbec73c55.jpg');
INSERT INTO `discussion_images` VALUES (2, 'uploads/products/f244e81d-78a5-4db3-90a5-61c766ae56e6.jpg');
INSERT INTO `discussion_images` VALUES (5, 'http://localhost:8081uploads/products/b7f96f61-c6f1-4c24-b58d-d2f73d0dca07.jpg');
INSERT INTO `discussion_images` VALUES (7, 'http://localhost:8081/uploads/products/286d1c73-efe4-4492-a351-6adfdc636f4d.jpg');

-- ----------------------------
-- Table structure for discussion_likes
-- ----------------------------
DROP TABLE IF EXISTS `discussion_likes`;
CREATE TABLE `discussion_likes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `discussion_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKmko3gyk9m1s7o8fug4cnr5ybr`(`discussion_id` ASC) USING BTREE,
  INDEX `FKprid5972sqht8wgpx6xfdqydy`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKmko3gyk9m1s7o8fug4cnr5ybr` FOREIGN KEY (`discussion_id`) REFERENCES `discussions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKprid5972sqht8wgpx6xfdqydy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of discussion_likes
-- ----------------------------
INSERT INTO `discussion_likes` VALUES (2, '2025-12-01 02:42:50.352000', '2025-12-01 02:42:50.352000', 2, 4);
INSERT INTO `discussion_likes` VALUES (5, '2025-12-01 15:31:36.893000', '2025-12-01 15:31:36.893000', 4, 3);
INSERT INTO `discussion_likes` VALUES (7, '2025-12-02 01:08:19.962000', '2025-12-02 01:08:19.962000', 6, 4);

-- ----------------------------
-- Table structure for discussion_tags
-- ----------------------------
DROP TABLE IF EXISTS `discussion_tags`;
CREATE TABLE `discussion_tags`  (
  `discussion_id` bigint NOT NULL,
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  INDEX `FKhbo0sho8hbqh4hhlqp6axwdn8`(`discussion_id` ASC) USING BTREE,
  CONSTRAINT `FKhbo0sho8hbqh4hhlqp6axwdn8` FOREIGN KEY (`discussion_id`) REFERENCES `discussions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of discussion_tags
-- ----------------------------
INSERT INTO `discussion_tags` VALUES (1, '求助问答');
INSERT INTO `discussion_tags` VALUES (2, '校园活动');
INSERT INTO `discussion_tags` VALUES (4, '答疑');
INSERT INTO `discussion_tags` VALUES (5, '避坑');
INSERT INTO `discussion_tags` VALUES (6, '答疑');
INSERT INTO `discussion_tags` VALUES (3, '置换');
INSERT INTO `discussion_tags` VALUES (7, '答疑');
INSERT INTO `discussion_tags` VALUES (7, '置换');

-- ----------------------------
-- Table structure for discussions
-- ----------------------------
DROP TABLE IF EXISTS `discussions`;
CREATE TABLE `discussions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_count` int NULL DEFAULT 0,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT 0,
  `like_count` int NULL DEFAULT 0,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT 0,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK9poj0fwjbqjit2n9dt6bhe5f7`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK9poj0fwjbqjit2n9dt6bhe5f7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of discussions
-- ----------------------------
INSERT INTO `discussions` VALUES (1, 0, '112222222222222', '2025-11-30 12:40:06.198000', 0, 0, '111111111111', '2025-11-30 12:40:06.198000', 0, 4);
INSERT INTO `discussions` VALUES (2, 0, '11111111111111', '2025-12-01 02:42:33.897000', 0, 1, '测试', '2025-12-01 02:42:50.354000', 0, 4);
INSERT INTO `discussions` VALUES (3, 1, '22222222222222', '2025-12-01 12:31:06.847000', 0, 0, '11111', '2025-12-03 05:12:43.953000', 1, 3);
INSERT INTO `discussions` VALUES (4, 0, '2131123333333333333333', '2025-12-01 14:27:31.214000', 0, 1, '123123', '2025-12-01 15:31:36.897000', 0, 3);
INSERT INTO `discussions` VALUES (5, 1, '123', '2025-12-01 15:19:01.405000', 0, 0, '123', '2025-12-02 13:27:18.508000', 1, 4);
INSERT INTO `discussions` VALUES (6, 2, '请问23123', '2025-12-01 15:26:01.238000', 0, 1, 'q\'weqwe22', '2025-12-03 05:11:47.682000', 2, 3);
INSERT INTO `discussions` VALUES (7, 0, '123', '2025-12-03 05:13:04.489000', 0, 0, '123', '2025-12-03 05:13:04.489000', 0, 3);

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `favorites_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorites
-- ----------------------------
INSERT INTO `favorites` VALUES (1, 3, 1, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (2, 3, 2, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (4, 5, 1, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (5, 5, 5, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (6, 5, 6, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (7, 5, 9, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (8, 1, 1, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (9, 1, 2, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (10, 1, 3, '2025-11-23 21:33:12');
INSERT INTO `favorites` VALUES (23, 3, 8, '2025-12-01 15:50:50');
INSERT INTO `favorites` VALUES (24, 3, 18, '2025-12-02 00:54:41');
INSERT INTO `favorites` VALUES (27, 3, 20, '2025-12-02 13:39:54');
INSERT INTO `favorites` VALUES (32, 4, 20, '2025-12-03 03:37:58');

-- ----------------------------
-- Table structure for likes
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `discussion_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKa8l0cqp8pyr9yyvgbnwkx9mxh`(`user_id` ASC, `discussion_id` ASC) USING BTREE,
  INDEX `FK9pv556yds9g5l4oi6iv0dggfp`(`discussion_id` ASC) USING BTREE,
  CONSTRAINT `FK9pv556yds9g5l4oi6iv0dggfp` FOREIGN KEY (`discussion_id`) REFERENCES `discussions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKnvx9seeqqyy71bij291pwiwrg` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of likes
-- ----------------------------

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `receiver_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKt05r0b6n0iis8u7dfna4xdh73`(`receiver_id` ASC) USING BTREE,
  INDEX `FK4ui4nnwntodh6wjvck53dbk9m`(`sender_id` ASC) USING BTREE,
  CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKt05r0b6n0iis8u7dfna4xdh73` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_id` bigint NOT NULL,
  `buyer_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `final_price` decimal(10, 2) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING',
  `contact_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `delivery_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `delivery_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `payment_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'UNPAID',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_number`(`order_number` ASC) USING BTREE,
  INDEX `fk_orders_product_id`(`product_id` ASC) USING BTREE,
  INDEX `fk_orders_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `fk_orders_seller_id`(`seller_id` ASC) USING BTREE,
  CONSTRAINT `fk_orders_buyer_id` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_orders_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_orders_seller_id` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD20230701001', 7, 3, 2, 79.00, 75.00, 'COMPLETED', '微信: buyer1_wx', '主校区学生宿舍A栋101', '面交', '微信支付', 'PAID', '当面验货后付款', '2025-11-23 21:33:12', '2025-11-23 21:33:12');
INSERT INTO `orders` VALUES (2, 'ORD20230701002', 5, 5, 2, 45.00, 45.00, 'PENDING', 'QQ: 654321098', '分校区学生宿舍B栋203', '面交', '支付宝', 'UNPAID', '希望周末交易', '2025-11-23 21:33:12', '2025-11-23 21:33:12');
INSERT INTO `orders` VALUES (3, 'ORD20230702001', 3, 1, 4, 25.00, 20.00, 'COMPLETED', '微信: admin_wx', '主校区行政楼', '面交', '现金', 'PAID', '已取货', '2025-11-23 21:33:12', '2025-11-23 21:33:12');
INSERT INTO `orders` VALUES (4, 'ORD20230702002', 10, 3, 4, 88.00, 85.00, 'SHIPPED', '微信: buyer1_wx', '分校区学生宿舍B栋203', '快递', '微信支付', 'PAID', '已发货，预计明天送达', '2025-11-23 21:33:12', '2025-11-23 21:33:12');
INSERT INTO `orders` VALUES (5, 'ORD202512011138134249', 18, 4, 4, 0.01, 0.01, 'CANCELLED', NULL, NULL, NULL, NULL, 'UNPAID', NULL, '2025-12-01 03:38:13', '2025-12-01 09:35:02');
INSERT INTO `orders` VALUES (6, 'ORD202512011140271980', 1, 4, 2, 4999.00, 4999.00, 'PAID', NULL, NULL, NULL, NULL, 'UNPAID', NULL, '2025-12-01 03:40:27', '2025-12-01 09:34:54');
INSERT INTO `orders` VALUES (7, 'ORD202512011141417981', 17, 4, 4, 0.01, 0.01, 'CANCELLED', NULL, NULL, NULL, NULL, 'UNPAID', NULL, '2025-12-01 03:41:42', '2025-12-01 09:34:52');
INSERT INTO `orders` VALUES (8, 'ORD202512011144469801', 18, 4, 4, 0.01, 0.01, 'CANCELLED', NULL, NULL, NULL, NULL, 'UNPAID', NULL, '2025-12-01 03:44:47', '2025-12-01 09:34:29');
INSERT INTO `orders` VALUES (9, 'ORD202512011731125210', 18, 4, 4, 0.01, 0.01, 'CANCELLED', NULL, NULL, NULL, NULL, 'UNPAID', NULL, '2025-12-01 09:31:12', '2025-12-01 09:34:22');
INSERT INTO `orders` VALUES (10, 'ORD202512011743535352', 18, 4, 4, 0.01, 0.01, 'CANCELLED', NULL, NULL, NULL, NULL, 'UNPAID', NULL, '2025-12-01 09:43:54', '2025-12-01 09:43:58');
INSERT INTO `orders` VALUES (11, 'ORD202512022241063457', 20, 3, 4, 20.00, 20.00, 'PENDING', NULL, NULL, NULL, NULL, 'UNPAID', NULL, '2025-12-02 14:41:07', '2025-12-02 14:41:07');

-- ----------------------------
-- Table structure for pickup_points
-- ----------------------------
DROP TABLE IF EXISTS `pickup_points`;
CREATE TABLE `pickup_points`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pickup_points
-- ----------------------------
INSERT INTO `pickup_points` VALUES (1, '图书馆一楼大厅', '张同学', b'1', '图书馆自提点', '13800138000');
INSERT INTO `pickup_points` VALUES (2, '第一食堂门口', '李同学', b'1', '食堂自提点', '13900139000');
INSERT INTO `pickup_points` VALUES (3, '学生宿舍1号楼楼下', '王同学', b'1', '宿舍区自提点', '13700137000');

-- ----------------------------
-- Table structure for platform_params
-- ----------------------------
DROP TABLE IF EXISTS `platform_params`;
CREATE TABLE `platform_params`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `param_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `param_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_rec88jf0s778k70ymeqjjnhxn`(`param_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of platform_params
-- ----------------------------
INSERT INTO `platform_params` VALUES (1, '商品审核时效（小时）', 'review_time', '24');
INSERT INTO `platform_params` VALUES (2, '平台交易规则', 'trade_rules', '请遵守平台交易规则，诚信交易');
INSERT INTO `platform_params` VALUES (3, '商品最大图片数量', 'max_product_images', '5');
INSERT INTO `platform_params` VALUES (4, '评论最大长度', 'max_comment_length', '500');
INSERT INTO `platform_params` VALUES (5, '默认头像URL', 'default_avatar', 'https://via.placeholder.com/150');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `price` decimal(10, 2) NOT NULL,
  `original_price` decimal(10, 2) NULL DEFAULT NULL,
  `image_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `category_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'AVAILABLE',
  `view_count` int NULL DEFAULT 0,
  `like_count` int NULL DEFAULT 0,
  `is_negotiable` tinyint(1) NULL DEFAULT 1,
  `is_new` tinyint(1) NULL DEFAULT 0,
  `delivery_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_products_category_id`(`category_id` ASC) USING BTREE,
  INDEX `fk_products_seller_id`(`seller_id` ASC) USING BTREE,
  CONSTRAINT `fk_products_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_products_seller_id` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 'iPhone 12 Pro 128G', '个人闲置iPhone 12 Pro，128G，深空灰色，无磕碰，功能完好，电池健康度90%，原装充电器和数据线都在。', 4999.00, 7999.00, 'https://tse2-mm.cn.bing.net/th/id/OIP-C.I59rtEI7D8qJOa-NjxbEhAHaFj?w=284&h=213&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 1, 2, 'AVAILABLE', 163, 23, 1, 0, '面交/快递', '主校区图书馆', 'QQ: 123456789', '2025-11-23 21:33:12', '2025-12-03 05:29:31');
INSERT INTO `products` VALUES (2, 'MacBook Air M1', '2021款MacBook Air M1芯片，8G内存，256G固态硬盘，银色，使用一年，非常新，无划痕。', 6500.00, 8999.00, 'https://tse1-mm.cn.bing.net/th/id/OIP-C.4bkOb1iqvmBh_75dYnP2cgAAAA?w=228&h=180&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 1, 2, 'AVAILABLE', 206, 31, 1, 0, '面交', '主校区学生宿舍', '微信: seller1_wx', '2025-11-23 21:33:12', '2025-12-03 14:49:22');
INSERT INTO `products` VALUES (3, '高等数学教材', '同济大学版《高等数学》第七版上册，几乎全新，有少量笔记，适合大一新生。', 25.00, 45.00, 'https://tse4-mm.cn.bing.net/th/id/OIP-C.w0zmalToDnfkqSAWyKNNYgHaHa?w=209&h=209&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 2, 4, 'AVAILABLE', 91, 12, 1, 0, '面交', '分校区教学楼', '电话: 13800138003', '2025-11-23 21:33:12', '2025-12-03 05:13:42');
INSERT INTO `products` VALUES (4, '英语四级词汇书', '星火英语四级词汇乱序版，有少量笔记和划线，不影响使用。', 15.00, 35.00, 'https://tse4-mm.cn.bing.net/th/id/OIP-C.Ipvanddapg78pYRog92gbQHaHa?w=206&h=206&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 2, 4, 'AVAILABLE', 67, 8, 0, 1, '快递', '主校区', '微信: seller2_wx', '2025-11-23 21:33:12', '2025-11-30 19:49:21');
INSERT INTO `products` VALUES (5, '瑜伽垫', '全新未拆封TPE瑜伽垫，加厚防滑，尺寸183*61cm，送瑜伽带和瑜伽砖。', 45.00, 80.00, 'https://tse4-mm.cn.bing.net/th/id/OIP-C.Awjg6KEv4NbYYJMz2sumGQHaHa?w=176&h=180&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 4, 2, 'AVAILABLE', 124, 19, 1, 1, '面交', '主校区体育馆', 'QQ: 123456789', '2025-11-23 21:33:12', '2025-11-30 19:49:36');
INSERT INTO `products` VALUES (6, 'Nike运动鞋', 'Nike Air Max 270，42码，黑白配色，穿过3次，几乎全新，原价899，现价450。', 450.00, 899.00, 'https://tse4-mm.cn.bing.net/th/id/OIP-C.3VTukh6e5lXAqThpr0g8RQHaHa?w=199&h=199&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 5, 4, 'AVAILABLE', 178, 25, 1, 0, '面交', '分校区', '电话: 13800138003', '2025-11-23 21:33:12', '2025-11-30 19:50:13');
INSERT INTO `products` VALUES (7, '小米充电宝', '小米20000mAh充电宝，支持快充，使用半年，无故障，外观9成新。', 79.00, 129.00, 'https://tse1-mm.cn.bing.net/th/id/OIP-C.Xgl-isTqM2-sSHHPzHnV-QHaJ4?w=163&h=218&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 1, 2, 'SOLD', 235, 34, 0, 0, '面交/快递', '主校区图书馆', 'QQ: 123456789', '2025-11-23 21:33:12', '2025-11-30 11:51:06');
INSERT INTO `products` VALUES (8, '《三体》全套', '刘慈欣《三体》三部曲全套，几乎全新，无笔记划线，科幻爱好者必备。', 60.00, 120.00, 'https://tse2-mm.cn.bing.net/th/id/OIP-C.s8zlcHeDdVL-W8t7aiwYTwHaHa?w=190&h=190&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 2, 4, 'AVAILABLE', 146, 21, 1, 0, '面交', '分校区教学楼', '微信: seller2_wx', '2025-11-23 21:33:12', '2025-12-01 15:50:45');
INSERT INTO `products` VALUES (9, '雅诗兰黛小棕瓶', '雅诗兰黛特润修护肌透精华露50ml，剩余约80%，正品保证，因更换护肤品出售。', 280.00, 580.00, 'https://tse4-mm.cn.bing.net/th/id/OIP-C.V-2V8xQyLs1qyYdnyUg9TAHaHa?w=206&h=206&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 6, 2, 'AVAILABLE', 198, 28, 1, 0, '快递', '主校区', '微信: seller1_wx', '2025-11-23 21:33:12', '2025-11-30 19:50:47');
INSERT INTO `products` VALUES (10, '零食大礼包', '各种进口零食组合，包括薯片、巧克力、饼干等，全新未拆封，因减肥出售。', 88.00, 150.00, 'https://tse4-mm.cn.bing.net/th/id/OIP-C.fRg8oP3eP7pTiQJ-XcT_6AHaHa?w=197&h=197&c=7&r=0&o=7&dpr=1.3&pid=1.7&rm=3', 7, 4, 'AVAILABLE', 112, 15, 0, 1, '面交', '分校区', '电话: 13800138003', '2025-11-23 21:33:12', '2025-11-30 19:50:58');
INSERT INTO `products` VALUES (11, '1231233', '1231231231231212312', 0.01, 0.01, '/uploads/products/49b0565b-9924-4654-9aad-e151dcc187c6.jpg', 2, 4, 'AVAILABLE', 1, 0, 0, 0, 'offline', '123123', '平台私信', '2025-11-30 04:17:10', '2025-12-03 03:39:39');
INSERT INTO `products` VALUES (12, '1231231231', '2133333333333333333', 0.01, 0.01, '/uploads/products/3a93ef2f-c1bd-43ed-bae5-103e871e14bd.jpg', 5, 4, 'AVAILABLE', 0, 0, 0, 0, 'offline', '12312', '平台私信', '2025-11-30 04:21:28', '2025-11-30 04:21:28');
INSERT INTO `products` VALUES (13, '1231231231', '2133333333333333333', 0.01, 0.01, '/uploads/products/e941750d-0616-4a6a-965f-3c5a07d27ded.jpg', 5, 4, 'AVAILABLE', 1, 0, 0, 0, 'offline', '12312', '平台私信', '2025-11-30 04:24:33', '2025-11-30 07:35:01');
INSERT INTO `products` VALUES (14, '123333333333333', '33333333333333333', 0.01, 0.01, '/uploads/products/fbddeaac-225e-4113-a0e2-b853779dbb9c.jpg', 2, 4, 'AVAILABLE', 7, 0, 0, 0, 'offline', '3333333333333', '平台私信', '2025-11-30 04:28:14', '2025-12-02 12:28:55');
INSERT INTO `products` VALUES (15, '1111111111111111111', '1111111111111111111', 0.01, 0.01, '/uploads/products/311524a6-0ce6-4b1d-8fde-2d1fac8533ac.jpg', 1, 4, 'AVAILABLE', 19, 0, 0, 0, 'offline', '11111111111', '平台私信', '2025-11-30 05:23:09', '2025-12-02 02:22:11');
INSERT INTO `products` VALUES (16, '111111111111111111111111', '11111111111111111111111', 0.01, 0.01, '/uploads/products/a93b9ada-2d7e-49de-a98d-4e9aa0033265.jpg', 1, 4, 'AVAILABLE', 13, 0, 0, 0, 'offline', '1111111111111', '平台私信', '2025-12-01 02:31:38', '2025-12-02 00:18:53');
INSERT INTO `products` VALUES (17, '12345', '11231231333', 0.01, 0.01, '/uploads/products/ad7bc77a-637e-4985-8ced-3cb659858e9e.jpg', 8, 4, 'AVAILABLE', 9, 0, 0, 0, 'offline', '3333', '平台私信', '2025-12-01 03:35:41', '2025-12-02 00:20:12');
INSERT INTO `products` VALUES (18, '123123', '2133333333333', 0.01, 0.01, '/uploads/products/34eb750b-1309-4f35-ab67-20bb150d916c.jpg,/uploads/products/702d8991-0cdb-4a29-93a3-7a3d15de884d.jpg', 1, 4, 'AVAILABLE', 67, 0, 0, 0, 'offline', '1111', '平台私信', '2025-12-01 03:36:34', '2025-12-02 00:54:38');
INSERT INTO `products` VALUES (19, '2222222', '2222222222222222', 10.00, 10.00, '/uploads/products/c2f2c108-50bc-4563-b9ef-8170ffafdb8d.jpg', 2, 3, 'AVAILABLE', 18, 0, 0, 0, 'offline', '1111111111', '平台私信', '2025-12-01 09:52:15', '2025-12-02 12:28:51');
INSERT INTO `products` VALUES (20, '测试商品1', '测试商品1测试商品1', 20.00, 20.00, '/uploads/products/e0466ee9-ebd2-4df6-9755-2d3db71c9a69.jpg', 1, 4, 'AVAILABLE', 31, 0, 0, 0, 'offline', '学校东门', '15992236606', '2025-12-02 13:32:40', '2025-12-03 05:12:16');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_ofx66keruapi6vyqpv6f2or37`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, '超级管理员，拥有所有权限', 'ROLE_SUPER_ADMIN');
INSERT INTO `roles` VALUES (2, '普通管理员，拥有部分权限', 'ROLE_ADMIN');
INSERT INTO `roles` VALUES (3, '普通用户，拥有基本权限', 'ROLE_USER');

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `FKh8ciramu9cc9q3qcqiv4ue8a6`(`role_id` ASC) USING BTREE,
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_roles
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `student_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `school_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `campus_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER',
  `is_active` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'testuser2', '$2a$10$.BzAHkBB5C4a4xEgbtnHp.xlgcPZI0VegksLJBSj1mt0I/s7Q.TOe', 'test2@example.com', '13800138002', NULL, '????2', NULL, NULL, NULL, 'USER', 1, '2025-11-23 04:17:43', '2025-11-23 04:17:43');
INSERT INTO `users` VALUES (2, 'test1', '$2a$10$bIaN6KK4igFjMqGUyBKLkuWIg7cpzarLhzOhMsi8TLaXGti5J732e', 'zys6606@163.com', '15992236606', '/uploads/avatars/aa3ee26c-89ea-4653-9beb-36e21bb98458.jpg', '张三', NULL, NULL, NULL, 'USER', 1, '2025-11-23 04:18:31', '2025-11-23 11:26:21');
INSERT INTO `users` VALUES (3, 'test', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'test@example.com', '13800138000', '/uploads/avatars/0f42286b-75ca-46c7-9ead-bd7042b0fbb6.jpg', '????', NULL, NULL, NULL, 'USER', 1, '2025-11-23 12:29:33', '2025-11-30 11:47:31');
INSERT INTO `users` VALUES (4, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@campus.com', NULL, '/uploads/avatars/23208ad4-68e9-4f29-a4e0-7efc11c441c4.jpg', '张三', NULL, NULL, NULL, 'ADMIN', 1, '2025-11-23 12:51:42', '2025-11-30 03:27:38');
INSERT INTO `users` VALUES (5, 'buyer2', '$2a$10$jTaGTevqmEA103A3QvIznuDSTvDbySIvtVNvhT7cZHiOxgW/tNepK', 'buyer2@campus.com', '13800138004', NULL, '赵六', '20210005', '示例大学', '分校区', 'USER', 1, '2025-11-23 21:33:12', '2025-11-23 22:31:46');
INSERT INTO `users` VALUES (7, 'testuser', '$2a$10$jTaGTevqmEA103A3QvIznuDSTvDbySIvtVNvhT7cZHiOxgW/tNepK', 'testuser@example.com', '13800138000', NULL, 'Test User', NULL, NULL, NULL, 'USER', 1, '2025-11-23 13:28:29', '2025-11-23 13:28:29');
INSERT INTO `users` VALUES (8, 'test5', '$2a$10$op5SHOVJ5C6PKtDW3MrEHOgF90mpT.e1bKWbZX/GPI9YwzA0j6BGC', 'zys660@163.com', '15992236606', NULL, 'zs', NULL, NULL, NULL, 'USER', 1, '2025-11-29 16:33:01', '2025-12-03 05:14:06');

SET FOREIGN_KEY_CHECKS = 1;

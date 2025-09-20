/*
 Navicat Premium Dump SQL

 Source Server         : naibaozi
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : momo

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 20/09/2025 21:08:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for community_collect
-- ----------------------------
DROP TABLE IF EXISTS `community_collect`;
CREATE TABLE `community_collect`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID（主键）',
  `note_id` int NOT NULL COMMENT '关联笔记ID',
  `user_id` int NOT NULL COMMENT '收藏用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_note_user`(`note_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `fk_collect_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_collect_note` FOREIGN KEY (`note_id`) REFERENCES `community_note` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_collect_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_collect
-- ----------------------------

-- ----------------------------
-- Table structure for community_comment
-- ----------------------------
DROP TABLE IF EXISTS `community_comment`;
CREATE TABLE `community_comment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID（主键）',
  `note_id` int NOT NULL COMMENT '关联笔记ID（community_note.id）',
  `user_id` int NOT NULL COMMENT '评论用户ID（关联user_base.id）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `parent_id` int NULL DEFAULT 0 COMMENT '父评论ID（0=主评论，>0=回复）',
  `like_count` int NULL DEFAULT 0 COMMENT '评论点赞数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_note_id`(`note_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_note` FOREIGN KEY (`note_id`) REFERENCES `community_note` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_comment
-- ----------------------------

-- ----------------------------
-- Table structure for community_like
-- ----------------------------
DROP TABLE IF EXISTS `community_like`;
CREATE TABLE `community_like`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID（主键）',
  `note_id` int NOT NULL COMMENT '关联笔记ID',
  `user_id` int NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_note_user`(`note_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `fk_like_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_like_note` FOREIGN KEY (`note_id`) REFERENCES `community_note` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_like
-- ----------------------------

-- ----------------------------
-- Table structure for community_note
-- ----------------------------
DROP TABLE IF EXISTS `community_note`;
CREATE TABLE `community_note`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '笔记ID（主键）',
  `user_id` int NOT NULL COMMENT '发布用户ID（关联user_base.id）',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '笔记标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '笔记内容',
  `img_urls` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片URL（逗号分隔，最多9张）',
  `pet_type_id` int NOT NULL COMMENT '关联宠物品类ID（dict_pet_type.id）',
  `tag_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '标签ID（逗号分隔）',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1=正常，0=违规/删除）',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_pet_type_id`(`pet_type_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_note_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区笔记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_note
-- ----------------------------

-- ----------------------------
-- Table structure for consult_chat
-- ----------------------------
DROP TABLE IF EXISTS `consult_chat`;
CREATE TABLE `consult_chat`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '聊天ID（主键）',
  `order_id` int NOT NULL COMMENT '关联问诊订单ID（consult_order.id）',
  `send_user_id` int NOT NULL COMMENT '发送者ID（用户/医生，关联user_base.id）',
  `send_role` tinyint NOT NULL COMMENT '发送者角色（1=用户，2=医生）',
  `content_type` tinyint NOT NULL COMMENT '内容类型（1=文字，2=图片，3=语音）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容（文字/媒体URL）',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `fk_chat_order` FOREIGN KEY (`order_id`) REFERENCES `consult_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问诊聊天记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consult_chat
-- ----------------------------

-- ----------------------------
-- Table structure for consult_doctor
-- ----------------------------
DROP TABLE IF EXISTS `consult_doctor`;
CREATE TABLE `consult_doctor`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '医生ID（主键）',
  `user_id` int NOT NULL COMMENT '关联user_base.id（医生的用户账号）',
  `real_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名（认证用）',
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '职称（如“异宠执业医师”）',
  `good_at_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '擅长品类（dict_pet_type.id逗号分隔）',
  `qualification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资质证书URL（审核用）',
  `consultation_fee` decimal(10, 2) NOT NULL COMMENT '单次问诊费（如59.90）',
  `receive_status` tinyint NULL DEFAULT 1 COMMENT '接诊状态（1=可接诊，0=休息中）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_doctor_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问诊医生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consult_doctor
-- ----------------------------

-- ----------------------------
-- Table structure for consult_order
-- ----------------------------
DROP TABLE IF EXISTS `consult_order`;
CREATE TABLE `consult_order`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '问诊订单ID（主键）',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号（唯一，如CON20241001001）',
  `user_id` int NOT NULL COMMENT '用户ID（关联user_base.id）',
  `pet_id` int NOT NULL COMMENT '问诊宠物ID（关联user_pet.id）',
  `doctor_id` int NOT NULL COMMENT '接诊医生ID（关联consult_doctor.id）',
  `consult_type` tinyint NOT NULL COMMENT '问诊类型（1=普通，2=紧急）',
  `symptom` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '症状描述（用户填写）',
  `media_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '症状媒体URL（照片/视频逗号分隔）',
  `amount` decimal(10, 2) NOT NULL COMMENT '应付金额（问诊费-优惠券）',
  `coupon_id` int NULL DEFAULT 0 COMMENT '优惠券ID（0=未使用）',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态（0=未支付，1=已支付）',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `consult_status` tinyint NULL DEFAULT 0 COMMENT '问诊状态（0=待接诊，1=接诊中，2=已完成，3=已取消）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `fk_consult_pet`(`pet_id` ASC) USING BTREE,
  CONSTRAINT `fk_consult_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `consult_doctor` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_consult_pet` FOREIGN KEY (`pet_id`) REFERENCES `user_pet` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_consult_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问诊订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consult_order
-- ----------------------------

-- ----------------------------
-- Table structure for dict_pet_type
-- ----------------------------
DROP TABLE IF EXISTS `dict_pet_type`;
CREATE TABLE `dict_pet_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '字典ID（主键）',
  `type_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '品类名称（如“爬宠-鬃狮蜥”“啮齿-金丝熊”）',
  `parent_id` int NOT NULL DEFAULT 0 COMMENT '父级ID（0=一级品类，>0=二级品类）',
  `sort` int NULL DEFAULT 0 COMMENT '排序（热门品类靠前）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_name`(`type_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宠物品类数据字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict_pet_type
-- ----------------------------
INSERT INTO `dict_pet_type` VALUES (1, '爬宠', 0, 1);
INSERT INTO `dict_pet_type` VALUES (2, '鬃狮蜥', 1, 1);
INSERT INTO `dict_pet_type` VALUES (3, '豹纹守宫', 1, 2);
INSERT INTO `dict_pet_type` VALUES (4, '玉米蛇', 1, 3);
INSERT INTO `dict_pet_type` VALUES (5, '角蛙', 1, 4);
INSERT INTO `dict_pet_type` VALUES (6, '啮齿类', 0, 2);
INSERT INTO `dict_pet_type` VALUES (7, '金丝熊', 6, 1);
INSERT INTO `dict_pet_type` VALUES (8, '仓鼠', 6, 2);
INSERT INTO `dict_pet_type` VALUES (9, '蜜袋鼯', 6, 3);
INSERT INTO `dict_pet_type` VALUES (10, '花枝鼠', 6, 4);
INSERT INTO `dict_pet_type` VALUES (11, '鸟类', 0, 3);
INSERT INTO `dict_pet_type` VALUES (12, '玄凤鹦鹉', 11, 1);
INSERT INTO `dict_pet_type` VALUES (13, '牡丹鹦鹉', 11, 2);
INSERT INTO `dict_pet_type` VALUES (14, '文鸟', 11, 3);
INSERT INTO `dict_pet_type` VALUES (15, '虎皮鹦鹉', 11, 4);

-- ----------------------------
-- Table structure for mall_cart
-- ----------------------------
DROP TABLE IF EXISTS `mall_cart`;
CREATE TABLE `mall_cart`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '购物车ID（主键）',
  `user_id` int NOT NULL COMMENT '用户ID（关联user_base.id）',
  `goods_id` int NOT NULL COMMENT '商品ID（关联mall_goods.id）',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量（默认1，限制最大5）',
  `select_status` tinyint NULL DEFAULT 1 COMMENT '是否选中（1=是，结算时勾选）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_goods`(`user_id` ASC, `goods_id` ASC) USING BTREE,
  INDEX `fk_cart_goods`(`goods_id` ASC) USING BTREE,
  CONSTRAINT `fk_cart_goods` FOREIGN KEY (`goods_id`) REFERENCES `mall_goods` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_cart
-- ----------------------------

-- ----------------------------
-- Table structure for mall_category
-- ----------------------------
DROP TABLE IF EXISTS `mall_category`;
CREATE TABLE `mall_category`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID（主键）',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称（如“爬宠-饲养箱”）',
  `parent_id` int NOT NULL DEFAULT 0 COMMENT '父级ID（0=一级分类，>0=二级分类）',
  `sort` int NULL DEFAULT 0 COMMENT '排序（靠前展示）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1=启用，0=下架）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商城商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_category
-- ----------------------------
INSERT INTO `mall_category` VALUES (1, '爬宠用品', 0, 1, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (2, '饲养箱', 1, 1, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (3, '加热设备', 1, 2, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (4, '垫材', 1, 3, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (5, '喂食器', 1, 4, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (6, '啮齿类用品', 0, 2, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (7, '笼子', 6, 1, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (8, '粮食', 6, 2, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (9, '玩具', 6, 3, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (10, '浴室', 6, 4, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (11, '鸟类用品', 0, 3, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (12, '鸟笼', 11, 1, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (13, '鸟食', 11, 2, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (14, '站架', 11, 3, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');
INSERT INTO `mall_category` VALUES (15, '清洁用品', 11, 4, 1, '2025-09-20 10:57:02', '2025-09-20 10:57:02');

-- ----------------------------
-- Table structure for mall_coupon
-- ----------------------------
DROP TABLE IF EXISTS `mall_coupon`;
CREATE TABLE `mall_coupon`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '优惠券ID（主键）',
  `coupon_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优惠券名称（如“满50减10”）',
  `type` tinyint NOT NULL COMMENT '类型（1=满减券，2=折扣券）',
  `value` decimal(10, 2) NOT NULL COMMENT '满减金额（如10.00）或折扣比例（如0.9=9折）',
  `min_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '使用门槛（0=无门槛）',
  `start_time` datetime NOT NULL COMMENT '生效时间',
  `end_time` datetime NOT NULL COMMENT '失效时间',
  `total` int NOT NULL COMMENT '总数量',
  `remain` int NOT NULL COMMENT '剩余数量',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1=可用，0=不可用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for mall_goods
-- ----------------------------
DROP TABLE IF EXISTS `mall_goods`;
CREATE TABLE `mall_goods`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID（主键）',
  `goods_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称（含适用品类，如“鬃狮蜥专用加热垫”）',
  `category_id` int NOT NULL COMMENT '分类ID（关联mall_category.id）',
  `pet_type_ids` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '适用宠物品类（dict_pet_type.id逗号分隔）',
  `price` decimal(10, 2) NOT NULL COMMENT '售价',
  `original_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '原价（划线价）',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存数量',
  `spec` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '规格（如“20cm×15cm”）',
  `main_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主图URL（列表页展示）',
  `detail_img` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '详情图URL（逗号分隔）',
  `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '养护小贴士（TEXT无默认值）',
  `doctor_recommend` tinyint NULL DEFAULT 0 COMMENT '是否医生推荐（1=是，0=否）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1=上架，0=下架）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_goods_category` FOREIGN KEY (`category_id`) REFERENCES `mall_category` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_goods
-- ----------------------------

-- ----------------------------
-- Table structure for mall_logistics_trace
-- ----------------------------
DROP TABLE IF EXISTS `mall_logistics_trace`;
CREATE TABLE `mall_logistics_trace`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '轨迹ID（主键）',
  `order_id` int NOT NULL COMMENT '关联商城订单ID',
  `logistics_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物流单号',
  `logistics_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快递公司编码',
  `logistics_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快递公司名称',
  `trace_time` datetime NOT NULL COMMENT '物流节点时间',
  `trace_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物流节点内容（如“【深圳市】已揽收”）',
  `trace_status` tinyint NOT NULL COMMENT '节点对应状态（0=待揽收，1=已揽收，2=运输中，3=派送中，4=已签收，5=异常）',
  `location_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '物流节点详细地址',
  `location_lng` decimal(10, 6) NULL DEFAULT NULL COMMENT '节点经度',
  `location_lat` decimal(10, 6) NULL DEFAULT NULL COMMENT '节点纬度',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_logistics_no`(`logistics_no` ASC) USING BTREE,
  INDEX `idx_trace_time`(`trace_time` ASC) USING BTREE,
  CONSTRAINT `fk_logistics_order` FOREIGN KEY (`order_id`) REFERENCES `mall_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物流轨迹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_logistics_trace
-- ----------------------------

-- ----------------------------
-- Table structure for mall_order
-- ----------------------------
DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID（主键）',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号（如MALL20241001001）',
  `user_id` int NOT NULL COMMENT '用户ID（关联user_base.id）',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额（商品+运费-优惠券）',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `freight` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '运费（满XX元包邮）',
  `coupon_id` int NULL DEFAULT 0 COMMENT '优惠券ID（0=未使用）',
  `receiver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `receiver_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货详细地址',
  `receiver_province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '收货省份',
  `receiver_city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '收货城市',
  `receiver_district` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '收货区/县',
  `order_status` tinyint NULL DEFAULT 0 COMMENT '订单状态（0=待付款，1=待发货，2=待收货，3=已完成，4=已取消）',
  `logistics_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '物流单号',
  `logistics_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '快递公司编码（如SF=顺丰）',
  `logistics_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '快递公司名称',
  `logistics_status` tinyint NULL DEFAULT 0 COMMENT '物流状态（0=待发货，1=已揽收，2=运输中，3=派送中，4=已签收，5=异常）',
  `pay_type` tinyint NULL DEFAULT 0 COMMENT '支付方式（1=微信支付，2=支付宝）',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认收货时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '订单备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_status`(`order_status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商城订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_order
-- ----------------------------

-- ----------------------------
-- Table structure for mall_order_item
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_item`;
CREATE TABLE `mall_order_item`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '明细ID（主键）',
  `order_id` int NOT NULL COMMENT '关联订单ID',
  `goods_id` int NOT NULL COMMENT '商品ID',
  `goods_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称（下单时快照）',
  `goods_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品图片（下单时快照）',
  `price` decimal(10, 2) NOT NULL COMMENT '购买单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `spec_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '规格信息（如颜色、尺寸）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_goods_id`(`goods_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `mall_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_order_item
-- ----------------------------

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '通知ID（主键）',
  `user_id` int NOT NULL COMMENT '接收用户ID（关联user_base.id）',
  `notice_type` tinyint NOT NULL COMMENT '通知类型（1=问诊提醒，2=订单提醒，3=社区互动，4=系统通知）',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `related_id` int NULL DEFAULT 0 COMMENT '关联ID（如订单ID/笔记ID）',
  `related_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '关联类型（如\"order\"/\"note\"）',
  `is_read` tinyint NULL DEFAULT 0 COMMENT ' 是否已读（0=未读，1=已读）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_notice_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典类型（如\"pet_gender\"/\"order_status\"）',
  `dict_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典编码（如\"male\"/\"female\"）',
  `dict_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典值（如\"公\"/\"母\"）',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_code`(`dict_type` ASC, `dict_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统通用数据字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, 'pet_gender', 'unknown', '未知', 0, 1);
INSERT INTO `sys_dict` VALUES (2, 'pet_gender', 'male', '公', 1, 1);
INSERT INTO `sys_dict` VALUES (3, 'pet_gender', 'female', '母', 2, 1);

-- ----------------------------
-- Table structure for user_base
-- ----------------------------
DROP TABLE IF EXISTS `user_base`;
CREATE TABLE `user_base`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号（登录账号）',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码（MD5+盐值）',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '社区展示昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像URL（微信头像或自定义）',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '账号状态（1=正常，0=禁用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_base
-- ----------------------------
INSERT INTO `user_base` VALUES (1, 'naibaozi', '15511690813', '$2a$10$FZz0XrlKTsCHueOMVDG4j.Q84CpuVl8z/RQGWlNKT1ZZWc9YsfLLa', 'mo小mo', 'https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132', 1, '2025-09-20 16:21:46', '2025-09-20 20:21:37', '1258899660@qq.com', 'oJAyz7So4S0vdoicq_eEwCHm7RCo');
INSERT INTO `user_base` VALUES (2, 'naibaozi2', '', '$2a$10$oqMLkVeF2C5/D5R/Te.tvuBkLUhvgzR6W57p5KPl9QuuoyszhdVPi', '微信用户', 'https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132', 1, '2025-09-20 20:28:17', '2025-09-20 20:29:39', 'mzqt666@163.com', 'oJAyz7So4S0vdoicq_eEwCHm7RCo');
INSERT INTO `user_base` VALUES (6, 'naibaozi3', '', '$2a$10$CCoIIEqI1FPMgityMG1QZeYyTIiQ.pDnfDylhNmNk3avdmsdxjNkS', 'naibaozi3', 'undefined', 1, '2025-09-20 20:38:54', '2025-09-20 20:41:26', '646566131@qq.com', 'oJAyz7So4S0vdoicq_eEwCHm7RCo');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID（主键）',
  `user_id` int NOT NULL COMMENT '用户ID（关联user_base.id）',
  `coupon_id` int NOT NULL COMMENT '优惠券ID（关联mall_coupon.id）',
  `get_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `order_id` int NULL DEFAULT 0 COMMENT '使用的订单ID（0=未使用）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1=未使用，2=已使用，3=已过期）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_user_coupon_coupon`(`coupon_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `mall_coupon` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_coupon_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for user_pet
-- ----------------------------
DROP TABLE IF EXISTS `user_pet`;
CREATE TABLE `user_pet`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '宠物ID（主键）',
  `user_id` int NOT NULL COMMENT '所属用户ID（关联user_base.id）',
  `pet_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宠物昵称（如“小鬃狮”）',
  `pet_type_id` int NOT NULL COMMENT '宠物品类ID（关联dict_pet_type.id）',
  `pet_age` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '宠物年龄（如“8个月”，文本更灵活）',
  `pet_gender` tinyint NULL DEFAULT 0 COMMENT '宠物性别（0=未知，1=公，2=母）',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '过往病史（TEXT无默认值，空为NULL）',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认宠物（1=是，发起问诊默认选中）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_pet_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户的宠物信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_pet
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

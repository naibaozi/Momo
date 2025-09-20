-- 异宠小程序数据库完整SQL脚本
-- 解决问题：1. 表已存在冲突 2. TEXT字段无默认值限制
-- 适用MySQL版本：5.7及以上

-- 第一步：先删除旧数据库（若需保留数据，跳过此步，直接用方案2的CREATE TABLE IF NOT EXISTS）
-- 注意：DROP会清空所有数据，首次搭建或重置时使用
DROP DATABASE IF EXISTS Momo;

-- 第二步：创建新数据库并指定字符集（避免中文乱码）
CREATE DATABASE IF NOT EXISTS Momo 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
USE Momo;

-- ###########################################################################
-- 1. 基础数据字典表（宠物品类，统一管理分类）
-- ###########################################################################
CREATE TABLE IF NOT EXISTS `dict_pet_type` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '字典ID（主键）',
  `type_name` VARCHAR(30) NOT NULL COMMENT '品类名称（如“爬宠-鬃狮蜥”“啮齿-金丝熊”）',
  `parent_id` INT NOT NULL DEFAULT 0 COMMENT '父级ID（0=一级品类，>0=二级品类）',
  `sort` INT DEFAULT 0 COMMENT '排序（热门品类靠前）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_name` (`type_name`)  -- 品类名称唯一，避免重复
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物品类数据字典表';

-- ###########################################################################
-- 2. 用户体系相关表
-- ###########################################################################
-- 2.1 用户基础信息表
CREATE TABLE IF NOT EXISTS `user_base` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `phone` VARCHAR(11) NOT NULL COMMENT '手机号（登录账号）',
  `password` VARCHAR(64) NOT NULL COMMENT '加密密码（MD5+盐值）',
  `nickname` VARCHAR(30) NOT NULL COMMENT '社区展示昵称',
  `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像URL（微信头像或自定义）',
  `status` TINYINT DEFAULT 1 COMMENT '账号状态（1=正常，0=禁用）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)  -- 手机号唯一，避免重复注册
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础信息表';

-- 2.2 异宠档案表（用户的宠物信息）
CREATE TABLE IF NOT EXISTS `user_pet` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '宠物ID（主键）',
  `user_id` INT NOT NULL COMMENT '所属用户ID（关联user_base.id）',
  `pet_name` VARCHAR(20) NOT NULL COMMENT '宠物昵称（如“小鬃狮”）',
  `pet_type_id` INT NOT NULL COMMENT '宠物品类ID（关联dict_pet_type.id）',
  `pet_age` VARCHAR(10) DEFAULT '' COMMENT '宠物年龄（如“8个月”，文本更灵活）',
  `pet_gender` TINYINT DEFAULT 0 COMMENT '宠物性别（0=未知，1=公，2=母）',
  `medical_history` TEXT COMMENT '过往病史（TEXT无默认值，空为NULL）',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认宠物（1=是，发起问诊默认选中）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),  -- 按用户ID查宠物列表
  CONSTRAINT `fk_pet_user` FOREIGN KEY (`user_id`) 
  REFERENCES `user_base` (`id`) ON DELETE CASCADE  -- 用户删除，宠物档案同步删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户的宠物信息表';

-- ###########################################################################
-- 3. 问诊系统相关表（核心业务）
-- ###########################################################################
-- 3.1 医生信息表（医生也是用户，单独存资质）
CREATE TABLE IF NOT EXISTS `consult_doctor` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '医生ID（主键）',
  `user_id` INT NOT NULL COMMENT '关联user_base.id（医生的用户账号）',
  `real_name` VARCHAR(20) NOT NULL COMMENT '真实姓名（认证用）',
  `title` VARCHAR(30) DEFAULT '' COMMENT '职称（如“异宠执业医师”）',
  `good_at_type` VARCHAR(100) NOT NULL COMMENT '擅长品类（dict_pet_type.id逗号分隔）',
  `qualification` VARCHAR(255) NOT NULL COMMENT '资质证书URL（审核用）',
  `consultation_fee` DECIMAL(10,2) NOT NULL COMMENT '单次问诊费（如59.90）',
  `receive_status` TINYINT DEFAULT 1 COMMENT '接诊状态（1=可接诊，0=休息中）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),  -- 一个用户只能对应一个医生账号
  CONSTRAINT `fk_doctor_user` FOREIGN KEY (`user_id`) 
  REFERENCES `user_base` (`id`) ON DELETE CASCADE  -- 用户删除，医生资质同步删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊医生信息表';

-- 3.2 问诊订单表（问诊交易核心）
CREATE TABLE IF NOT EXISTS `consult_order` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '问诊订单ID（主键）',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（唯一，如CON20241001001）',
  `user_id` INT NOT NULL COMMENT '用户ID（关联user_base.id）',
  `pet_id` INT NOT NULL COMMENT '问诊宠物ID（关联user_pet.id）',
  `doctor_id` INT NOT NULL COMMENT '接诊医生ID（关联consult_doctor.id）',
  `consult_type` TINYINT NOT NULL COMMENT '问诊类型（1=普通，2=紧急）',
  `symptom` TEXT NOT NULL COMMENT '症状描述（用户填写）',
  `media_url` VARCHAR(512) DEFAULT '' COMMENT '症状媒体URL（照片/视频逗号分隔）',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '应付金额（问诊费-优惠券）',
  `coupon_id` INT DEFAULT 0 COMMENT '优惠券ID（0=未使用）',
  `pay_status` TINYINT DEFAULT 0 COMMENT '支付状态（0=未支付，1=已支付）',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `consult_status` TINYINT DEFAULT 0 COMMENT '问诊状态（0=待接诊，1=接诊中，2=已完成，3=已取消）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),  -- 订单编号唯一
  KEY `idx_user_id` (`user_id`),  -- 按用户查问诊订单
  KEY `idx_doctor_id` (`doctor_id`),  -- 按医生查接诊订单
  CONSTRAINT `fk_consult_user` FOREIGN KEY (`user_id`) 
  REFERENCES `user_base` (`id`) ON DELETE CASCADE,  -- 用户删除，订单同步删除
  CONSTRAINT `fk_consult_pet` FOREIGN KEY (`pet_id`) 
  REFERENCES `user_pet` (`id`) ON DELETE CASCADE,  -- 宠物删除，关联订单同步删除
  CONSTRAINT `fk_consult_doctor` FOREIGN KEY (`doctor_id`) 
  REFERENCES `consult_doctor` (`id`) ON DELETE RESTRICT  -- 医生账号删除前需先处理订单
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊订单表';

-- 3.3 问诊聊天记录表（问诊沟通内容）
CREATE TABLE IF NOT EXISTS `consult_chat` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '聊天ID（主键）',
  `order_id` INT NOT NULL COMMENT '关联问诊订单ID（consult_order.id）',
  `send_user_id` INT NOT NULL COMMENT '发送者ID（用户/医生，关联user_base.id）',
  `send_role` TINYINT NOT NULL COMMENT '发送者角色（1=用户，2=医生）',
  `content_type` TINYINT NOT NULL COMMENT '内容类型（1=文字，2=图片，3=语音）',
  `content` TEXT NOT NULL COMMENT '内容（文字/媒体URL）',
  `send_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),  -- 按订单ID查聊天记录
  CONSTRAINT `fk_chat_order` FOREIGN KEY (`order_id`) 
  REFERENCES `consult_order` (`id`) ON DELETE CASCADE  -- 订单删除，聊天记录同步删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊聊天记录表';

-- ###########################################################################
-- 4. 商城系统相关表（变现核心）
-- ###########################################################################
-- 4.1 商品分类表
CREATE TABLE IF NOT EXISTS `mall_category` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '分类ID（主键）',
  `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称（如“爬宠-饲养箱”）',
  `parent_id` INT NOT NULL DEFAULT 0 COMMENT '父级ID（0=一级分类，>0=二级分类）',
  `sort` INT DEFAULT 0 COMMENT '排序（靠前展示）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=下架）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城商品分类表';

-- 4.2 商品表
CREATE TABLE IF NOT EXISTS `mall_goods` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '商品ID（主键）',
  `goods_name` VARCHAR(100) NOT NULL COMMENT '商品名称（含适用品类，如“鬃狮蜥专用加热垫”）',
  `category_id` INT NOT NULL COMMENT '分类ID（关联mall_category.id）',
  `pet_type_ids` VARCHAR(100) NOT NULL COMMENT '适用宠物品类（dict_pet_type.id逗号分隔）',
  `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
  `original_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价（划线价）',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `spec` VARCHAR(50) DEFAULT '' COMMENT '规格（如“20cm×15cm”）',
  `main_img` VARCHAR(255) NOT NULL COMMENT '主图URL（列表页展示）',
  `detail_img` VARCHAR(1024) DEFAULT '' COMMENT '详情图URL（逗号分隔）',
  `tips` TEXT COMMENT '养护小贴士（TEXT无默认值）',
  `doctor_recommend` TINYINT DEFAULT 0 COMMENT '是否医生推荐（1=是，0=否）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1=上架，0=下架）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),  -- 按分类查商品
  CONSTRAINT `fk_goods_category` FOREIGN KEY (`category_id`) 
  REFERENCES `mall_category` (`id`) ON DELETE CASCADE  -- 分类删除，商品同步下架
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 4.3 购物车表
CREATE TABLE IF NOT EXISTS `mall_cart` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '购物车ID（主键）',
  `user_id` INT NOT NULL COMMENT '用户ID（关联user_base.id）',
  `goods_id` INT NOT NULL COMMENT '商品ID（关联mall_goods.id）',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量（默认1，限制最大5）',
  `select_status` TINYINT DEFAULT 1 COMMENT '是否选中（1=是，结算时勾选）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_goods` (`user_id`,`goods_id`),  -- 同一商品在购物车只存一条
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) 
  REFERENCES `user_base` (`id`) ON DELETE CASCADE,  -- 用户删除，购物车清空
  CONSTRAINT `fk_cart_goods` FOREIGN KEY (`goods_id`) 
  REFERENCES `mall_goods` (`id`) ON DELETE CASCADE  -- 商品下架，购物车同步删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 4.4 优惠券表
CREATE TABLE IF NOT EXISTS `mall_coupon` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID（主键）',
  `coupon_name` VARCHAR(50) NOT NULL COMMENT '优惠券名称（如“满50减10”）',
  `type` TINYINT NOT NULL COMMENT '类型（1=满减券，2=折扣券）',
  `value` DECIMAL(10,2) NOT NULL COMMENT '满减金额（如10.00）或折扣比例（如0.9=9折）',
  `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '使用门槛（0=无门槛）',
  `start_time` DATETIME NOT NULL COMMENT '生效时间',
  `end_time` DATETIME NOT NULL COMMENT '失效时间',
  `total` INT NOT NULL COMMENT '总数量',
  `remain` INT NOT NULL COMMENT '剩余数量',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1=可用，0=不可用）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 4.5 用户优惠券关联表（用户领取的优惠券）
CREATE TABLE IF NOT EXISTS `user_coupon` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'ID（主键）',
  `user_id` INT NOT NULL COMMENT '用户ID（关联user_base.id）',
  `coupon_id` INT NOT NULL COMMENT '优惠券ID（关联mall_coupon.id）',
  `get_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `order_id` INT DEFAULT 0 COMMENT '使用的订单ID（0=未使用）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1=未使用，2=已使用，3=已过期）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),  -- 按用户查优惠券
  CONSTRAINT `fk_user_coupon_user` FOREIGN KEY (`user_id`) 
  REFERENCES `user_base` (`id`) ON DELETE CASCADE,  -- 用户删除，优惠券同步删除
  CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) 
  REFERENCES `mall_coupon` (`id`) ON DELETE CASCADE  -- 优惠券失效，关联记录同步删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券关联表';

-- 4.6 商城订单表（完整定义）
CREATE TABLE IF NOT EXISTS `mall_order` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '订单ID（主键）',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（如MALL20241001001）',
  `user_id` INT NOT NULL COMMENT '用户ID（关联user_base.id）',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额（商品+运费-优惠券）',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `freight` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费（满XX元包邮）',
  `coupon_id` INT DEFAULT 0 COMMENT '优惠券ID（0=未使用）',
  `receiver_name` VARCHAR(20) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(11) NOT NULL COMMENT '收货人电话',
  `receiver_addr` VARCHAR(255) NOT NULL COMMENT '收货详细地址',
  `receiver_province` VARCHAR(20) DEFAULT '' COMMENT '收货省份',
  `receiver_city` VARCHAR(20) DEFAULT '' COMMENT '收货城市',
  `receiver_district` VARCHAR(20) DEFAULT '' COMMENT '收货区/县',
  `order_status` TINYINT DEFAULT 0 COMMENT '订单状态（0=待付款，1=待发货，2=待收货，3=已完成，4=已取消）',
  `logistics_no` VARCHAR(50) DEFAULT '' COMMENT '物流单号',
  `logistics_code` VARCHAR(20) DEFAULT '' COMMENT '快递公司编码（如SF=顺丰）',
  `logistics_name` VARCHAR(30) DEFAULT '' COMMENT '快递公司名称',
  `logistics_status` TINYINT DEFAULT 0 COMMENT '物流状态（0=待发货，1=已揽收，2=运输中，3=派送中，4=已签收，5=异常）',
  `pay_type` TINYINT DEFAULT 0 COMMENT '支付方式（1=微信支付，2=支付宝）',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `confirm_time` DATETIME DEFAULT NULL COMMENT '确认收货时间',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `remark` VARCHAR(512) DEFAULT '' COMMENT '订单备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单表';

-- 4.7 订单明细表
CREATE TABLE IF NOT EXISTS `mall_order_item` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '明细ID（主键）',
  `order_id` INT NOT NULL COMMENT '关联订单ID',
  `goods_id` INT NOT NULL COMMENT '商品ID',
  `goods_name` VARCHAR(100) NOT NULL COMMENT '商品名称（下单时快照）',
  `goods_img` VARCHAR(255) NOT NULL COMMENT '商品图片（下单时快照）',
  `price` DECIMAL(10,2) NOT NULL COMMENT '购买单价',
  `quantity` INT NOT NULL COMMENT '购买数量',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  `spec_info` VARCHAR(100) DEFAULT '' COMMENT '规格信息（如颜色、尺寸）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_goods_id` (`goods_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `mall_order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 4.8 物流轨迹表
CREATE TABLE IF NOT EXISTS `mall_logistics_trace` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '轨迹ID（主键）',
  `order_id` INT NOT NULL COMMENT '关联商城订单ID',
  `logistics_no` VARCHAR(50) NOT NULL COMMENT '物流单号',
  `logistics_code` VARCHAR(20) NOT NULL COMMENT '快递公司编码',
  `logistics_name` VARCHAR(30) NOT NULL COMMENT '快递公司名称',
  `trace_time` DATETIME NOT NULL COMMENT '物流节点时间',
  `trace_content` VARCHAR(255) NOT NULL COMMENT '物流节点内容（如“【深圳市】已揽收”）',
  `trace_status` TINYINT NOT NULL COMMENT '节点对应状态（0=待揽收，1=已揽收，2=运输中，3=派送中，4=已签收，5=异常）',
  `location_addr` VARCHAR(255) DEFAULT '' COMMENT '物流节点详细地址',
  `location_lng` DECIMAL(10,6) DEFAULT NULL COMMENT '节点经度',
  `location_lat` DECIMAL(10,6) DEFAULT NULL COMMENT '节点纬度',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_trace_time` (`trace_time`),
  CONSTRAINT `fk_logistics_order` FOREIGN KEY (`order_id`) REFERENCES `mall_order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

-- ###########################################################################
-- 5. 社区系统相关表（用户互动）
-- ###########################################################################
-- 5.1 社区笔记表
CREATE TABLE IF NOT EXISTS `community_note` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '笔记ID（主键）',
  `user_id` INT NOT NULL COMMENT '发布用户ID（关联user_base.id）',
  `title` VARCHAR(100) DEFAULT '' COMMENT '笔记标题',
  `content` TEXT NOT NULL COMMENT '笔记内容',
  `img_urls` VARCHAR(1024) DEFAULT '' COMMENT '图片URL（逗号分隔，最多9张）',
  `pet_type_id` INT NOT NULL COMMENT '关联宠物品类ID（dict_pet_type.id）',
  `tag_ids` VARCHAR(100) DEFAULT '' COMMENT '标签ID（逗号分隔）',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `collect_count` INT DEFAULT 0 COMMENT '收藏数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1=正常，0=违规/删除）',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_type_id` (`pet_type_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_note_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区笔记表';

-- 5.2 笔记评论表
CREATE TABLE IF NOT EXISTS `community_comment` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '评论ID（主键）',
  `note_id` INT NOT NULL COMMENT '关联笔记ID（community_note.id）',
  `user_id` INT NOT NULL COMMENT '评论用户ID（关联user_base.id）',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` INT DEFAULT 0 COMMENT '父评论ID（0=主评论，>0=回复）',
  `like_count` INT DEFAULT 0 COMMENT '评论点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_note_id` (`note_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_comment_note` FOREIGN KEY (`note_id`) REFERENCES `community_note` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记评论表';

-- 5.3 笔记点赞表
CREATE TABLE IF NOT EXISTS `community_like` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'ID（主键）',
  `note_id` INT NOT NULL COMMENT '关联笔记ID',
  `user_id` INT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_note_user` (`note_id`,`user_id`), -- 同一用户对同一笔记只能点赞一次
  CONSTRAINT `fk_like_note` FOREIGN KEY (`note_id`) REFERENCES `community_note` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记点赞表';

-- 5.4 笔记收藏表
CREATE TABLE IF NOT EXISTS `community_collect` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'ID（主键）',
  `note_id` INT NOT NULL COMMENT '关联笔记ID',
  `user_id` INT NOT NULL COMMENT '收藏用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_note_user` (`note_id`,`user_id`), -- 同一用户对同一笔记只能收藏一次
  CONSTRAINT `fk_collect_note` FOREIGN KEY (`note_id`) REFERENCES `community_note` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_collect_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记收藏表';

-- ###########################################################################
-- 6. 系统通用表
-- ###########################################################################
-- 6.1 消息通知表
CREATE TABLE IF NOT EXISTS `notice` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '通知ID（主键）',
  `user_id` INT NOT NULL COMMENT '接收用户ID（关联user_base.id）',
  `notice_type` TINYINT NOT NULL COMMENT '通知类型（1=问诊提醒，2=订单提醒，3=社区互动，4=系统通知）',
  `title` VARCHAR(50) NOT NULL COMMENT '通知标题',
  `content` VARCHAR(255) NOT NULL COMMENT '通知内容',
  `related_id` INT DEFAULT 0 COMMENT '关联ID（如订单ID/笔记ID）',
  `related_type` VARCHAR(20) DEFAULT '' COMMENT '关联类型（如"order"/"note"）',
  `is_read` TINYINT DEFAULT 0 COMMENT ' 是否已读（0=未读，1=已读）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_notice_user` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 6.2 数据字典表（通用分类）
CREATE TABLE IF NOT EXISTS `sys_dict` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` VARCHAR(50) NOT NULL COMMENT '字典类型（如"pet_gender"/"order_status"）',
  `dict_code` VARCHAR(30) NOT NULL COMMENT '字典编码（如"male"/"female"）',
  `dict_value` VARCHAR(50) NOT NULL COMMENT '字典值（如"公"/"母"）',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`dict_type`,`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通用数据字典表';

-- ###########################################################################
-- 初始化基础数据
-- ###########################################################################
-- 初始化宠物品类数据
INSERT INTO `dict_pet_type` (`type_name`, `parent_id`, `sort`) VALUES
('爬宠', 0, 1),
('鬃狮蜥', 1, 1),
('豹纹守宫', 1, 2),
('玉米蛇', 1, 3),
('角蛙', 1, 4),
('啮齿类', 0, 2),
('金丝熊', 6, 1),
('仓鼠', 6, 2),
('蜜袋鼯', 6, 3),
('花枝鼠', 6, 4),
('鸟类', 0, 3),
('玄凤鹦鹉', 11, 1),
('牡丹鹦鹉', 11, 2),
('文鸟', 11, 3),
('虎皮鹦鹉', 11, 4);

-- 初始化商品分类数据
INSERT INTO `mall_category` (`category_name`, `parent_id`, `sort`, `status`) VALUES
('爬宠用品', 0, 1, 1),
('饲养箱', 1, 1, 1),
('加热设备', 1, 2, 1),
('垫材', 1, 3, 1),
('喂食器', 1, 4, 1),
('啮齿类用品', 0, 2, 1),
('笼子', 6, 1, 1),
('粮食', 6, 2, 1),
('玩具', 6, 3, 1),
('浴室', 6, 4, 1),
('鸟类用品', 0, 3, 1),
('鸟笼', 11, 1, 1),
('鸟食', 11, 2, 1),
('站架', 11, 3, 1),
('清洁用品', 11, 4, 1);

-- 初始化系统字典数据（宠物性别）
INSERT INTO `sys_dict` (`dict_type`, `dict_code`, `dict_value`, `sort`, `status`) VALUES
('pet_gender', 'unknown', '未知', 0, 1),
('pet_gender', 'male', '公', 1, 1),
('pet_gender', 'female', '母', 2, 1);

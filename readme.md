# 宠物相关系统数据库表结构说明
## 概述
本数据库（schema：momo）基于MySQL 8.0.39构建，主要支撑宠物相关平台的核心业务，涵盖用户管理、社区互动、在线问诊、商城交易及系统通知五大核心模块，共包含21张数据表。所有表均采用`utf8mb4`字符集及`utf8mb4_0900_ai_ci`排序规则，支持emoji存储，引擎统一为InnoDB以保障事务一致性和外键完整性。


## 1. 用户管理模块
### 1.1 user_base（用户基础信息表）
| 字段名       | 数据类型         | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int              | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 用户唯一标识ID                            |
| username     | varchar(50)      | 否     | -                                       | -               | 用户名                                    |
| phone        | varchar(11)      | 是     | NULL                                    | -               | 手机号（作为登录账号）                    |
| password     | varchar(64)      | 否     | -                                       | -               | 加密密码（采用MD5+盐值方式存储）          |
| nickname     | varchar(30)      | 否     | -                                       | -               | 社区展示昵称                              |
| avatar       | varchar(255)     | 是     | ''                                      | -               | 头像URL（支持微信头像或自定义上传）        |
| status       | tinyint UNSIGNED | 否     | 1                                       | -               | 账号状态（1=正常，0=禁用）                |
| create_time  | datetime         | 是     | CURRENT_TIMESTAMP                       | -               | 账号创建时间                              |
| update_time  | datetime         | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 账号更新时间（自动触发）                  |
| email        | varchar(255)     | 是     | NULL                                    | -               | 用户邮箱                                  |
| open_id      | varchar(255)     | 是     | NULL                                    | -               | 微信开放平台ID（用于微信登录关联）        |

### 1.2 user_pet（用户的宠物信息表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 宠物唯一标识ID                            |
| user_id      | int        | 否     | -                                       | 索引（BTREE）   | 所属用户ID，关联`user_base.id`            |
| pet_name     | varchar(20)| 否     | -                                       | -               | 宠物昵称（如“小鬃狮”）                    |
| pet_type_id  | int        | 否     | -                                       | -               | 宠物品类ID，关联`dict_pet_type.id`        |
| pet_age      | varchar(10)| 是     | ''                                      | -               | 宠物年龄（文本格式，如“8个月”）           |
| pet_gender   | tinyint    | 是     | 0                                       | -               | 宠物性别（0=未知，1=公，2=母）            |
| medical_history | text    | 是     | NULL                                    | -               | 过往病史（无默认值，空值为NULL）          |
| is_default   | tinyint    | 是     | 0                                       | -               | 是否默认宠物（1=是，发起问诊默认选中）    |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 信息创建时间                              |
| update_time  | datetime   | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 信息更新时间（自动触发）                  |


## 2. 社区互动模块
### 2.1 community_note（社区笔记表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 笔记唯一标识ID                            |
| user_id      | int        | 否     | -                                       | 索引（BTREE）   | 发布用户ID，关联`user_base.id`            |
| title        | varchar(100)| 是    | ''                                      | -               | 笔记标题                                  |
| content      | text       | 否     | -                                       | -               | 笔记内容                                  |
| img_urls     | varchar(1024)| 是   | ''                                      | -               | 图片URL（逗号分隔，最多9张）              |
| pet_type_id  | int        | 否     | -                                       | 索引（BTREE）   | 关联宠物品类ID，关联`dict_pet_type.id`    |
| tag_ids      | varchar(100)| 是   | ''                                      | -               | 标签ID（逗号分隔）                        |
| like_count   | int        | 是     | 0                                       | -               | 点赞数                                    |
| collect_count | int       | 是     | 0                                       | -               | 收藏数                                    |
| comment_count | int      | 是     | 0                                       | -               | 评论数                                    |
| status       | tinyint    | 是     | 1                                       | -               | 状态（1=正常，0=违规/删除）                |
| view_count   | int        | 是     | 0                                       | -               | 浏览量                                    |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | 索引（BTREE）   | 创建时间                                  |
| update_time  | datetime   | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 2.2 community_comment（笔记评论表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 评论唯一标识ID                            |
| note_id      | int        | 否     | -                                       | 索引（BTREE）   | 关联笔记ID，关联`community_note.id`       |
| user_id      | int        | 否     | -                                       | 索引（BTREE）   | 评论用户ID，关联`user_base.id`            |
| content      | text       | 否     | -                                       | -               | 评论内容                                  |
| parent_id    | int        | 是     | 0                                       | 索引（BTREE）   | 父评论ID（0=主评论，>0=回复）             |
| like_count   | int        | 是     | 0                                       | -               | 评论点赞数                                |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |
| update_time  | datetime   | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 2.3 community_like（笔记点赞表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 点赞唯一标识ID                            |
| note_id      | int        | 否     | -                                       | 唯一索引（BTREE） | 关联笔记ID，关联`community_note.id`     |
| user_id      | int        | 否     | -                                       | 索引（BTREE）   | 点赞用户ID，关联`user_base.id`            |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 点赞时间                                  |

### 2.4 community_collect（笔记收藏表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 收藏唯一标识ID                            |
| note_id      | int        | 否     | -                                       | 唯一索引（BTREE） | 关联笔记ID，关联`community_note.id`     |
| user_id      | int        | 否     | -                                       | 索引（BTREE）   | 收藏用户ID，关联`user_base.id`            |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 收藏时间                                  |


## 3. 在线问诊模块
### 3.1 consult_doctor（问诊医生信息表）
| 字段名       | 数据类型        | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|-----------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int             | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 医生唯一标识ID                            |
| user_id      | int             | 否     | -                                       | 唯一索引（BTREE） | 关联用户账号ID，关联`user_base.id`     |
| real_name    | varchar(20)     | 否     | -                                       | -               | 真实姓名（认证用）                        |
| title        | varchar(30)     | 是     | ''                                      | -               | 职称（如“异宠执业医师”）                  |
| good_at_type | varchar(100)    | 否     | -                                       | -               | 擅长品类（`dict_pet_type.id`逗号分隔）    |
| qualification | varchar(255)   | 否     | -                                       | -               | 资质证书URL（审核用）                    |
| consultation_fee | decimal(10,2) | 否  | -                                       | -               | 单次问诊费（如59.90）                     |
| receive_status | tinyint       | 是     | 1                                       | -               | 接诊状态（1=可接诊，0=休息中）            |
| create_time  | datetime        | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |
| update_time  | datetime        | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 3.2 consult_order（问诊订单表）
| 字段名       | 数据类型        | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|-----------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int             | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 订单唯一标识ID                            |
| order_no     | varchar(32)     | 否     | -                                       | 唯一索引（BTREE） | 订单编号（唯一，如CON20241001001）      |
| user_id      | int             | 否     | -                                       | 索引（BTREE）   | 用户ID，关联`user_base.id`                |
| pet_id       | int             | 否     | -                                       | 索引（BTREE）   | 问诊宠物ID，关联`user_pet.id`             |
| doctor_id    | int             | 否     | -                                       | 索引（BTREE）   | 接诊医生ID，关联`consult_doctor.id`       |
| consult_type | tinyint         | 否     | -                                       | -               | 问诊类型（1=普通，2=紧急）                |
| symptom      | text            | 否     | -                                       | -               | 症状描述（用户填写）                      |
| media_url    | varchar(512)    | 是     | ''                                      | -               | 症状媒体URL（照片/视频逗号分隔）          |
| amount       | decimal(10,2)   | 否     | -                                       | -               | 应付金额（问诊费-优惠券）                 |
| coupon_id    | int             | 是     | 0                                       | -               | 优惠券ID（0=未使用）                      |
| pay_status   | tinyint         | 是     | 0                                       | -               | 支付状态（0=未支付，1=已支付）            |
| pay_time     | datetime        | 是     | NULL                                    | -               | 支付时间                                  |
| consult_status | tinyint       | 是     | 0                                       | -               | 问诊状态（0=待接诊，1=接诊中，2=已完成，3=已取消） |
| create_time  | datetime        | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |
| update_time  | datetime        | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 3.3 consult_chat（问诊聊天记录表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 聊天记录唯一标识ID                        |
| order_id     | int        | 否     | -                                       | 索引（BTREE）   | 关联问诊订单ID，关联`consult_order.id`    |
| send_user_id | int        | 否     | -                                       | -               | 发送者ID，关联`user_base.id`              |
| send_role    | tinyint    | 否     | -                                       | -               | 发送者角色（1=用户，2=医生）              |
| content_type | tinyint    | 否     | -                                       | -               | 内容类型（1=文字，2=图片，3=语音）        |
| content      | text       | 否     | -                                       | -               | 内容（文字/媒体URL）                      |
| send_time    | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 发送时间                                  |


## 4. 商城交易模块
### 4.1 mall_category（商城商品分类表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 分类唯一标识ID                            |
| category_name | varchar(50)| 否    | -                                       | -               | 分类名称（如“爬宠-饲养箱”）                |
| parent_id    | int        | 否     | 0                                       | -               | 父级ID（0=一级分类，>0=二级分类）         |
| sort         | int        | 是     | 0                                       | -               | 排序（靠前展示）                          |
| status       | tinyint    | 是     | 1                                       | -               | 状态（1=启用，0=下架）                    |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |
| update_time  | datetime   | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 4.2 mall_goods（商品表）
| 字段名       | 数据类型        | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|-----------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int             | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 商品唯一标识ID                            |
| goods_name   | varchar(100)    | 否     | -                                       | -               | 商品名称（含适用品类，如“鬃狮蜥专用加热垫”） |
| category_id  | int             | 否     | -                                       | 索引（BTREE）   | 分类ID，关联`mall_category.id`            |
| pet_type_ids | varchar(100)    | 否     | -                                       | -               | 适用宠物品类（`dict_pet_type.id`逗号分隔） |
| price        | decimal(10,2)   | 否     | -                                       | -               | 售价                                      |
| original_price | decimal(10,2) | 是    | 0.00                                    | -               | 原价（划线价）                            |
| stock        | int             | 是     | 0                                       | -               | 库存数量                                  |
| spec         | varchar(50)     | 是     | ''                                      | -               | 规格（如“20cm×15cm”）                     |
| main_img     | varchar(255)    | 否     | -                                       | -               | 主图URL（列表页展示）                     |
| detail_img   | varchar(1024)   | 是     | ''                                      | -               | 详情图URL（逗号分隔）                     |
| tips         | text            | 是     | NULL                                    | -               | 养护小贴士（TEXT无默认值）                |
| doctor_recommend | tinyint    | 是  | 0                                       | -               | 是否医生推荐（1=是，0=否）                |
| status       | tinyint         | 是     | 1                                       | -               | 状态（1=上架，0=下架）                    |
| create_time  | datetime        | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |
| update_time  | datetime        | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 4.3 mall_cart（购物车表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 购物车记录唯一标识ID                      |
| user_id      | int        | 否     | -                                       | 唯一索引（BTREE） | 用户ID，关联`user_base.id`              |
| goods_id     | int        | 否     | -                                       | 索引（BTREE）   | 商品ID，关联`mall_goods.id`               |
| quantity     | int        | 否     | 1                                       | -               | 数量（默认1，限制最大5）                  |
| select_status | tinyint    | 是    | 1                                       | -               | 是否选中（1=是，结算时勾选）              |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |
| update_time  | datetime   | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 4.4 mall_coupon（优惠券表）
| 字段名       | 数据类型        | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|-----------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int             | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 优惠券唯一标识ID                          |
| coupon_name  | varchar(50)     | 否     | -                                       | -               | 优惠券名称（如“满50减10”）                |
| type         | tinyint         | 否     | -                                       | -               | 类型（1=满减券，2=折扣券）                |
| value        | decimal(10,2)   | 否     | -                                       | -               | 满减金额（如10.00）或折扣比例（如0.9=9折） |
| min_amount   | decimal(10,2)   | 是     | 0.00                                    | -               | 使用门槛（0=无门槛）                      |
| start_time   | datetime        | 否     | -                                       | -               | 生效时间                                  |
| end_time     | datetime        | 否     | -                                       | -               | 失效时间                                  |
| total        | int             | 否     | -                                       | -               | 总数量                                    |
| remain       | int             | 否     | -                                       | -               | 剩余数量                                  |
| status       | tinyint         | 是     | 1                                       | -               | 状态（1=可用，0=不可用）                  |
| create_time  | datetime        | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |

### 4.5 user_coupon（用户优惠券关联表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 关联记录唯一标识ID                        |
| user_id      | int        | 否     | -                                       | 索引（BTREE）   | 用户ID，关联`user_base.id`                |
| coupon_id    | int        | 否     | -                                       | 索引（BTREE）   | 优惠券ID，关联`mall_coupon.id`            |
| get_time     | datetime   | 是     | CURRENT_TIMESTAMP                       | -               | 领取时间                                  |
| use_time     | datetime   | 是     | NULL                                    | -               | 使用时间                                  |
| order_id     | int        | 是     | 0                                       | -               | 使用的订单ID（0=未使用）                  |
| status       | tinyint    | 是     | 1                                       | -               | 状态（1=未使用，2=已使用，3=已过期）      |

### 4.6 mall_order（商城订单表）
| 字段名       | 数据类型        | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|-----------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int             | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 订单唯一标识ID                            |
| order_no     | varchar(32)     | 否     | -                                       | 唯一索引（BTREE） | 订单编号（如MALL20241001001）            |
| user_id      | int             | 否     | -                                       | 索引（BTREE）   | 用户ID，关联`user_base.id`                |
| total_amount | decimal(10,2)   | 否     | -                                       | -               | 订单总金额（商品+运费-优惠券）             |
| pay_amount   | decimal(10,2)   | 否     | -                                       | -               | 实付金额                                  |
| freight      | decimal(10,2)   | 是     | 0.00                                    | -               | 运费（满XX元包邮）                        |
| coupon_id    | int             | 是     | 0                                       | -               | 优惠券ID（0=未使用）                      |
| receiver_name | varchar(20)    | 否    | -                                       | -               | 收货人姓名                                |
| receiver_phone | varchar(11)   | 否   | -                                       | -               | 收货人电话                                |
| receiver_addr | varchar(255)   | 否   | -                                       | -               | 收货详细地址                              |
| receiver_province | varchar(20)| 是  | ''                                      | -               | 收货省份                                  |
| receiver_city | varchar(20)    | 是    | ''                                      | -               | 收货城市                                  |
| receiver_district | varchar(20)| 是 | ''                                      | -               | 收货区/县                                 |
| order_status | tinyint         | 是     | 0                                       | 索引（BTREE）   | 订单状态（0=待付款，1=待发货，2=待收货，3=已完成，4=已取消） |
| logistics_no | varchar(50)     | 是     | ''                                      | -               | 物流单号                                  |
| logistics_code | varchar(20)  | 是   | ''                                      | -               | 快递公司编码（如SF=顺丰）                  |
| logistics_name | varchar(30) | 是  | ''                                      | -               | 快递公司名称                              |
| logistics_status | tinyint    | 是  | 0                                       | -               | 物流状态（0=待发货，1=已揽收，2=运输中，3=派送中，4=已签收，5=异常） |
| pay_type     | tinyint         | 是     | 0                                       | -               | 支付方式（1=微信支付，2=支付宝）          |
| pay_time     | datetime        | 是     | NULL                                    | -               | 支付时间                                  |
| ship_time    | datetime        | 是     | NULL                                    | -               | 发货时间                                  |
| confirm_time | datetime        | 是     | NULL                                    | -               | 确认收货时间                              |
| cancel_time  | datetime        | 是     | NULL                                    | -               | 取消时间                                  |
| remark       | varchar(512)    | 是     | ''                                      | -               | 订单备注                                  |
| create_time  | datetime        | 是     | CURRENT_TIMESTAMP                       | 索引（BTREE）   | 创建时间                                  |
| update_time  | datetime        | 是     | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | -         | 更新时间（自动触发）                      |

### 4.7 mall_order_item（订单明细表）
| 字段名       | 数据类型        | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|-----------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int             | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 明细唯一标识ID                            |
| order_id     | int             | 否     | -                                       | 索引（BTREE）   | 关联订单ID，关联`mall_order.id`           |
| goods_id     | int             | 否     | -                                       | 索引（BTREE）   | 商品ID，关联`mall_goods.id`               |
| goods_name   | varchar(100)    | 否     | -                                       | -               | 商品名称（下单时快照）                    |
| goods_img    | varchar(255)    | 否     | -                                       | -               | 商品图片（下单时快照）                    |
| price        | decimal(10,2)   | 否     | -                                       | -               | 购买单价                                  |
| quantity     | int             | 否     | -                                       | -               | 购买数量                                  |
| total_price  | decimal(10,2)   | 否     | -                                       | -               | 小计金额                                  |
| spec_info    | varchar(100)    | 是     | ''                                      | -               | 规格信息（如颜色、尺寸）                  |
| create_time  | datetime        | 是     | CURRENT_TIMESTAMP                       | -               | 创建时间                                  |

### 4.8 mall_logistics_trace（物流轨迹表）
| 字段名       | 数据类型        | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|-----------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int             | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 轨迹唯一标识ID                            |
| order_id     | int             | 否     | -                                       | 索引（BTREE）   | 关联商城订单ID，关联`mall_order.id`       |
| logistics_no | varchar(50)     | 否     | -                                       | 索引（BTREE）   | 物流单号                                  |
| logistics_code | varchar(20)  | 否   | -                                       | -               | 快递公司编码                              |
| logistics_name | varchar(30) | 否  | -                                       | -               | 快递公司名称                              |
| trace_time   | datetime        | 否     | -                                       | 索引（BTREE）   | 物流节点时间                              |
| trace_content | varchar(255)   | 否   | -                                       | -               | 物流节点内容（如“【深圳市】已揽收”）      |
| trace_status | tinyint         | 否     | -                                       | -               | 节点对应状态（0=待揽收，1=已揽收，2=运输中，3=派送中，4=已签收，5=异常） |
| location_addr | varchar(255)   | 是   | ''                                      | -               | 物流节点详细地址                          |
| location_lng | decimal(10,6)   | 是     | NULL                                    | -               | 节点经度                                  |
| location_lat | decimal(10,6)   | 是     | NULL                                    | -               | 节点纬度                                  |
| create_time  | datetime        | 是     | CURRENT_TIMESTAMP                       | -               | 数据创建时间                              |


## 5. 系统支撑模块
### 5.1 dict_pet_type（宠物品类数据字典表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 字典唯一标识ID                            |
| type_name    | varchar(30)| 否     | -                                       | 唯一索引（BTREE） | 品类名称（如“爬宠-鬃狮蜥”“啮齿-金丝熊”） |
| parent_id    | int        | 否     | 0                                       | -               | 父级ID（0=一级品类，>0=二级品类）         |
| sort         | int        | 是     | 0                                       | -               | 排序（热门品类靠前）                      |

### 5.2 sys_dict（系统通用数据字典表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 字典唯一标识ID                            |
| dict_type    | varchar(50)| 否     | -                                       | 唯一索引（BTREE） | 字典类型（如“pet_gender”“order_status”） |
| dict_code    | varchar(30)| 否     | -                                       | 唯一索引（BTREE） | 字典编码（如“male”“female”）            |
| dict_value   | varchar(50)| 否     | -                                       | -               | 字典值（如“公”“母”）                      |
| sort         | int        | 是     | 0                                       | -               | 排序                                      |
| status       | tinyint    | 是     | 1                                       | -               | 状态（1=启用，0=禁用）                    |

### 5.3 notice（消息通知表）
| 字段名       | 数据类型   | 允许空 | 默认值                                  | 主键/索引       | 字段说明                                  |
|--------------|------------|--------|-----------------------------------------|-----------------|-------------------------------------------|
| id           | int        | 否     | AUTO_INCREMENT                          | 主键（BTREE）   | 通知唯一标识ID                            |
| user_id      | int        | 否     | -                                       | 索引（BTREE）   | 接收用户ID，关联`user_base.id`            |
| notice_type  | tinyint    | 否     | -                                       | -               | 通知类型（1=问诊提醒，2=订单提醒，3=社区互动，4=系统通知） |
| title        | varchar(50)| 否     | -                                       | -               | 通知标题                                  |
| content      | varchar(255)| 否     | -                                       | -               | 通知内容                                  |
| related_id   | int        | 是     | 0                                       | -               | 关联ID（如订单ID/笔记ID）                  |
| related_type | varchar(20)| 是     | ''                                      | -               | 关联类型（如“order”/“note”）              |
| is_read      | tinyint    | 是     | 0                                       | 索引（BTREE）   | 是否已读（0=未读，1=已读）                |
| create_time  | datetime   | 是     | CURRENT_TIMESTAMP                       | 索引（BTREE）   | 创建时间                                  |
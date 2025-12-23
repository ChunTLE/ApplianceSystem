/*
 Navicat Premium Data Transfer

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : appliance_inventory_db

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 23/12/2025 14:31:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称',
  `type_id` bigint(20) NOT NULL COMMENT '产品类型ID',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `stock` int(11) NULL DEFAULT 0 COMMENT '库存数量',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态(1上架 0下架)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_product_type`(`type_id` ASC) USING BTREE,
  CONSTRAINT `fk_product_type` FOREIGN KEY (`type_id`) REFERENCES `product_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家电产品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '海尔双开门冰箱', 1, 4999.00, 50, 1, '2025-12-22 08:41:26');
INSERT INTO `product` VALUES (2, '美的变频空调', 2, 3999.00, 80, 1, '2025-12-22 08:41:26');
INSERT INTO `product` VALUES (3, '小天鹅滚筒洗衣机', 3, 2999.00, 60, 1, '2025-12-22 08:41:26');
INSERT INTO `product` VALUES (4, '海信4K电视', 4, 3599.00, 40, 1, '2025-12-22 08:41:26');
INSERT INTO `product` VALUES (5, '格力立式空调', 2, 6999.00, 30, 1, '2025-12-22 08:41:26');

-- ----------------------------
-- Table structure for product_type
-- ----------------------------
DROP TABLE IF EXISTS `product_type`;
CREATE TABLE `product_type`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家电类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_type
-- ----------------------------
INSERT INTO `product_type` VALUES (1, '冰箱', '制冷类家电');
INSERT INTO `product_type` VALUES (2, '空调', '制冷制热设备');
INSERT INTO `product_type` VALUES (3, '洗衣机', '洗涤设备');
INSERT INTO `product_type` VALUES (4, '电视', '影音娱乐设备');

-- ----------------------------
-- Table structure for sale
-- ----------------------------
DROP TABLE IF EXISTS `sale`;
CREATE TABLE `sale`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `quantity` int(11) NOT NULL COMMENT '销售数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '销售总额',
  `salesman_id` bigint(20) NOT NULL COMMENT '销售人员ID',
  `sale_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_sale_product`(`product_id` ASC) USING BTREE,
  INDEX `fk_sale_user`(`salesman_id` ASC) USING BTREE,
  CONSTRAINT `fk_sale_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sale_user` FOREIGN KEY (`salesman_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sale
-- ----------------------------
INSERT INTO `sale` VALUES (1, 1, 2, 9998.00, 4, '2025-12-22 08:41:36');
INSERT INTO `sale` VALUES (2, 2, 3, 11997.00, 5, '2025-12-22 08:41:36');
INSERT INTO `sale` VALUES (3, 4, 1, 3599.00, 4, '2025-12-22 08:41:36');
INSERT INTO `sale` VALUES (4, 5, 1, 6999.00, 5, '2025-12-22 08:41:36');

-- ----------------------------
-- Table structure for stock_in
-- ----------------------------
DROP TABLE IF EXISTS `stock_in`;
CREATE TABLE `stock_in`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `quantity` int(11) NOT NULL COMMENT '入库数量',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `in_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_stockin_product`(`product_id` ASC) USING BTREE,
  INDEX `fk_stockin_user`(`operator_id` ASC) USING BTREE,
  CONSTRAINT `fk_stockin_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_stockin_user` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '入库记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_in
-- ----------------------------
INSERT INTO `stock_in` VALUES (1, 1, 20, 2, '2025-12-22 08:41:29');
INSERT INTO `stock_in` VALUES (2, 2, 30, 2, '2025-12-22 08:41:29');
INSERT INTO `stock_in` VALUES (3, 3, 15, 3, '2025-12-22 08:41:29');
INSERT INTO `stock_in` VALUES (4, 4, 10, 3, '2025-12-22 08:41:29');
INSERT INTO `stock_in` VALUES (5, 5, 20, 2, '2025-12-22 08:41:29');

-- ----------------------------
-- Table structure for stock_out
-- ----------------------------
DROP TABLE IF EXISTS `stock_out`;
CREATE TABLE `stock_out`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `quantity` int(11) NOT NULL COMMENT '出库数量',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `out_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_stockout_product`(`product_id` ASC) USING BTREE,
  INDEX `fk_stockout_user`(`operator_id` ASC) USING BTREE,
  CONSTRAINT `fk_stockout_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_stockout_user` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '出库记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_out
-- ----------------------------
INSERT INTO `stock_out` VALUES (1, 1, 5, 2, '2025-12-22 08:41:32');
INSERT INTO `stock_out` VALUES (2, 2, 10, 3, '2025-12-22 08:41:32');
INSERT INTO `stock_out` VALUES (3, 3, 8, 2, '2025-12-22 08:41:32');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码(ADMIN/STOCK/SALES)',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ADMIN', '管理员', '系统管理员');
INSERT INTO `sys_role` VALUES (2, 'STOCK', '库存人员', '负责入库和出库');
INSERT INTO `sys_role` VALUES (3, 'SALES', '销售人员', '负责产品销售');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `fk_user_role`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$NqEvwYYd5oC.4xczRt4GeuXPyliRLi4FJt6g/fNeJKnlEUFpLfAXq', 1, 1, '2025-12-22 08:41:19');
INSERT INTO `sys_user` VALUES (2, 'stock01', '$2a$10$NqEvwYYd5oC.4xczRt4GeuXPyliRLi4FJt6g/fNeJKnlEUFpLfAXq', 2, 1, '2025-12-22 08:41:19');
INSERT INTO `sys_user` VALUES (3, 'stock02', '$2a$10$NqEvwYYd5oC.4xczRt4GeuXPyliRLi4FJt6g/fNeJKnlEUFpLfAXq', 2, 1, '2025-12-22 08:41:19');
INSERT INTO `sys_user` VALUES (4, 'sales01', '$2a$10$NqEvwYYd5oC.4xczRt4GeuXPyliRLi4FJt6g/fNeJKnlEUFpLfAXq', 3, 1, '2025-12-22 08:41:19');
INSERT INTO `sys_user` VALUES (5, 'sales02', '$2a$10$NqEvwYYd5oC.4xczRt4GeuXPyliRLi4FJt6g/fNeJKnlEUFpLfAXq', 3, 1, '2025-12-22 08:41:19');

SET FOREIGN_KEY_CHECKS = 1;

/*
Navicat MySQL Data Transfer

Source Server         : root
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : attachment_service

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2020-06-02 23:00:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reference_code` char(20) CHARACTER SET utf8mb4 NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_original_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `mime_type` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `uploaded_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `file_path` text CHARACTER SET utf8mb4,
  `status` int(11) DEFAULT NULL,
  `module` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `uploader_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

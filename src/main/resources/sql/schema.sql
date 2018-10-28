/*
SQLyog Ultimate v12.3.3 (64 bit)
MySQL - 5.7.14 : Database - mytest
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mytest` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `mytest`;

/*Table structure for table `db_config` */

DROP TABLE IF EXISTS `db_config`;

CREATE TABLE `db_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `db_name` varchar(30) DEFAULT NULL COMMENT '名字',
  `url` varchar(255) DEFAULT NULL COMMENT 'url',
  `driver` varchar(150) DEFAULT NULL COMMENT 'driver',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `schema` varchar(255) DEFAULT NULL,
  `catalog` varchar(255) DEFAULT NULL,
  `db_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `db_config` */

insert  into `db_config`(`id`,`db_name`,`url`,`driver`,`username`,`password`,`schema`,`catalog`,`db_type`) values
(1,'mytest','jdbc:mysql://localhost:3306/mytest?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true','com.mysql.jdbc.Driver','root',NULL,'mytest',NULL,NULL);

/*Table structure for table `table_strategy_config` */

DROP TABLE IF EXISTS `table_strategy_config`;

CREATE TABLE `table_strategy_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `db_id` int(11) DEFAULT NULL COMMENT '数据库id',
  `table_name` varchar(255) DEFAULT NULL COMMENT '数据库表名',
  `prefix` varchar(255) DEFAULT NULL,
  `project_path` varchar(255) DEFAULT NULL,
  `model_name` varchar(255) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `entity_name` varchar(255) DEFAULT NULL,
  `mapper_name` varchar(255) DEFAULT NULL,
  `xml_name` varchar(255) DEFAULT NULL,
  `service_name` varchar(255) DEFAULT NULL,
  `service_impl_name` varchar(255) DEFAULT NULL,
  `controller_name` varchar(255) DEFAULT NULL,
  `entity_package` varchar(255) DEFAULT NULL,
  `service_package` varchar(255) DEFAULT NULL,
  `service_impl_package` varchar(255) DEFAULT NULL,
  `mapper_package` varchar(255) DEFAULT NULL,
  `mapper_xml_package` varchar(255) DEFAULT NULL,
  `controller_package` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `table_strategy_config` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
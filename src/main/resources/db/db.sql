CREATE DATABASE IF NOT EXISTS test;

USE test;

-- 用户表（分库分表）
CREATE TABLE IF NOT EXISTS tb_user
(
    id          BIGINT PRIMARY KEY COMMENT '用户id',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    email       VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone       VARCHAR(20)  NOT NULL UNIQUE COMMENT '手机号',
    create_time DATETIME     NOT NULL DEFAULT NOW()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '用户表1';

INSERT INTO tb_user (id, username, password, email, phone)
VALUES (0, 'userA1234', '$2a$10$iGJ5t6tbffIJI1K0QmPtB.dgVGZP6bf5d7VP1nhekAFYMimu7p6PG', 'user1@example.com', '19973571860');

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `branch_id`     bigint(20)   NOT NULL,
    `xid`           varchar(100) NOT NULL,
    `context`       varchar(128) NOT NULL,
    `rollback_info` longblob     NOT NULL,
    `log_status`    int(11)      NOT NULL,
    `log_created`   datetime     NOT NULL,
    `log_modified`  datetime     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '回滚日志1';

CREATE DATABASE IF NOT EXISTS test01;

USE test01;

-- 用户表（分库分表）
CREATE TABLE IF NOT EXISTS tb_user
(
    id          BIGINT PRIMARY KEY COMMENT '用户id',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    email       VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone       VARCHAR(20)  NOT NULL UNIQUE COMMENT '手机号',
    create_time DATETIME     NOT NULL DEFAULT NOW()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '用户表2';

INSERT INTO tb_user (id, username, password, email, phone)
VALUES (1, 'userB1234', '$2a$10$iGJ5t6tbffIJI1K0QmPtB.dgVGZP6bf5d7VP1nhekAFYMimu7p6PG', 'user2@example.com', '19973571861');

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `branch_id`     bigint(20)   NOT NULL,
    `xid`           varchar(100) NOT NULL,
    `context`       varchar(128) NOT NULL,
    `rollback_info` longblob     NOT NULL,
    `log_status`    int(11)      NOT NULL,
    `log_created`   datetime     NOT NULL,
    `log_modified`  datetime     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '回滚日志2';

CREATE DATABASE IF NOT EXISTS test02;

USE test02;

-- 用户表（分库分表）
CREATE TABLE IF NOT EXISTS tb_user
(
    id          BIGINT PRIMARY KEY COMMENT '用户id',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    email       VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone       VARCHAR(20)  NOT NULL UNIQUE COMMENT '手机号',
    create_time DATETIME     NOT NULL DEFAULT NOW()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '用户表3';

INSERT INTO tb_user (id, username, password, email, phone)
VALUES (2, 'userC1234', '$2a$10$iGJ5t6tbffIJI1K0QmPtB.dgVGZP6bf5d7VP1nhekAFYMimu7p6PG', 'user3@example.com', '19973571862');

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `branch_id`     bigint(20)   NOT NULL,
    `xid`           varchar(100) NOT NULL,
    `context`       varchar(128) NOT NULL,
    `rollback_info` longblob     NOT NULL,
    `log_status`    int(11)      NOT NULL,
    `log_created`   datetime     NOT NULL,
    `log_modified`  datetime     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '回滚日志3';
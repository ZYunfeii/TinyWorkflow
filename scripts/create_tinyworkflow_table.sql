CREATE TABLE `workflow_context` (
                                    `workflow_id` bigint(32) NOT NULL,
                                    `gmt_created` datetime DEFAULT CURRENT_TIMESTAMP,
                                    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
                                    `context` text CHARACTER SET latin1,
                                    `is_deleted` tinyint(1) DEFAULT '0',
                                    PRIMARY KEY (`workflow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE `workflow` (
                            `id` bigint(32) NOT NULL AUTO_INCREMENT,
                            `workflow_id` bigint(32) DEFAULT NULL,
                            `gmt_created` datetime DEFAULT CURRENT_TIMESTAMP,
                            `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
                            `task_name` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
                            `status` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
                            `is_deleted` tinyint(1) DEFAULT '0',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4
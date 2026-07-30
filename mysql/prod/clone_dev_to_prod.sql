-- 用 mms_side_income_dev_core 整库覆盖 mms_side_income_prod_core（同机、不备份）
-- 用法（必须用 mysql 客户端，支持 DELIMITER）：
--   mysql -u root -p < clone_dev_to_prod.sql
--
-- 修复点：
-- 1) 先 USE 库，避免 ERROR 1046 No database selected
-- 2) 按 information_schema 动态拷贝，不写死表名（dev 有啥拷啥）
-- 3) 删掉 prod 多余表（如历史 side_income_record）

CREATE DATABASE IF NOT EXISTS `mms_side_income_prod_core`
  CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

USE `mms_side_income_prod_core`;

DELIMITER $$

DROP PROCEDURE IF EXISTS `clone_side_income_dev_to_prod`$$

CREATE PROCEDURE `clone_side_income_dev_to_prod`()
BEGIN
  DECLARE v_done INT DEFAULT 0;
  DECLARE v_name VARCHAR(128);
  DECLARE v_dev_cnt INT DEFAULT 0;

  DECLARE cur_src CURSOR FOR
    SELECT TABLE_NAME
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = 'mms_side_income_dev_core'
      AND TABLE_TYPE = 'BASE TABLE'
    ORDER BY TABLE_NAME;

  DECLARE cur_extra CURSOR FOR
    SELECT t.TABLE_NAME
    FROM information_schema.TABLES t
    WHERE t.TABLE_SCHEMA = 'mms_side_income_prod_core'
      AND t.TABLE_TYPE = 'BASE TABLE'
      AND NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLES s
        WHERE s.TABLE_SCHEMA = 'mms_side_income_dev_core'
          AND s.TABLE_TYPE = 'BASE TABLE'
          AND s.TABLE_NAME = t.TABLE_NAME
      );

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

  SELECT COUNT(*) INTO v_dev_cnt
  FROM information_schema.TABLES
  WHERE TABLE_SCHEMA = 'mms_side_income_dev_core'
    AND TABLE_TYPE = 'BASE TABLE';

  IF v_dev_cnt = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'mms_side_income_dev_core 不存在或没有表，已中止';
  END IF;

  SET FOREIGN_KEY_CHECKS = 0;

  -- 1) 删除 prod 多出来的表
  SET v_done = 0;
  OPEN cur_extra;
  extra_loop: LOOP
    FETCH cur_extra INTO v_name;
    IF v_done = 1 THEN
      LEAVE extra_loop;
    END IF;
    SET @sql = CONCAT('DROP TABLE IF EXISTS `mms_side_income_prod_core`.`', v_name, '`');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END LOOP;
  CLOSE cur_extra;

  -- 2) 按 dev 重建并灌数
  SET v_done = 0;
  OPEN cur_src;
  copy_loop: LOOP
    FETCH cur_src INTO v_name;
    IF v_done = 1 THEN
      LEAVE copy_loop;
    END IF;

    SET @sql = CONCAT('DROP TABLE IF EXISTS `mms_side_income_prod_core`.`', v_name, '`');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @sql = CONCAT(
      'CREATE TABLE `mms_side_income_prod_core`.`', v_name,
      '` LIKE `mms_side_income_dev_core`.`', v_name, '`'
    );
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @sql = CONCAT(
      'INSERT INTO `mms_side_income_prod_core`.`', v_name,
      '` SELECT * FROM `mms_side_income_dev_core`.`', v_name, '`'
    );
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END LOOP;
  CLOSE cur_src;

  SET FOREIGN_KEY_CHECKS = 1;
END$$

DELIMITER ;

CALL `clone_side_income_dev_to_prod`();
DROP PROCEDURE IF EXISTS `clone_side_income_dev_to_prod`;

-- 自检：表数量应一致
SELECT 'dev' AS db_name, COUNT(*) AS table_cnt
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'mms_side_income_dev_core' AND TABLE_TYPE = 'BASE TABLE'
UNION ALL
SELECT 'prod' AS db_name, COUNT(*) AS table_cnt
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'mms_side_income_prod_core' AND TABLE_TYPE = 'BASE TABLE';

-- 自检：关键表行数应一致
SELECT 'finance_transaction' AS tbl,
       (SELECT COUNT(*) FROM `mms_side_income_dev_core`.`finance_transaction`) AS dev_cnt,
       (SELECT COUNT(*) FROM `mms_side_income_prod_core`.`finance_transaction`) AS prod_cnt
UNION ALL
SELECT 'finance_account',
       (SELECT COUNT(*) FROM `mms_side_income_dev_core`.`finance_account`),
       (SELECT COUNT(*) FROM `mms_side_income_prod_core`.`finance_account`)
UNION ALL
SELECT 'system_user',
       (SELECT COUNT(*) FROM `mms_side_income_dev_core`.`system_user`),
       (SELECT COUNT(*) FROM `mms_side_income_prod_core`.`system_user`);

USE `mms_prod_core`;

ALTER TABLE system_user
CHANGE avatar avatar_id BIGINT NULL COMMENT '头像附件ID';

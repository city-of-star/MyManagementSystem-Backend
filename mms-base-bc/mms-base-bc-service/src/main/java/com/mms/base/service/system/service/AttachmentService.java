package com.mms.base.service.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.AttachmentBatchDeleteDto;
import com.mms.base.common.system.dto.AttachmentCreateDto;
import com.mms.base.common.system.dto.AttachmentPageQueryDto;
import com.mms.base.common.system.dto.AttachmentStatusSwitchDto;
import com.mms.base.common.system.dto.AttachmentUpdateDto;
import com.mms.base.common.system.vo.AttachmentVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 实现功能【附件服务】
 * <p>
 * 提供附件服务的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2026-02-06 14:36:38
 */
public interface AttachmentService {

    /**
     * 分页查询附件列表
     *
     * @param dto 查询条件
     * @return 分页附件列表
     */
    Page<AttachmentVo> getAttachmentPage(AttachmentPageQueryDto dto);

    /**
     * 根据附件ID查询附件详情
     *
     * @param attachmentId 附件ID
     * @return 附件详情
     */
    AttachmentVo getAttachmentById(Long attachmentId);

    /**
     * 创建附件记录（元数据）
     *
     * @param dto 创建参数
     * @return 创建后的附件信息
     */
    AttachmentVo createAttachment(AttachmentCreateDto dto);

    /**
     * 更新附件信息（如绑定业务、备注、状态等）
     *
     * @param dto 更新参数
     * @return 更新后的附件信息
     */
    AttachmentVo updateAttachment(AttachmentUpdateDto dto);

    /**
     * 删除附件（逻辑删除）
     *
     * @param attachmentId 附件ID
     */
    void deleteAttachment(Long attachmentId);

    /**
     * 批量删除附件（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeleteAttachment(AttachmentBatchDeleteDto dto);

    /**
     * 切换附件状态（启用/禁用）
     *
     * @param dto 状态切换参数
     */
    void switchAttachmentStatus(AttachmentStatusSwitchDto dto);

    /**
     * 上传文件并创建附件记录
     *
     * @param file         上传文件
     * @param businessType 业务类型（可选）
     * @param businessId   关联业务ID（可选）
     * @param remark       备注（可选）
     * @return 附件信息
     */
    AttachmentVo uploadAttachment(MultipartFile file, String businessType, Long businessId, String remark);
}


package com.mms.base.feign;

import com.mms.base.common.system.vo.AttachmentVo;
import com.mms.common.core.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 实现功能【附件服务 Feign 接口】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-30 21:55:21
 */
@FeignClient(name = "base", path = "/attachment")
public interface AttachmentFeign {

    /**
     * 根据附件ID查询附件详情
     */
    @GetMapping("/{attachmentId}")
    Response<AttachmentVo> getAttachmentById(@PathVariable Long attachmentId);

}
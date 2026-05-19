package com.mms.job.common.mq;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 实现功能【作业触发 MQ 载荷】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 18:00:00
 */
@Data
public class JobRunMqPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long jobId;
}

package com.mms.intern.core.service;

import com.mms.intern.common.api.batch.BatchRequests;
import com.mms.intern.common.api.batch.BatchResponses;
import com.mms.intern.common.vo.PageResultVo;

import java.util.List;

public interface InternBatchService {

    PageResultVo<BatchResponses.ListItem> page(BatchRequests.PageQuery q);

    List<BatchResponses.Option> options();

    BatchResponses.Detail get(Long id);

    void save(BatchRequests.Save dto);

    void update(Long id, BatchRequests.Save dto);

    void delete(Long id);
}

package com.mms.intern.core.service;

import com.mms.intern.common.api.position.PositionRequests;
import com.mms.intern.common.api.position.PositionResponses;
import com.mms.intern.common.vo.PageResultVo;

public interface InternPositionService {

    PageResultVo<PositionResponses.ListItem> page(PositionRequests.PageQuery q);

    PageResultVo<PositionResponses.OpenItem> openPage(PositionRequests.OpenQuery q);

    PositionResponses.Detail get(Long id);

    void save(PositionRequests.Save dto);

    void update(Long id, PositionRequests.Save dto);

    void delete(Long id);

    void updateStatus(Long id, PositionRequests.StatusBody dto);
}

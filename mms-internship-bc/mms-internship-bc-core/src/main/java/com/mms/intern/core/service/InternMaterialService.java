package com.mms.intern.core.service;

import com.mms.intern.common.api.material.MaterialRequests;
import com.mms.intern.common.api.material.MaterialResponses;
import com.mms.intern.common.vo.PageResultVo;

import java.util.List;

public interface InternMaterialService {

    void submit(MaterialRequests.Submit dto);

    void update(Long id, MaterialRequests.Submit dto);

    void delete(Long id);

    List<MaterialResponses.ListItem> list(Long applicationId);

    PageResultVo<MaterialResponses.ListItem> page(MaterialRequests.PageQuery q);

    MaterialResponses.Detail get(Long id);

    void audit(Long id, MaterialRequests.Audit dto);
}

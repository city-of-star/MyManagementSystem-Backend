package com.mms.intern.core.service;

import com.mms.intern.common.api.enterprise.EnterpriseRequests;
import com.mms.intern.common.api.enterprise.EnterpriseResponses;
import com.mms.intern.common.vo.PageResultVo;

import java.util.List;

public interface InternEnterpriseService {

    PageResultVo<EnterpriseResponses.ListItem> page(EnterpriseRequests.PageQuery q);

    List<EnterpriseResponses.Option> options(String keyword);

    EnterpriseResponses.Detail get(Long id);

    void save(EnterpriseRequests.Save dto);

    void update(Long id, EnterpriseRequests.Save dto);

    void delete(Long id);

    void audit(Long id, EnterpriseRequests.Audit dto);
}

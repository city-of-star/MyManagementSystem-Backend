package com.mms.intern.core.service;

import com.mms.intern.common.api.application.ApplicationRequests;
import com.mms.intern.common.api.application.ApplicationResponses;
import com.mms.intern.common.vo.PageResultVo;

public interface InternApplicationService {

    void apply(ApplicationRequests.Apply dto);

    void assign(ApplicationRequests.Assign dto);

    void cancel(Long id);

    PageResultVo<ApplicationResponses.ListItem> myPage(ApplicationRequests.MyPageQuery q);

    PageResultVo<ApplicationResponses.ListItem> adminPage(ApplicationRequests.AdminPageQuery q);

    ApplicationResponses.Detail get(Long id);

    void audit(Long id, ApplicationRequests.Audit dto);

    void setMentor(Long id, ApplicationRequests.Mentor dto);

    void setEnterpriseMentor(Long id, ApplicationRequests.EnterpriseMentor dto);

    void lifecycle(Long id, ApplicationRequests.Lifecycle dto);
}

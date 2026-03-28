package com.mms.intern.core.service;

import com.mms.intern.common.api.weekly.WeeklyLogRequests;
import com.mms.intern.common.api.weekly.WeeklyLogResponses;
import com.mms.intern.common.vo.PageResultVo;

import java.util.List;

public interface InternWeeklyLogService {

    void save(WeeklyLogRequests.Save dto);

    void update(Long id, WeeklyLogRequests.Save dto);

    void delete(Long id);

    WeeklyLogResponses.Detail get(Long id);

    PageResultVo<WeeklyLogResponses.ListItem> myPage(WeeklyLogRequests.MyPageQuery q);

    PageResultVo<WeeklyLogResponses.ListItem> pendingPage(WeeklyLogRequests.PendingPageQuery q);

    PageResultVo<WeeklyLogResponses.ListItem> adminPage(WeeklyLogRequests.AdminPageQuery q);

    List<WeeklyLogResponses.ListItem> listByApplication(Long applicationId);

    void review(Long id, WeeklyLogRequests.Review dto);
}

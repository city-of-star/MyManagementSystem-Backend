package com.mms.intern.core.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.intern.common.vo.PageResultVo;

public final class InternPageSupport {

    private InternPageSupport() {
    }

    public static <T> PageResultVo<T> wrap(Page<T> page) {
        PageResultVo<T> vo = new PageResultVo<>();
        vo.setList(page.getRecords());
        vo.setTotal(page.getTotal());
        vo.setPageNum((int) page.getCurrent());
        vo.setPageSize((int) page.getSize());
        vo.setPages((int) page.getPages());
        return vo;
    }
}

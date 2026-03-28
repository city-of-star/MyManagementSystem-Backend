package com.mms.intern.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResultVo<T> {

    private List<T> list = Collections.emptyList();
    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
}

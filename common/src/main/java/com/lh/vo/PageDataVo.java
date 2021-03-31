package com.lh.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: PageDataVo.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/03/26 10:44
 */
@Data
public class PageDataVo {

    @JsonProperty("total_record")
    private long totalRecord;

    @JsonProperty("total_page")
    private long totalPage;

    @JsonProperty("current_data")
    private List<Object> currentData;
}
package com.lh.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: FileService.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/23 16:57
 */
public interface FileService {

    String uploadUserHeadImg(MultipartFile file);
}
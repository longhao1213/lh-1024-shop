package com.lh.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.lh.config.OSSConfig;
import com.lh.service.FileService;
import com.lh.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Copyright (C), 2006-2010, ChengDu longsan info. Co., Ltd.
 * FileName: FileServiceImpl.java
 *
 * @author lh
 * @version 1.0.0
 * @Date 2021/02/23 16:58
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private OSSConfig ossConfig;

    @Override
    public String uploadUserHeadImg(MultipartFile file) {
        // 创建oss客户端
        String bucketName = ossConfig.getBucketName();
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        // 创建新的文件名称
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String folder = dtf.format(now); // 按照日期分类文件
        String fileName = CommonUtil.generateUUID(); // 获取一个uuid名称
        // 获取文件后缀
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String newFileName = "test/" + folder + "/" + fileName + extension;
        // 上传文件
        try {
            PutObjectResult result = ossClient.putObject(bucketName, newFileName, file.getInputStream());
            //返回访问路径
            if (null != result) {
                //https://xd-test1.oss-cn-beijing.aliyuncs.com/test/1.jpg
                String imgUrl = "https://"+bucketName+"."+endpoint+"/"+newFileName;
                return imgUrl;
            }
        } catch (IOException e) {
            log.error("上传头像失败：{}", e);
        }finally {
            ossClient.shutdown();
        }


        return null;
    }
}
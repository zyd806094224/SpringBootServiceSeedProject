package com.zyd.springbootserviceseedproject.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 文件上传服务接口
 * @date 2025/10/25
 */
public interface FileUploadService {

    /**
     * 上传单个文件
     * @param file 上传的文件
     * @return 文件信息
     */
    Map<String, Object> uploadFile(MultipartFile file);

    /**
     * 上传多个文件
     * @param files 上传的文件数组
     * @return 上传结果
     */
    Map<String, Object> uploadMultipleFiles(MultipartFile[] files);

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 删除结果
     */
    boolean deleteFile(String fileName);

    /**
     * 获取文件信息
     * @param fileName 文件名
     * @return 文件信息
     */
    Map<String, Object> getFileInfo(String fileName);

    /**
     * 检查文件是否存在
     * @param fileName 文件名
     * @return 是否存在
     */
    boolean fileExists(String fileName);
}
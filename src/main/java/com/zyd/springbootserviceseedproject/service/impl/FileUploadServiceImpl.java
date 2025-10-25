package com.zyd.springbootserviceseedproject.service.impl;

import com.zyd.springbootserviceseedproject.config.FileUploadConfig;
import com.zyd.springbootserviceseedproject.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 文件上传服务实现类
 * @date 2025/10/25
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 验证文件
            String validationResult = validateFile(file);
            if (validationResult != null) {
                result.put("success", false);
                result.put("message", validationResult);
                return result;
            }

            // 获取文件信息
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFileName = generateUniqueFileName(fileExtension);

            // 创建上传目录
            String uploadDir = createUploadDirectory();

            // 构建文件保存路径
            String filePath = uploadDir + File.separator + newFileName;

            // 保存文件
            File dest = new File(filePath);
            file.transferTo(dest);

            // 构建返回数据
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("originalName", originalFilename);
            result.put("newName", newFileName);
            result.put("fileSize", file.getSize());
            result.put("fileType", fileExtension);
            result.put("filePath", filePath);
            result.put("fileUrl", "/uploads/" + newFileName);

            log.info("文件上传成功: {} -> {}", originalFilename, newFileName);

            return result;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> uploadMultipleFiles(MultipartFile[] files) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (files == null || files.length == 0) {
                result.put("success", false);
                result.put("message", "请选择要上传的文件");
                return result;
            }

            // 验证文件数量
            Integer maxFileCount = fileUploadConfig.getMaxCount();
            if (maxFileCount != null && files.length > maxFileCount) {
                result.put("success", false);
                result.put("message", "一次最多上传" + maxFileCount + "个文件");
                return result;
            }

            // 创建上传目录
            String uploadDir = createUploadDirectory();

            result.put("totalCount", files.length);
            result.put("successCount", 0);
            result.put("failedCount", 0);
            result.put("successFiles", new java.util.ArrayList<Map<String, Object>>());
            result.put("failedFiles", new java.util.ArrayList<Map<String, String>>());

            for (MultipartFile file : files) {
                Map<String, Object> fileResult = uploadSingleFile(file, uploadDir);

                if ((Boolean) fileResult.get("success")) {
                    ((java.util.ArrayList<Map<String, Object>>) result.get("successFiles")).add(fileResult);
                    result.put("successCount", (Integer) result.get("successCount") + 1);
                } else {
                    Map<String, String> failedInfo = new HashMap<>();
                    String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "未知文件";
                    failedInfo.put("fileName", fileName);
                    failedInfo.put("reason", (String) fileResult.get("message"));
                    ((java.util.ArrayList<Map<String, String>>) result.get("failedFiles")).add(failedInfo);
                    result.put("failedCount", (Integer) result.get("failedCount") + 1);
                }
            }

            result.put("success", true);
            result.put("message", "文件上传完成");

            return result;
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            result.put("success", false);
            result.put("message", "批量文件上传失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public boolean deleteFile(String fileName) {
        try {
            String uploadDir = createUploadDirectory();
            String filePath = uploadDir + File.separator + fileName;

            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("文件删除成功: {}", fileName);
                } else {
                    log.warn("文件删除失败: {}", fileName);
                }
                return deleted;
            } else {
                log.warn("文件不存在: {}", fileName);
                return false;
            }
        } catch (Exception e) {
            log.error("删除文件时发生错误: {}", fileName, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getFileInfo(String fileName) {
        Map<String, Object> fileInfo = new HashMap<>();

        try {
            String uploadDir = createUploadDirectory();
            String filePath = uploadDir + File.separator + fileName;

            File file = new File(filePath);
            if (file.exists()) {
                Path path = Paths.get(filePath);

                fileInfo.put("exists", true);
                fileInfo.put("fileName", fileName);
                fileInfo.put("fileSize", file.length());
                fileInfo.put("lastModified", file.lastModified());
                fileInfo.put("fileType", getFileExtension(fileName));
                fileInfo.put("filePath", filePath);
                fileInfo.put("fileUrl", "/uploads/" + fileName);
            } else {
                fileInfo.put("exists", false);
                fileInfo.put("message", "文件不存在");
            }
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", fileName, e);
            fileInfo.put("exists", false);
            fileInfo.put("message", "获取文件信息失败: " + e.getMessage());
        }

        return fileInfo;
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            String uploadDir = createUploadDirectory();
            String filePath = uploadDir + File.separator + fileName;
            return new File(filePath).exists();
        } catch (Exception e) {
            log.error("检查文件是否存在时发生错误: {}", fileName, e);
            return false;
        }
    }

    /**
     * 验证文件
     * @param file 上传的文件
     * @return 验证失败返回错误信息，验证成功返回null
     */
    private String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "文件不能为空";
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "文件名不能为空";
        }

        // 检查文件大小
        Long maxSize = fileUploadConfig.getMaxSize();
        if (maxSize != null && file.getSize() > maxSize) {
            return "文件大小不能超过" + (maxSize / 1024 / 1024) + "MB";
        }

        // 检查文件类型
        String allowedTypes = fileUploadConfig.getAllowedTypes();
        if (allowedTypes != null && !allowedTypes.isEmpty()) {
            String fileExtension = getFileExtension(originalFilename).toLowerCase();
            String[] allowedTypeArray = allowedTypes.split(",");
            if (!Arrays.asList(allowedTypeArray).contains(fileExtension)) {
                return "不支持的文件类型: " + fileExtension;
            }
        }

        return null;
    }

    /**
     * 上传单个文件
     * @param file 上传的文件
     * @param uploadDir 上传目录
     * @return 上传结果
     */
    private Map<String, Object> uploadSingleFile(MultipartFile file, String uploadDir) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 验证文件
            String validationResult = validateFile(file);
            if (validationResult != null) {
                result.put("success", false);
                result.put("message", validationResult);
                return result;
            }

            // 获取文件信息
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFileName = generateUniqueFileName(fileExtension);

            // 构建文件保存路径
            String filePath = uploadDir + File.separator + newFileName;

            // 保存文件
            File dest = new File(filePath);
            file.transferTo(dest);

            // 构建返回数据
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("originalName", originalFilename);
            result.put("newName", newFileName);
            result.put("fileSize", file.getSize());
            result.put("fileType", fileExtension);
            result.put("filePath", filePath);
            result.put("fileUrl", "/uploads/" + newFileName);

            log.info("文件上传成功: {} -> {}", originalFilename, newFileName);

            return result;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 文件扩展名（包含点）
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * 生成唯一的文件名
     * @param fileExtension 文件扩展名
     * @return 唯一的文件名
     */
    private String generateUniqueFileName(String fileExtension) {
        return UUID.randomUUID().toString() + fileExtension;
    }

    /**
     * 创建上传目录
     * @return 上传目录路径
     * @throws IOException 如果目录创建失败
     */
    private String createUploadDirectory() throws IOException {
        // 从配置中获取上传目录
        String uploadDir = fileUploadConfig.getPath();
        if (uploadDir == null || uploadDir.isEmpty()) {
            // 如果配置中没有设置，使用默认目录
            String projectRoot = System.getProperty("user.dir");
            uploadDir = projectRoot + File.separator + "uploads";
        }

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("无法创建上传目录: " + uploadDir);
            }
        }

        return uploadDir;
    }
}
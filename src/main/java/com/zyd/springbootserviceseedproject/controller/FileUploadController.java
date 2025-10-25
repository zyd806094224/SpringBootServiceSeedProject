package com.zyd.springbootserviceseedproject.controller;

import com.zyd.springbootserviceseedproject.common.Result;
import com.zyd.springbootserviceseedproject.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 文件上传Controller
 * @date 2025/10/25
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 单文件上传
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = fileUploadService.uploadFile(file);

        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.fail(400, (String) result.get("message"));
        }
    }

    /**
     * 多文件上传
     * @param files 上传的文件数组
     * @return 上传结果
     */
    @PostMapping("/uploadMultiple")
    public Result uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> result = fileUploadService.uploadMultipleFiles(files);

        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.fail(400, (String) result.get("message"));
        }
    }

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 删除结果
     */
    @DeleteMapping("/delete/{fileName}")
    public Result deleteFile(@PathVariable String fileName) {
        boolean deleted = fileUploadService.deleteFile(fileName);
        if (deleted) {
            return Result.success("文件删除成功");
        } else {
            return Result.fail(400, "文件删除失败");
        }
    }

    /**
     * 获取文件信息
     * @param fileName 文件名
     * @return 文件信息
     */
    @GetMapping("/info/{fileName}")
    public Result getFileInfo(@PathVariable String fileName) {
        Map<String, Object> fileInfo = fileUploadService.getFileInfo(fileName);
        return Result.success(fileInfo);
    }

    /**
     * 检查文件是否存在
     * @param fileName 文件名
     * @return 是否存在
     */
    @GetMapping("/exists/{fileName}")
    public Result fileExists(@PathVariable String fileName) {
        boolean exists = fileUploadService.fileExists(fileName);
        Map<String, Object> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("exists", exists);
        return Result.success(result);
    }
}
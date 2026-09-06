package com.example.music.controller;

import com.example.music.controller.vo.BaseVo;
import com.example.music.controller.vo.UploadVo;
import com.example.music.util.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 文件上传（音频 / 封面等），返回可访问的 URL
     */
    @PostMapping("/upload")
    public UploadVo upload(@RequestParam("file") MultipartFile file) {
        long start = System.currentTimeMillis();
        long end;
        UploadVo uploadVo = new UploadVo();

        try {
            if (file == null || file.isEmpty()) {
                end = System.currentTimeMillis();
                uploadVo.setBaseVo(new BaseVo(500, end - start, false, "上传文件不能为空"));
                return uploadVo;
            }

            String fileName = buildFileName(file.getOriginalFilename());
            String url = minioUtil.uploadFile(file, fileName);

            end = System.currentTimeMillis();
            uploadVo.setUrl(url);
            uploadVo.setBaseVo(new BaseVo(200, end - start, true, null));
            return uploadVo;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            end = System.currentTimeMillis();
            uploadVo.setBaseVo(new BaseVo(500, end - start, false, "文件上传失败"));
            return uploadVo;
        }
    }

    /**
     * 生成唯一对象名，保留原始扩展名，避免同名覆盖
     */
    private String buildFileName(String originalFilename) {
        String suffix = "";
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0) {
                suffix = originalFilename.substring(dot);
            }
        }
        return UUID.randomUUID().toString().replace("-", "") + suffix;
    }
}

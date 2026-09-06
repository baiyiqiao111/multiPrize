package com.example.music.util;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinioUtil {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.domain}")
    private String domain;

    /**
     * 应用启动时确保默认桶存在且为公开可读，
     * 从而修复此前已上传但因桶私有而无法播放的音频。
     */
    @PostConstruct
    public void init() {
        try {
            createBucketIfNotExist(bucketName);
        } catch (Exception e) {
            log.error("初始化 MinIO 桶（{}）失败", bucketName, e);
        }
    }

    /**
     * 检查桶是否存在，不存在则创建；并确保桶为公开可读，
     * 否则通过 domain 拼接出的直链在浏览器/&lt;audio&gt; 中会因权限被拒无法播放。
     */
    public void createBucketIfNotExist(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        // 幂等地设置匿名只读策略，既覆盖新建桶，也修复此前已创建的私有桶
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(publicReadPolicy(bucketName))
                        .build()
        );
    }

    /**
     * 生成允许匿名 GetObject（读取/下载）的桶策略 JSON
     */
    private String publicReadPolicy(String bucketName) {
        return "{"
                + "\"Version\":\"2012-10-17\","
                + "\"Statement\":[{"
                + "\"Effect\":\"Allow\","
                + "\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:GetObject\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]"
                + "}]}";
    }

    /**
     * 文件上传
     */
    public String uploadFile(MultipartFile file, String fileName) throws Exception {
        String bucket = bucketName;
        createBucketIfNotExist(bucket);

        InputStream inputStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        inputStream.close();
        return domain + "/" + bucket + "/" + fileName;
    }

    /**
     * 获取临时访问URL
     */
    public String getPresignedUrl(String fileName, int expireMinutes) throws Exception {
        String bucket = bucketName;
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .method(Method.GET)
                        .expiry(expireMinutes, TimeUnit.MINUTES)
                        .build()
        );
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    /**
     * 下载文件流
     */
    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
}
package com.bruno.artistalbum.service;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url}")
    private String publicUrl;

    /**
     * Faz upload de um arquivo para o MinIO e retorna apenas o nome do arquivo.
     * A URL pré-assinada deve ser gerada posteriormente via getPresignedUrl().
     */
    public String uploadFile(String filename, InputStream content, String contentType) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(content, -1, 10485760) // 10MB part size
                            .contentType(contentType)
                            .build());

            // Retorna apenas o nome do arquivo (não a URL completa)
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    /**
     * Gera uma URL pré-assinada com expiração para acesso temporário ao arquivo.
     * 
     * @param filename      Nome do arquivo no bucket
     * @param expiryMinutes Tempo de expiração em minutos (padrão: 30 minutos)
     * @return URL pré-assinada válida por expiryMinutes
     */
    public String getPresignedUrl(String filename, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filename)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL for file: " + filename, e);
        }
    }

    /**
     * Gera uma URL pré-assinada com expiração padrão de 30 minutos.
     */
    public String getPresignedUrl(String filename) {
        return getPresignedUrl(filename, 30);
    }
}
